package com.example.cchiv.jiggles.player;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.activities.PlayerActivity;
import com.example.cchiv.jiggles.model.Image;
import com.example.cchiv.jiggles.model.Track;
import com.example.cchiv.jiggles.services.PlayerService;

import java.io.IOException;

public class PlayerMediaSession {

    private static final String TAG = "PlayerMediaSession";

    public static final String NOTIFICATION_PLAYER_CONTROLLER = "NOTIFICATION_PLAYER_CONTROLLER";

    private MediaPlayer mediaPlayer;

    private static MediaSessionCompat mediaSessionCompat = null;

    private PlaybackStateCompat playbackStateCompat = null;
    private PlaybackStateCompat.Builder builder;
    private NotificationManager notificationManager = null;

    private Context context;

    public PlayerMediaSession(Context context, MediaPlayer mediaPlayer) {
        this.context = context;
        this.mediaPlayer = mediaPlayer;
    }

    public void createMediaSession() {
        if(mediaSessionCompat != null)
            mediaSessionCompat.release();

        mediaSessionCompat = new MediaSessionCompat(context, TAG);

        mediaSessionCompat.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSessionCompat.setMediaButtonReceiver(null);

        builder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mediaSessionCompat.setPlaybackState(builder.build());
        mediaSessionCompat.setCallback(new JigglesSessionCallback());
        mediaSessionCompat.setActive(true);

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void setState(int state) {
        builder.setState(state, mediaPlayer.getExoPlayer().getCurrentPosition(), 1f);
    }

    public int getState() {
        if(playbackStateCompat != null)
            return playbackStateCompat.getState();
        else return -1;
    }

    public Notification buildNotificationPlayer(Track track) {
        playbackStateCompat = builder.build();

        mediaSessionCompat.setPlaybackState(builder.build());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_PLAYER_CONTROLLER);

        int toggleIcon;
        if(playbackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING) {
            toggleIcon = R.drawable.exo_controls_pause;
        } else {
            toggleIcon = R.drawable.exo_controls_play;
        }

        NotificationCompat.Action playbackAction = new NotificationCompat.Action(
                toggleIcon, "Toggle playback",
                MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                        PlaybackStateCompat.ACTION_PLAY_PAUSE));

        NotificationCompat.Action restartAction = new NotificationCompat
                .Action(R.drawable.exo_controls_previous, "Restart",
                MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (context, 0, new Intent(context, PlayerActivity.class), 0);

        Image art = track.getAlbum().getArt();
        Bitmap largeIcon = null;
        try {
            if(art.getUrl() != null)
                largeIcon = MediaStore.Images.Media.getBitmap(context.getContentResolver(), art.getUrl());
            else largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_artwork_placeholder);
        } catch(IOException e) {
            Log.v(TAG, e.toString());
        }


        if(notificationManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_PLAYER_CONTROLLER,
                    "MediaPlayer channel",
                    NotificationManager.IMPORTANCE_LOW);

            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = builder
                .setContentTitle(track.getName())
                .setColorized(true)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0))
                .setColor(art.getColor())
                .setContentText(track.getArtist().getName())
                .setSmallIcon(R.drawable.ic_microphone)
                .setLargeIcon(largeIcon)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(contentPendingIntent)
                .addAction(restartAction)
                .addAction(playbackAction)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken())
                        .setShowActionsInCompactView(0, 1))
                .build();

        notificationManager.notify(PlayerService.SERVICE_NOTIFICATION_ID, notification);

        return notification;
    }

    public void setActive(boolean active) {
        if(mediaSessionCompat != null)
            mediaSessionCompat.setActive(active);
    }

    public void release() {
        if(mediaSessionCompat != null) {
            mediaSessionCompat.setActive(false);
            mediaSessionCompat.release();

            mediaSessionCompat = null;
        }

        if(notificationManager != null)
            notificationManager.cancelAll();

        if(playbackStateCompat != null)
            playbackStateCompat = null;
    }

    private class JigglesSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mediaPlayer.togglePlayback(true);
        }

        @Override
        public void onPause() {
            mediaPlayer.togglePlayback(false);
        }

        @Override
        public void onSkipToPrevious() {
            mediaPlayer.changeSeeker(0);
        }
    }

    public static class MediaPlayerReceiver extends BroadcastReceiver {

        public MediaPlayerReceiver() {}

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mediaSessionCompat, intent);
        }
    }
}
