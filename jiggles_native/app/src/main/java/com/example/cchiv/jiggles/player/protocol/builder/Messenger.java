package com.example.cchiv.jiggles.player.protocol.builder;

import android.app.Activity;
import android.content.Context;

import com.example.cchiv.jiggles.player.MediaPlayer;
import com.example.cchiv.jiggles.player.protocol.RemotePlayer;

import java.util.HashMap;

public class Messenger {

    private static final String TAG = "Messenger";

    private Context context;
    private RemotePlayer remotePlayer;

    private HashMap<String, Message> messages = new HashMap<>();
    private Packet packet = null;

    public Messenger(Context context, RemotePlayer remotePlayer) {
        this.context = context;
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
                String type = packet.getHeaders().getValue(Protocol.CONTENT.TYPE.HEADER);

                Message message;
                if(messages.containsKey(identifier)) {
                    message = messages.get(identifier);
                } else {
                    message = new Message(identifier);

                    messages.put(identifier, message);
                }

                message.resolve(packet);
                if(type.equals(Protocol.CONTENT.TYPE.TYPE_AUDIO)) {
                    resolveAudioPacket(message, packet);
                }

                if(message.isResolved())
                    remotePlayer.onManageMessage(message);

                packet = null;
            }

            offset += length;
        }
    }

    public void resolveAudioPacket(Message message, Packet packet) {
        final MediaPlayer.DataFetcher dataFetcher = message.getDataFetcher();
        MediaPlayer mediaPlayer = remotePlayer.getMediaPlayer();
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.setStreamSource(dataFetcher);

                if(packet.getBody() != null)
                    dataFetcher.write(packet.getBody().getInput());
            }
        });
    }
}
