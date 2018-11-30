package com.example.cchiv.jiggles.player.protocol;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Connection {

    private static final String TAG = "Connection";

    private HashMap<String, Message> messages = new HashMap<>();

    public interface OnConnectionCallback {
        void onConnectionImageCallback(Bitmap bitmap);
    }

    private OnConnectionCallback onConnectionCallback = null;

    public Connection(OnConnectionCallback onConnectionCallback) {
        this.onConnectionCallback = onConnectionCallback;
    }

    public Message createMessage(String identifier, Message.OnMessageCallback onMessageCallback) {
        Message message = new Message(identifier);
        message.onAttachMessageCallback(onMessageCallback);
        messages.put(identifier, message);

        return message;
    };

    public void resolve(Chunk chunk) {
        String identifier = chunk.getHeader(RemoteProtocol.IDENTIFIER.HEADER);

        int chunkSize = Integer.valueOf(chunk.getHeader(RemoteProtocol.CHUNKS.LENGTH.HEADER));

        Log.v(TAG, String.valueOf(chunkSize));

        if(identifier != null) {
            if(messages.containsKey(identifier)) {
                Message message = messages.get(identifier);

                message.resolve(chunk);
            } else {
                Message.OnMessageCallback onMessageCallback = message -> {
                    String action = chunk.getHeader(RemoteProtocol.ACTIONS.HEADER);
                    switch(action) {
                        case RemoteProtocol.ACTIONS.ACTION_STREAM : {
                            Bitmap bitmap = Message.decodeStreamImageMessage(message);

                            onConnectionCallback.onConnectionImageCallback(bitmap);
                        }
                        default : {
                            Log.v(TAG, "Action unknown");
                        }
                    }
                };

                Message message = createMessage(identifier, onMessageCallback);
                message.resolve(chunk);
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

        public static void encodeStreamMessage(RemoteConnection remoteConnection, Bitmap bitmap) {
            byte[] data = new byte[248];
            int len;

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
            byte[] input = bos.toByteArray();
            ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(input);

            String identifier = Message.generateIdentifier();

            while((len = arrayInputStream.read(data, 0, 248)) != -1) {
                sendMessage(remoteConnection, identifier, data, RemoteProtocol.CHUNKS.LENGTH.UNKNOWN_STREAM_LENGTH, len);
            }

            sendMessage(remoteConnection, identifier, null, RemoteProtocol.CHUNKS.LENGTH.UNKNOWN_STREAM_END, 0);
        }

        public static Bitmap decodeStreamImageMessage(Message message) {
            byte[] body = Connection.Message.decodeByteSource(message);

            return BitmapFactory.decodeByteArray(body, 0, body.length);
        }

        private static void sendMessage(RemoteConnection remoteConnection, String identifier, byte[] data, int size, int length) {
            byte[] streamAction = RemoteProtocol.createStreamAction(identifier, data, size, length);

            remoteConnection.writeStream(streamAction);
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

        public void printHeaders() {
            for(Map.Entry<String, String> stringMap : headers.entrySet()) {
                Log.v(TAG, stringMap.getKey() + " - " + stringMap.getValue());
            }
        }
    }
}
