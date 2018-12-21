package com.example.cchiv.jiggles.player.tools;

import android.util.Log;

import com.example.cchiv.jiggles.model.Track;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class DataFetcher extends Thread {

    private static final String TAG = "DataFetcher";

    private Track track;

    private PipedInputStream pipedInputStream = new PipedInputStream();
    private PipedOutputStream pipedOutputStream = new PipedOutputStream();

    public DataFetcher() {
        try {
            pipedInputStream.connect(this.pipedOutputStream);
        } catch(IOException e) {
            Log.v(TAG, e.toString());
        }
    }

    public DataFetcher(Track track) {
        this();

        this.track = track;
    }

    public PipedInputStream getPipedInputStream() {
        return pipedInputStream;
    }

    public void write(byte[] data, int offset, int len) {
        try {
            pipedOutputStream.write(data, offset, len);
        } catch(IOException e) {
            Log.v(TAG, e.toString());
        }
    }

    public void write(byte[] data) {
        try {
            pipedOutputStream.write(data);
        } catch(IOException e) {
            Log.v(TAG, e.toString());
        }
    }

    @Override
    public void run() {
        File file = new File(track.getUri());

        try {
            FileInputStream fileInputStream = new FileInputStream(file);

            byte[] data = new byte[1024];

            int len = fileInputStream.read(data, 0, 1024);
            while(len != -1) {
                write(data, 0, len);

                len = fileInputStream.read(data, 0, 1024);
            }

            pipedOutputStream.close();

            fileInputStream.close();
        } catch(FileNotFoundException e) {
            Log.v(TAG, e.toString());
        } catch(IOException e) {
            Log.v(TAG, e.toString());
        }
    }
}