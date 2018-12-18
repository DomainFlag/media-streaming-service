package com.example.cchiv.jiggles.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.interfaces.RemoteMediaCallback;
import com.example.cchiv.jiggles.model.Track;
import com.example.cchiv.jiggles.player.MediaPlayer;
import com.example.cchiv.jiggles.services.PlayerService;
import com.example.cchiv.jiggles.services.PlayerServiceConnection;

public abstract class PlayerAppCompatActivity extends AppCompatActivity implements
        PlayerService.OnCallbackListener, PlayerServiceConnection.OnCallbackConnectionComplete, RemoteMediaCallback {

    private static final String TAG = "PlayerAppCompatActivity";

    public PlayerServiceConnection playerServiceConnection = null;

    public View barPlayerLayout;
    public TextView barPlayerTitle;
    public ImageView barPlayerController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateActivity(savedInstanceState);

        barPlayerLayout = findViewById(R.id.home_player);
        barPlayerTitle = findViewById(R.id.home_player_title);
        barPlayerController = findViewById(R.id.home_player_controller);

        playerServiceConnection = new PlayerServiceConnection(this);
        playerServiceConnection.onStartService(null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        onChangeCurrentTrack();
    }

    public void onChangeCurrentTrack() {
        Track track = playerServiceConnection.getCurrentTrack();
        int playbackStateCompat = playerServiceConnection.getPlaybackStateCompat();

        updatePlayerBar(track, playbackStateCompat);
    }

    private void updatePlayerBar(Track track, int playbackStateCompat) {
        if(track == null) {
            barPlayerLayout.setVisibility(View.GONE);
            barPlayerLayout.setOnClickListener(null);
        } else {
            barPlayerLayout.setVisibility(View.VISIBLE);

            barPlayerTitle.setText(getString(R.string.home_bar_title, track.getArtistName(), track.getName()));
            barPlayerTitle.setOnClickListener((view) -> {
                Intent intent = new Intent(this, PlayerActivity.class);
                startActivity(intent);
            });

            barPlayerLayout.setBackgroundColor(track.getColor(this));
            switch(playbackStateCompat) {
                case PlaybackStateCompat.STATE_PLAYING : {
                    barPlayerController.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.exo_controls_pause));

                    onAttachControllerListener(false);
                    break;
                }
                default : {
                    barPlayerController.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.exo_controls_play));

                    onAttachControllerListener(true);
                    break;
                }
            }
        }
    }

    public void startService(Bundle bundle) {
        playerServiceConnection.onStartService(bundle);
    }

    private void onAttachControllerListener(boolean state) {
        barPlayerController.setOnClickListener((view) -> {
            MediaPlayer mediaPlayer = playerServiceConnection.getMediaPlayer();
            mediaPlayer.togglePlayer(state);

            playerServiceConnection.getPlayerService().togglePlayer(state);
        });
    }

    protected abstract void onCreateActivity(@Nullable Bundle savedInstanceState);

    protected abstract void onDestroyActivity();

    @Override
    public void onCallbackListener(Track track, int playbackStateCompat) {
        updatePlayerBar(track, playbackStateCompat);
    }

    @Override
    public void onCallbackConnectionComplete() {
        onChangeCurrentTrack();
    }

    @Override
    public void onRemoteMediaClick(String uri) {
        Bundle bundle = new Bundle();
        bundle.putString(PlayerService.RESOURCE_SOURCE, PlayerService.RESOURCE_REMOTE);
        bundle.putString(PlayerService.RESOURCE_IDENTIFIER, uri);

        startService(bundle);
    }
}
