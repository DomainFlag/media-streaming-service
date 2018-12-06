package com.example.cchiv.jiggles.player.protocol.builder;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Message {

    private static final String TAG = "Message";

    List<Packet> packets = new ArrayList<>();
    private String identifier;
    private boolean state = false;

    public Message(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getAction() {
        return packets.get(0).getHeaders().getValue(Protocol.ACTIONS.HEADER);
    }

    public void resolve(Packet packet) {
        int chunkSize = Integer.valueOf(packet.getHeaders().getValue(Protocol.CHUNKS.LENGTH.HEADER));
        if(chunkSize == Protocol.CHUNKS.LENGTH.UNKNOWN_STREAM_END)
            state = true;
        else {
            packets.add(packet);

            state = chunkSize == packets.size();
        }
    }

    public boolean isResolved() {
        return state;
    }

    public static String generateIdentifier() {
        return UUID.randomUUID().toString();
    }

    public static String decodeString(Message message) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            for(Packet packet : message.packets) {
                String data = new String(packet.getBody().getInput(), "ISO-8859-1");

                stringBuilder.append(data);
            }
        } catch(IOException e) {
            Log.v(TAG, e.toString());
        }


        return stringBuilder.toString();
    }

    public static Long decodeLong(Message message) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            for(Packet packet : message.packets) {
                String data = new String(packet.getBody().getInput(), "ISO-8859-1");

                stringBuilder.append(data);
            }
        } catch(IOException e) {
            Log.v(TAG, e.toString());
        }

        return Long.valueOf(stringBuilder.toString());
    }

    public static byte[] decodeByteSource(Message message) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            for(Packet packet : message.packets) {
                byteArrayOutputStream.write(packet.getBody().getInput());
            }
        } catch(IOException e) {
            Log.v(TAG,  e.toString());
        }

        return byteArrayOutputStream.toByteArray();
    }
}
