package com.example.cchiv.jiggles.player.protocol.builder;

import android.util.Log;

import java.util.LinkedHashMap;

public class Headers {

    private static final String TAG = "Headers";

    private LinkedHashMap<String, Header> headers = new LinkedHashMap<>();
    private int offset = 0;

    public Headers() {}

    public Header getHeader(String key) {
        return headers.get(key);
    }

    public String getValue(String key) {
        return getHeader(key).getValue();
    }

    public boolean containsHeader(Header header) {
        return headers.containsKey(header.getHeader());
    }

    public boolean containsHeader(String key) {
        return headers.containsKey(key);
    }

    public void appendHeader(Header header) {
        headers.put(header.getHeader(), header);
    }

    public void appendHeader(String key, int value) {
        Header header = new Header(key, String.valueOf(value));

        headers.put(key, header);
    }

    public void appendHeader(String key, String value) {
        Header header = new Header(key, value);

        headers.put(key, header);
    }

    public void printHeaders() {
        for(LinkedHashMap.Entry<String, Header> header : headers.entrySet()) {
            Log.v(TAG, header.getKey() + ": " + header.getValue());
        }
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public LinkedHashMap<String, Header> getHeaders() {
        return headers;
    }

    public static class Header {

        private static final String TAG = "Header";

        private String header;
        private String value;

        public Header(String header, String value) {
            this.header = header;
            this.value = value;
        }

        public Header(String header, int value) {
            this(header, String.valueOf(value));
        }

        public void encode(StringBuilder stringBuilder, String value) {
            stringBuilder.append(header);
            stringBuilder.append(": ");
            stringBuilder.append(value);
            stringBuilder.append("\n");
        }

        public void encode(StringBuilder stringBuilder) {
            encode(stringBuilder, value);
        }

        public Header decode(String value) {
            return new Header(null, null);
        }

        public String getHeader() {
            return header;
        }

        public String getValue() {
            return this.value;
        }

        public static Header decodeHeader(String line) {
            String[] header = line.split(": ");

            if(header.length == 2)
                return new Header(header[0], header[1]);
            else return null;
        };
    }
}