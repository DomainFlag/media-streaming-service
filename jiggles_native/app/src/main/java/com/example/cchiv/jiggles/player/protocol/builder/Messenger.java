package com.example.cchiv.jiggles.player.protocol.builder;

import com.example.cchiv.jiggles.player.protocol.RemotePlayer;

import java.util.HashMap;

public class Messenger {

    private static final String TAG = "Messenger";

    private RemotePlayer remotePlayer;

    private HashMap<String, Message> messages = new HashMap<>();
    private Packet packet = null;

    public Messenger(RemotePlayer remotePlayer) {
        this.remotePlayer = remotePlayer;
    }

    public void resolvePacket(byte[] data, int size) {
        int offset = 0;

        while(offset != size) {
            if(packet == null) {
                packet = new Packet();
            }

            int length = Math.min(size - offset, Protocol.PACKET_SIZE - packet.getCount());

            packet.resolveChunk(data, offset, length);

            if(packet.isResolved()) {
                String identifier = packet.getHeaders().getValue(Protocol.IDENTIFIER.HEADER);

                Message message;
                if(messages.containsKey(identifier)) {
                    message = messages.get(identifier);
                } else {
                    message = new Message(identifier);

                    messages.put(identifier, message);
                }

                message.resolve(packet);

                if(message.isResolved())
                    remotePlayer.onManageMessage(message);

                packet = null;
            }

            offset += length;
        }
    }
}
