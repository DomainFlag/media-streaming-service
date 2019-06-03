package com.example.cchiv.jiggles.player;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.activities.PlayerActivity;
import com.example.cchiv.jiggles.model.player.PlayerState;
import com.example.cchiv.jiggles.model.player.Store;
import com.example.cchiv.jiggles.model.player.content.Track;
import com.example.cchiv.jiggles.services.PlayerService;

public class MediaSessionPlayer {

    private static final String TAG = "MediaSessionPlayer";

    private static final String NOTIFICATION_PLAYER_CONTROLLER = "NOTIFICATION_PLAYER_CONTROLLER";

    private static MediaSessionCompat mediaSessionCompat;

    private PlaybackStateCompat.Builder builder;
    private NotificationManager notificationManager;

    private Context context;

    private MediaPlayer mediaPlayer;

    public MediaSessionPlayer(Context context, MediaPlayer mediaPlayer) {
        this.context = context;
        this.mediaPlayer = mediaPlayer;

        createMediaSession();
    }

    public void createMediaSession() {
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
        builder.setState(state, 0, 1f);
    }

    public int getState() {
        return builder.build().getState();
    }

    private void setPlaybackStateCompat(PlaybackStateCompat playbackStateCompat) {
        mediaSessionCompat.setPlaybackState(playbackStateCompat);
    }

    public Notification buildNotificationPlayer(PlayerState playerState) {
        if(playerState == null)
            return null;

        Store store = playerState.getStore();
        Track track = store.getTrack(playerState.getPosition());

        PlaybackStateCompat playbackStateCompat = builder.build();
        setPlaybackStateCompat(playbackStateCompat);

        int toggleIcon;
        if(playbackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING) {
            toggleIcon = R.drawable.exo_controls_pause;
        } else {
            toggleIcon = R.drawable.exo_controls_play;
        }

        // Playback action resume/pause
        NotificationCompat.Action playbackAction = new NotificationCompat.Action(
                toggleIcon, "Toggle playback",
                MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                        PlaybackStateCompat.ACTION_PLAY_PAUSE));

        // Playback action seek to starting position
        NotificationCompat.Action restartAction = new NotificationCompat
                .Action(R.drawable.exo_controls_previous, "Restart",
                MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        // Intent action redirect to the PlayerActivity
        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (context, 0, new Intent(context, PlayerActivity.class), 0);

        // Setting up notification channel for Oreo
        if(notificationManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_PLAYER_CONTROLLER,
                    "MediaPlayer channel",
                    NotificationManager.IMPORTANCE_LOW);

            notificationManager.createNotificationChannel(channel);
        }

        // Build notification
        Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_PLAYER_CONTROLLER)
                .setContentTitle(track.getName())
                .setColorized(true)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0))
                .setColor(track.getColor(context))
                .setContentText(track.getArtistName())
                .setSmallIcon(R.drawable.ic_logo)
                .setColor(ContextCompat.getColor(context, R.color.unexpectedColor))
                .setLargeIcon(track.getBitmap(context))
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

    public void release() {
        notificationManager.cancelAll();

        mediaSessionCompat.setActive(false);
        mediaSessionCompat.release();
    }

    private class JigglesSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mediaPlayer.toggle(true);
        }

        @Override
        public void onPause() {
            mediaPlayer.toggle(false);
        }

        @Override
        public void onSkipToPrevious() {
            mediaPlayer.skip(true);
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
