package com.example.cchiv.jiggles.player.protocol.builder;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class Packet {
    
    private static final String TAG = "Packet";

    private byte[] input = new byte[Protocol.PACKET_SIZE];

    private Headers headers = new Headers();
    private Body body = null;

    private int count = 0;

    public Packet() {}

    public Packet(Headers headers, Body body) {
        this.headers = headers;
        this.body = body;
    };

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Headers.Header getHeader(String header) {
        if(headers.containsHeader(header))
            return headers.getHeader(header);
        else return null;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

    public Headers getHeaders() {
        return headers;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Body getBody() {
        return body;
    }

    /**
     * Resolve a chunk coming from a packet be it deconstructed or not
     */
    public void resolveChunk(byte[] data, int offset, int size) {
        System.arraycopy(data, offset, input, getCount(), size);

        setCount(getCount() + size);
    }

    public boolean isResolved() {
        if(getCount() == Protocol.PACKET_SIZE) {
            Packet.decodeHeaders(this);
            Packet.decodeBody(this);

            return true;
        } else {
            return false;
        }
    }

    private static Line decodeLine(BufferedReader bufferedReader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        boolean flag = false;

        int character = bufferedReader.read();
        while(character != -1 && ((char) character != '\n')) {
            if((char) character == '\r') {
                flag = true;
            }

            stringBuilder.append((char) character);

            character = bufferedReader.read();
        }

        if(character == -1 && stringBuilder.toString().isEmpty())
            return null;

        return new Line(stringBuilder.toString(), flag);
    }

    private static void decodeHeaders(Packet packet) {
        Headers headers = packet.getHeaders();
        int offset = 2;

        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(packet.input, 0, Protocol.PACKET_SIZE);
            InputStreamReader inputStreamReader = new InputStreamReader(byteArrayInputStream, "ISO-8859-1");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            Line line = decodeLine(bufferedReader);
            while(line != null && !line.flag) {
                Headers.Header header = Headers.Header.decodeHeader(line.input);

                if(header != null) {
                    if(!headers.containsHeader(header)) {
                        headers.appendHeader(header);
                    }
                }

                offset += line.input.length() + 1;

                line = decodeLine(bufferedReader);
            }
        } catch(UnsupportedEncodingException e) {
            Log.v(TAG, e.toString());
        } catch(IOException e) {
            Log.v(TAG, e.toString());
        }

        headers.setOffset(offset);
    }

    private static void decodeBody(Packet packet) {
        Headers headers = packet.getHeaders();

        int offset = headers.getOffset();
        int size = Integer.valueOf(headers.getValue(Protocol.CONTENT.LENGTH.HEADER));

        if(size > 0) {
            Body body = packet.getBody();
            if(body == null) {
                body = new Body(size);

                packet.setBody(body);
            }

            body.resolve(packet.input, offset, size);
        }
    }

    private static class Line {

        private String input;
        private boolean flag;

        Line(String input, boolean flag) {
            this.input = input;
            this.flag = flag;
        }
    }
}