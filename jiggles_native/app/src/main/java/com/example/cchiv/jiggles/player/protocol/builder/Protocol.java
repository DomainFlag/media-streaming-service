package com.example.cchiv.jiggles.player.protocol.builder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.cchiv.jiggles.player.protocol.RemoteConnection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class Protocol {

    private static final String TAG = "Protocol";

    public static final int PACKET_SIZE = 1024;
    public static final int BODY_SIZE = 512;

    public static class IDENTIFIER {
        public static final String HEADER = "identifier-value";
    }

    public static class ACTIONS {
        public static final String HEADER = "action-type";

        public static final String ACTION_STREAM = "stream";
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
            public static final String TYPE_JSON = "json";
            public static final String TYPE_NONE = "none";
        }

        public static class LENGTH {
            public static final String HEADER = "content-length";
        }
    }

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

    /********************************************************/
    private static void sendMessage(RemoteConnection remoteConnection, String identifier, byte[] data, int size, int length) {
        byte[] streamAction = Protocol.createStreamAction(identifier, data, size, length);

        remoteConnection.writeStream(streamAction);
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
