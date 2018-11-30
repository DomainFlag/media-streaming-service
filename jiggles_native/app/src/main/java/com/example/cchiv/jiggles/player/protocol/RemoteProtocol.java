package com.example.cchiv.jiggles.player.protocol;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RemoteProtocol {

    private static final String TAG = "RemoteProtocol";

    public static class IDENTIFIER {
        public static final String HEADER = "identifier-value";
    }

    public static class ACTIONS {
        public static final String HEADER = "action-type";

        public static final String ACTION_STREAM = "stream";
        public static final String ACTION_SEEK = "stream";
        public static final String ACTION_PAUSE = "pause";
        public static final String ACTION_RESUME = "resume";
    }

    public static class CHUNKS {
        public static class NUMBER {
            public static final String HEADER = "chunks-index";
        }

        public static class LENGTH {
            public static final String HEADER = "chunks-length";

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

    public static byte[] createResumeAction(String identifier) {
        Builder builder = new Builder();

        return builder
                .appendHeader(ACTIONS.HEADER, ACTIONS.ACTION_RESUME)
                .appendHeader(IDENTIFIER.HEADER, identifier)
                .appendHeader(CHUNKS.LENGTH.HEADER, 1)
                .appendHeader(CONTENT.TYPE.HEADER, CONTENT.TYPE.TYPE_NONE)
                .build();
    }

    public static byte[] createSeekAction(String identifier, float value) {
        Builder builder = new Builder();

        return builder
                .appendHeader(ACTIONS.HEADER, ACTIONS.ACTION_SEEK)
                .appendHeader(IDENTIFIER.HEADER, identifier)
                .appendHeader(CHUNKS.LENGTH.HEADER, 1)
                .appendHeader(CONTENT.TYPE.HEADER, CONTENT.TYPE.TYPE_TEXT)
                .appendBody(value).build();
    }

    public static byte[] createPauseAction(String identifier) {
        Builder builder = new Builder();

        return builder
                .appendHeader(ACTIONS.HEADER, ACTIONS.ACTION_PAUSE)
                .appendHeader(IDENTIFIER.HEADER, identifier)
                .appendHeader(CHUNKS.LENGTH.HEADER, 1)
                .appendHeader(CONTENT.TYPE.HEADER, CONTENT.TYPE.TYPE_NONE)
                .build();
    }

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

    private static Header decodeHeader(String line) {
        String[] header = line.split(": ");

        if(header.length == 2)
            return new Header(header[0], header[1]);
        else return null;
    };

    public static Connection.Chunk decodeChunk(byte[] data, int size) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data, 0, size);
            InputStreamReader inputStreamReader = new InputStreamReader(byteArrayInputStream, "ISO-8859-1");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            HashMap<String, String> headers = new HashMap<>();

            String line = bufferedReader.readLine();
            while(line != null && !line.equals("\n")) {
                Header header = decodeHeader(line);

                if(header != null) {
                    if(!headers.containsKey(header.getHeader())) {
                        headers.put(header.getHeader(), header.getValue());
                    }
                }

                line = bufferedReader.readLine();
            }

            byte[] body = null;
            if(headers.containsKey(CONTENT.LENGTH.HEADER)) {
                int length = Integer.valueOf(headers.get(CONTENT.LENGTH.HEADER));

                if(length > 0) {
                    body = new byte[length];

                    int offset = data.length - length;

                    System.arraycopy(data, offset, body, 0, length);
                }
            }

            return new Connection.Chunk(headers, body);
        } catch(UnsupportedEncodingException e) {
            Log.v(TAG, e.toString());
        } catch(IOException e) {
            Log.v(TAG, e.toString());
        }

        return null;
    }

    public static class Builder {

        private List<Header> headers = new ArrayList<>();

        private byte[] body = null;

        private int length;

        private Builder appendHeader(Header header) {
            headers.add(header);

            return this;
        };

        private Builder appendHeader(String header, String value) {
            return appendHeader(new Header(header, value));
        };

        private Builder appendHeader(String header, int value) {
            return appendHeader(new Header(header, String.valueOf(value)));
        };

        private Builder appendBody(String body) {
            try {
                this.body = body.getBytes("ISO-8859-1");
            } catch(UnsupportedEncodingException e) {
                Log.v(TAG, e.toString());
            }

            return this;
        }

        private Builder appendBody(float value) {
            return appendBody(String.valueOf(value));
        }

        private Builder appendBody(byte[] body) {
            this.body = body;

            return this;
        }

        private Builder appendBody(byte[] body, int length) {
            this.length = length;

            return this.appendBody(body);
        }

        public byte[] build() {
            try {
                int length = body != null ? body.length : 0;

                StringBuilder stringBuilder = new StringBuilder();
                for(Header header : headers) {
                    header.encode(stringBuilder);
                }

                Header header = new Header(CONTENT.LENGTH.HEADER, String.valueOf(length));
                header.encode(stringBuilder);

                byte[] headers = stringBuilder.toString().getBytes("ISO-8859-1");
                if(length != 0) {
                    byte[] message = new byte[headers.length + body.length];

                    System.arraycopy(headers, 0, message, 0, headers.length);
                    System.arraycopy(body, 0, message, headers.length, length);

                    return message;
                }

                return headers;
            } catch(UnsupportedEncodingException e) {
                Log.v(TAG, e.toString());
            }

            return null;
        }
    }
}