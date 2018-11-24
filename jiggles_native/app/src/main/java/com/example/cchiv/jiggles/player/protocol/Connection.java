package com.example.cchiv.jiggles.player.protocol;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Connection {

    private HashMap<String, Message> messages = new HashMap<>();

    public Connection() {}

    public Message createMessage(Message.OnMessageCallback onMessageCallback) {
        String identifier;
        do {
            identifier = Message.generateIdentifier();
        } while(messages.containsKey(identifier));

        Message message = new Message(identifier);
        message.onAttachMessageCallback(onMessageCallback);
        messages.put(identifier, message);

        return message;
    };

    public void resolve(Chunk chunk) {
        String identifier = chunk.getHeader(RemoteProtocol.IDENTIFIER.HEADER);

        if(identifier != null) {
            if(messages.containsKey(identifier)) {
                Message message = messages.get(identifier);

                message.resolve(chunk);
            }
        }
    }

    public static class Message {

        private static final String TAG = "Notification";

        public interface OnMessageCallback {
            void onMessageCallback(Message message);
        }

        private OnMessageCallback onMessageCallback;

        // Time off ms window where message is still in pending mode
        private static final int MESSAGE_TIME_OFF = 1000000;

        // Identifier that uniquely identifies a message
        private String identifier = null;

        List<Chunk> chunks = new ArrayList<>();

        private Message(String identifier) {
            this.identifier = identifier;
        }

        private Message(String identifier, Chunk chunk) {
            this(identifier);

            resolve(chunk);
        }

        public static String generateIdentifier() {
            return UUID.randomUUID().toString();
        }

        private void onAttachMessageCallback(OnMessageCallback onMessageCallback) {
            this.onMessageCallback = onMessageCallback;
        }

        private void resolve(Chunk chunk) {
            int chunkSize = Integer.valueOf(chunk.getHeader(RemoteProtocol.CHUNKS.LENGTH.HEADER));

            if(chunkSize == RemoteProtocol.CHUNKS.LENGTH.UNKNOWN_STREAM_END) {
                if(this.onMessageCallback != null)
                    onMessageCallback.onMessageCallback(this);
            } else {
                chunks.add(chunk);
            }
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

        public List<Chunk> getChunks() {
            return chunks;
        }

        public String getIdentifier() {
            return this.identifier;
        }

        public static byte[] decodeByteSource(Message message) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            try {
                for(Chunk chunk: message.chunks) {
                    byteArrayOutputStream.write(chunk.body);
                }
            } catch(IOException e) {
                Log.v(TAG,  e.toString());
            }

            return byteArrayOutputStream.toByteArray();
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
