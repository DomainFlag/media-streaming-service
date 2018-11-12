package com.example.cchiv.jiggles.player.protocol;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Connection {

    private HashMap<String, Message> messages = new HashMap<>();

    public Connection() {}

    public void resolve(Message.OnMessageCallback onMessageCallback, Chunk chunk) {
        String identifier = chunk.getHeader(RemoteProtocol.IDENTIFIER.HEADER);
        String chunkSize = chunk.getHeader(RemoteProtocol.CHUNKS.LENGTH.HEADER);

        if(identifier != null && chunkSize != null) {
            if(!messages.containsKey(identifier)) {
                Message message = new Message(identifier, Integer.valueOf(chunkSize));
                message.onAttachMessageCallback(onMessageCallback);
                message.resolve(chunk);

                messages.put(identifier, message);
            } else {
                messages.get(identifier).resolve(chunk);
            }
        }
    }


    public static class Message {

        private static final String TAG = "Message";

        public interface OnMessageCallback {
            void onMessageCallback(Message message);
        }

        private OnMessageCallback onMessageCallback;

        // Time off ms window where message is still in pending mode
        private static final int MESSAGE_TIME_OFF = 1000000;

        // Identifier that uniquely identifies a message
        private String identifier = null;

        private int chunkSize = -1;

        List<Chunk> chunks = new ArrayList<>();

        private Message(String identifier, int chunkSize) {
            this.identifier = identifier;
            this.chunkSize = chunkSize;
        }

        private Message(String identifier, int chunkSize, Chunk chunk) {
            this(identifier, chunkSize);

            resolve(chunk);
        }

        private void onAttachMessageCallback(OnMessageCallback onMessageCallback) {
            this.onMessageCallback = onMessageCallback;
        }

        private void resolve(Chunk chunk) {
            chunks.add(chunk);

            if(chunks.size() == chunkSize)
                onMessageCallback.onMessageCallback(this);
        }

        public static String decodeString(Message message) {
            StringBuilder stringBuilder = new StringBuilder();

            try {
                for(Chunk chunk: message.chunks) {
                    String data = new String(chunk.getBody(), "ISO-8859-1");

                    stringBuilder.append(data);
                }
            } catch(IOException e) {
                Log.v(TAG, e.toString());
            }


            return stringBuilder.toString();
        }

        public byte[] decodeAudioSource(int position) {
            return chunks.get(position).getBody();
        }

        public List<Chunk> getChunks() {
            return chunks;
        }

        public static Bitmap decodeImage(Message message) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            try {
                for(Chunk chunk: message.chunks)
                    byteArrayOutputStream.write(chunk.body);
            } catch(IOException e) {
                Log.v(TAG,  e.toString());
            }

            byte[] raw = byteArrayOutputStream.toByteArray();

            return BitmapFactory.decodeByteArray(raw, 0, raw.length);
        }
    }

    public static class Chunk {

        private HashMap<String, String> headers;
        private byte[] body;

        public Chunk(HashMap<String, String> headers, byte[] body) {
            this.headers = headers;
            this.body = body;
        };

        public HashMap<String, String> getHeaders() {
            return headers;
        }

        public byte[] getBody() {
            return body;
        }

        public String getHeader(String header) {
            if(headers.containsKey(header))
                return headers.get(header);
            else return null;
        }
    }
}
