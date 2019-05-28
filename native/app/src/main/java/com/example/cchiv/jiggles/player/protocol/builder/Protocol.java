package com.example.cchiv.jiggles.player.protocol.builder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.cchiv.jiggles.model.player.content.Track;
import com.example.cchiv.jiggles.player.protocol.RemoteConnection;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

public class Protocol {

    private static final String TAG = "Protocol";

    public static final int PACKET_SIZE = 16192;
    public static final int BODY_SIZE = 15650;

    public static class IDENTIFIER {
        public static final String HEADER = "identifier-value";
    }

    public static class ACTIONS {
        public static final String HEADER = "action-type";

        public static final String ACTION_STREAM = "stream";
        public static final String ACTION_METADATA = "metadata";
        public static final String ACTION_SEEK = "seek";
        public static final String ACTION_PAUSE = "pause";
        public static final String ACTION_RESUME = "resume";
    }

    public static class PACKET {
        public static final String HEADER = "packet-size";
    }

    public static class CHUNKS {
        public static class LENGTH {
            public static final String HEADER = "packets-length";

            public static final int UNKNOWN_STREAM_LENGTH = -2;
            public static final int UNKNOWN_STREAM_END = -1;
        }
    }

    public static class CONTENT {
        public static class TYPE {
            public static final String HEADER = "content-type";

            public static final String TYPE_TEXT = "text";
            public static final String TYPE_RAW = "raw";
            public static final String TYPE_AUDIO = "audio";
            public static final String TYPE_JSON = "json";
            public static final String TYPE_NONE = "none";
        }

        public static class LENGTH {
            public static final String HEADER = "content-length";
        }
    }

    private static final Gson gson = new Gson();

    /**
     * Resume Action
     */
    public static byte[] createResumeAction(String identifier) {
        Builder builder = new Builder();

        return builder
                .appendHeader(ACTIONS.HEADER, ACTIONS.ACTION_RESUME)
                .appendHeader(IDENTIFIER.HEADER, identifier)
                .appendHeader(CHUNKS.LENGTH.HEADER, 1)
                .appendHeader(CONTENT.TYPE.HEADER, CONTENT.TYPE.TYPE_NONE)
                .build();
    }

    /**
     * Seek Action
     */
    public static byte[] createSeekAction(String identifier, long value) {
        Builder builder = new Builder();

        return builder
                .appendHeader(ACTIONS.HEADER, ACTIONS.ACTION_SEEK)
                .appendHeader(IDENTIFIER.HEADER, identifier)
                .appendHeader(CHUNKS.LENGTH.HEADER, 1)
                .appendHeader(CONTENT.TYPE.HEADER, CONTENT.TYPE.TYPE_TEXT)
                .appendBody(value).build();
    }

    /**
     * Pause Action
     */
    public static byte[] createPauseAction(String identifier) {
        Builder builder = new Builder();

        return builder
                .appendHeader(ACTIONS.HEADER, ACTIONS.ACTION_PAUSE)
                .appendHeader(IDENTIFIER.HEADER, identifier)
                .appendHeader(CHUNKS.LENGTH.HEADER, 1)
                .appendHeader(CONTENT.TYPE.HEADER, CONTENT.TYPE.TYPE_NONE)
                .build();
    }

    /**
     * JSON Action
     */
    public static byte[] createJSONAction(String identifier, String body) {
        Builder builder = new Builder();

        return builder
                .appendHeader(ACTIONS.HEADER, ACTIONS.ACTION_METADATA)
                .appendHeader(IDENTIFIER.HEADER, identifier)
                .appendHeader(CHUNKS.LENGTH.HEADER, 1)
                .appendHeader(CONTENT.TYPE.HEADER, CONTENT.TYPE.TYPE_JSON)
                .appendBody(body)
                .build();
    }

    /**
     * Stream Action
     */
    public static byte[] createStreamAction(String identifier, byte[] data, int size, int length) {
        Builder builder = new Builder();

        return builder
                .appendHeader(ACTIONS.HEADER, ACTIONS.ACTION_STREAM)
                .appendHeader(IDENTIFIER.HEADER, identifier)
                .appendHeader(CHUNKS.LENGTH.HEADER, size)
                .appendHeader(CONTENT.TYPE.HEADER, CONTENT.TYPE.TYPE_RAW)
                .appendBody(data, length)
                .build();
    }

    /**
     * Stream Audio Action
     */
    public static byte[] createStreamAudioAction(String identifier, byte[] data, int size, int length) {
        Builder builder = new Builder();

        return builder
                .appendHeader(ACTIONS.HEADER, ACTIONS.ACTION_STREAM)
                .appendHeader(IDENTIFIER.HEADER, identifier)
                .appendHeader(CHUNKS.LENGTH.HEADER, size)
                .appendHeader(CONTENT.TYPE.HEADER, CONTENT.TYPE.TYPE_AUDIO)
                .appendBody(data, length)
                .build();
    }

    /********************************************************/
    private static void sendMessage(RemoteConnection remoteConnection, String identifier, byte[] data, int size, int length) {
        byte[] streamAction = Protocol.createStreamAction(identifier, data, size, length);

        remoteConnection.writeStream(streamAction);
    }

    public static void encodeJSONMessage(RemoteConnection remoteConnection, Track track) {
        String identifier = Message.generateIdentifier();
        String json = gson.toJson(track, Track.class);

        byte[] messageAction = Protocol.createJSONAction(identifier, json);

        remoteConnection.writeStream(messageAction);
    }

    public static void encodeStreamMessage(RemoteConnection remoteConnection, Bitmap bitmap) {
        byte[] data = new byte[BODY_SIZE];
        int len;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);

        byte[] input = bos.toByteArray();
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(input);

        String identifier = Message.generateIdentifier();
        while((len = arrayInputStream.read(data, 0, BODY_SIZE)) != -1) {
            sendMessage(remoteConnection, identifier, data, Protocol.CHUNKS.LENGTH.UNKNOWN_STREAM_LENGTH, len);
        }

        sendMessage(remoteConnection, identifier, data, Protocol.CHUNKS.LENGTH.UNKNOWN_STREAM_END, 0);
    }

    public static void encodeStreamAudioMessage(RemoteConnection remoteConnection, String uri) {
        File file = new File(uri);

        try {
            FileInputStream fileInputStream = new FileInputStream(file);

            byte[] data = new byte[BODY_SIZE];
            int len;

            String identifier = Message.generateIdentifier();
            while((len = fileInputStream.read(data, 0, BODY_SIZE)) != -1) {
                remoteConnection.writeStream(Protocol.createStreamAudioAction(identifier, data,
                        Protocol.CHUNKS.LENGTH.UNKNOWN_STREAM_LENGTH, len));
            }

            remoteConnection.writeStream(Protocol.createStreamAudioAction(identifier, data,
                    Protocol.CHUNKS.LENGTH.UNKNOWN_STREAM_END, 0));

            fileInputStream.close();
        } catch(FileNotFoundException e) {
            Log.v(TAG, e.toString());
        } catch(IOException e) {
            Log.v(TAG, e.toString());
        }
    }

    public static Track decodeJSONTrackMessage(Message message) {
        byte[] body = Message.decodeByteSource(message);

        Type type = new TypeToken<Track>() {}.getType();
        try {
            String data = new String(body, "ISO-8859-1");

            return gson.fromJson(data, type);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap decodeStreamImageMessage(Message message) {
        byte[] body = Message.decodeByteSource(message);

        return BitmapFactory.decodeByteArray(body, 0, body.length);
    }

    public static long decodeSeekerMessage(Message message) {
        return Message.decodeLong(message);
    }

    /**
     * Packet Builder
     */
    public static class Builder {

        private Headers headers = new Headers();
        private byte[] body = null;

        private int length;

        private Builder appendHeaders(Headers headers) {
            this.headers = headers;

            return this;
        }

        private Builder appendHeader(Headers.Header header) {
            this.headers.appendHeader(header);

            return this;
        }

        private Builder appendHeader(String key, String value) {
            this.headers.appendHeader(key, value);

            return this;
        };

        private Builder appendHeader(String key, int value) {
            return appendHeader(key, String.valueOf(value));
        };

        private Builder appendBody(String body) {
            try {
                this.body = body.getBytes("ISO-8859-1");
                this.length = this.body.length;
            } catch(UnsupportedEncodingException e) {
                Log.v(TAG, e.toString());
            }

            return this;
        }

        private Builder appendBody(float value) {
            return appendBody(String.valueOf(value));
        }

        private Builder appendBody(long value) {
            return appendBody(String.valueOf(value));
        }

        private Builder appendBody(byte[] body, int length) {
            this.length = length;
            this.body = body;

            return this;
        }

        public byte[] build() {
            byte[] packet = new byte[PACKET_SIZE];

            try {
                int length = body != null ? this.length : 0;

                StringBuilder stringBuilder = new StringBuilder();
                for(Map.Entry<String, Headers.Header> header : headers.getHeaders().entrySet()) {
                    header.getValue().encode(stringBuilder);
                }

                Headers.Header packetHeader = new Headers.Header(PACKET.HEADER, PACKET_SIZE);
                packetHeader.encode(stringBuilder);

                Headers.Header bodyLength = new Headers.Header(CONTENT.LENGTH.HEADER, String.valueOf(length));
                bodyLength.encode(stringBuilder);

                stringBuilder.append("\r\n");

                byte[] headers = stringBuilder.toString().getBytes("ISO-8859-1");
                System.arraycopy(headers, 0, packet, 0, headers.length);
                if(length > 0) {
                    System.arraycopy(body, 0, packet, headers.length, length);
                }
            } catch(UnsupportedEncodingException e) {
                Log.v(TAG, e.toString());
            }

            return packet;
        }
    }
}
