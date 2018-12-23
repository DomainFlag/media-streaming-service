package com.example.cchiv.jiggles.prediction;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Speech {

    private static final String TAG = "Speech";

    public interface OnSpeechResultCallback {
        void onSpeechResultCallback(String action, int position);
    }

    private static final int SAMPLE_RATE = 16000;
    private static final int SAMPLE_DURATION_MS = 1000;
    private static final int RECORDING_LENGTH = (int) (SAMPLE_RATE * SAMPLE_DURATION_MS / 1000);
    private static final long AVERAGE_WINDOW_DURATION_MS = 500;
    private static final float DETECTION_THRESHOLD = 0.70f;
    private static final int SUPPRESSION_MS = 1500;
    private static final int MINIMUM_COUNT = 3;
    private static final long MINIMUM_TIME_BETWEEN_SAMPLES_MS = 30;

    private static final String LABEL_FILENAME = "file:///android_asset/models/conv_actions_labels.txt";
    private static final String MODEL_FILENAME = "file:///android_asset/models/conv_actions_frozen.pb";
    private static final String INPUT_DATA_NAME = "decoded_sample_data:0";
    private static final String SAMPLE_RATE_NAME = "decoded_sample_data:1";
    private static final String OUTPUT_SCORES_NAME = "labels_softmax";

    // Class related variables
    private static final int REQUEST_RECORD_AUDIO = 13;

    private OnSpeechResultCallback onSpeechResultCallback;

    private Context context;

    // Working variables
    private short[] recordingBuffer = new short[RECORDING_LENGTH];
    private int recordingOffset = 0;
    private boolean shouldContinue = true;
    private Thread recordingThread = null;
    private boolean shouldContinueRecognition = true;
    private Thread recognitionThread;
    private final ReentrantLock recordingBufferLock = new ReentrantLock();
    private TensorFlowInferenceInterface inferenceInterface;
    private List<String> labels = new ArrayList<>();
    private RecognizeCommands recognizeCommands;

    public Speech(Context context, OnSpeechResultCallback onSpeechResultCallback) {
        this.context = context;
        this.onSpeechResultCallback = onSpeechResultCallback;
    }

    public void initiate() {
        // AssetManager to retrieve model & labels
        AssetManager assetManager = context.getAssets();

        // Load the labels for the model
        String actualFilename = LABEL_FILENAME.split("file:///android_asset/")[1];

        Log.v(TAG, actualFilename);

        // Reading each label
        try {
            InputStream inputStream = assetManager.open(actualFilename);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            while((line = bufferedReader.readLine()) != null) {
                labels.add(line);

                Log.v(TAG, line);
            }

            bufferedReader.close();
        } catch(IOException e) {
            throw new RuntimeException("Problem reading label file!", e);
        }

        // Set up an object to smooth recognition results to increase accuracy
        recognizeCommands =
                new RecognizeCommands(
                        labels,
                        AVERAGE_WINDOW_DURATION_MS,
                        DETECTION_THRESHOLD,
                        SUPPRESSION_MS,
                        MINIMUM_COUNT,
                        MINIMUM_TIME_BETWEEN_SAMPLES_MS);

        // Load the TensorFlow model
        inferenceInterface = new TensorFlowInferenceInterface(assetManager, MODEL_FILENAME);

        // Start the recording and recognition threads.
        requestMicrophonePermission();
        startRecording();
        startRecognition();
    }

    private void requestMicrophonePermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ((Activity) context).requestPermissions(
                    new String[] { android.Manifest.permission.RECORD_AUDIO }, REQUEST_RECORD_AUDIO);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == REQUEST_RECORD_AUDIO && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startRecording();
            startRecognition();
        }
    }

    private synchronized void startRecording() {
        if(recordingThread != null) {
            return;
        }

        shouldContinue = true;

        recordingThread = new Thread(this::record);
        recordingThread.start();
    }

    private synchronized void stopRecording() {
        if(recordingThread == null) {
            return;
        }

        shouldContinue = false;
        recordingThread = null;
    }

    private void record() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

        // Estimate the buffer size we'll need for this device.
        int bufferSize =
                AudioRecord.getMinBufferSize(
                        SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

        if(bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2;
        }

        short[] audioBuffer = new short[bufferSize / 2];

        AudioRecord record =
                new AudioRecord(
                        MediaRecorder.AudioSource.DEFAULT,
                        SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize);

        if(record.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.v(TAG, "Unable to audio record");

            return;
        }

        record.startRecording();

        Log.v(TAG, "Audio recording started");

        // Loop, gathering audio data and copying it to a round-robin buffer.
        while(shouldContinue) {
            int numberRead = record.read(audioBuffer, 0, audioBuffer.length);
            int maxLength = recordingBuffer.length;
            int newRecordingOffset = recordingOffset + numberRead;
            int secondCopyLength = Math.max(0, newRecordingOffset - maxLength);
            int firstCopyLength = numberRead - secondCopyLength;

            // We store off all the data for the recognition thread to access. The ML
            // thread will copy out of this buffer into its own, while holding the
            // lock, so this should be thread safe.
            recordingBufferLock.lock();
            try {
                System.arraycopy(audioBuffer, 0, recordingBuffer, recordingOffset, firstCopyLength);
                System.arraycopy(audioBuffer, firstCopyLength, recordingBuffer, 0, secondCopyLength);

                recordingOffset = newRecordingOffset % maxLength;
            } finally {
                recordingBufferLock.unlock();
            }
        }

        record.stop();
        record.release();
    }

    private synchronized void startRecognition() {
        if(recognitionThread != null) {
            return;
        }

        shouldContinueRecognition = true;

        recognitionThread = new Thread(this::recognize);
        recognitionThread.start();
    }

    private synchronized void stopRecognition() {
        if(recognitionThread == null) {
            return;
        }

        shouldContinueRecognition = false;
        recognitionThread = null;
    }

    public void release() {
        stopRecognition();
        stopRecording();
    }

    private void recognize() {
        Log.v(TAG, "Start recognition");

        short[] inputBuffer = new short[RECORDING_LENGTH];
        float[] floatInputBuffer = new float[RECORDING_LENGTH];
        float[] outputScores = new float[labels.size()];
        String[] outputScoresNames = new String[] {OUTPUT_SCORES_NAME};
        int[] sampleRateList = new int[] {SAMPLE_RATE};

        // Loop, grabbing recorded data and running the recognition model on it.
        while(shouldContinueRecognition) {
            // The recording thread places data in this round-robin buffer, so lock to
            // make sure there's no writing happening and then copy it to our own
            // local version.
            recordingBufferLock.lock();

            try {
                int maxLength = recordingBuffer.length;
                int firstCopyLength = maxLength - recordingOffset;
                int secondCopyLength = recordingOffset;
                System.arraycopy(recordingBuffer, recordingOffset, inputBuffer, 0, firstCopyLength);
                System.arraycopy(recordingBuffer, 0, inputBuffer, firstCopyLength, secondCopyLength);
            } finally {
                recordingBufferLock.unlock();
            }

            // We need to feed in float values between -1.0f and 1.0f, so divide the
            // signed 16-bit inputs.
            for (int i = 0; i < RECORDING_LENGTH; i++) {
                floatInputBuffer[i] = inputBuffer[i] / 32767.0f;
            }

            // Run the model.
            inferenceInterface.feed(SAMPLE_RATE_NAME, sampleRateList);
            inferenceInterface.feed(INPUT_DATA_NAME, floatInputBuffer, RECORDING_LENGTH, 1);
            inferenceInterface.run(outputScoresNames);
            inferenceInterface.fetch(OUTPUT_SCORES_NAME, outputScores);

            // Use the smoother to figure out if we've had a real recognition event.
            long currentTime = System.currentTimeMillis();
            RecognizeCommands.RecognitionResult result =
                    recognizeCommands.processLatestResults(outputScores, currentTime);

            ((Activity) context).runOnUiThread(() -> {
                // If we do have a new command, highlight the right list entry.
                if(!result.foundCommand.startsWith("_") && result.isNewCommand) {
                    int labelIndex = -1;
                    for(int i = 0; i < labels.size(); ++i) {
                        if(labels.get(i).equals(result.foundCommand)) {
                            labelIndex = i;
                        }
                    }

                    int labelInd = labelIndex;
                    String action = labels.get(labelIndex);

                    ((Activity) context).runOnUiThread(() ->
                            onSpeechResultCallback.onSpeechResultCallback(action, labelInd));
                }
            });

            try {
                // We don't need to run too frequently, so snooze for a bit.
                Thread.sleep(MINIMUM_TIME_BETWEEN_SAMPLES_MS);
            } catch(InterruptedException e) {
                Log.v(TAG, e.toString());
            }
        }

        Log.v(TAG, "End recognition");
    }
}