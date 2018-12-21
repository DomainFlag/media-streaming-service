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

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class PlayerAppCompatActivity extends AppCompatActivity implements
        PlayerService.OnCallbackListener, PlayerServiceConnection.OnConnectionCallback, RemoteMediaCallback {

    private static final String TAG = "PlayerAppCompatActivity";

    public PlayerServiceConnection playerServiceConnection = null;

    @BindView(R.id.home_player) View barPlayerLayout;
    @BindView(R.id.home_player_title) TextView barPlayerTitle;
    @BindView(R.id.home_player_controller) ImageView barPlayerController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateActivity(savedInstanceState);

        ButterKnife.bind(this);

        playerServiceConnection = new PlayerServiceConnection(this);
        playerServiceConnection.onStartService(null);
    }

    private void updatePlayerBar(Track track, int playbackStateCompat) {
        if(track == null) {
            // Setting the PlayerBar invisible as there is no content to be played
            barPlayerLayout.setVisibility(View.GONE);
        } else {
            // Setting the title and intent action
            barPlayerTitle.setText(getString(R.string.home_bar_title, track.getArtistName(), track.getName()));
            barPlayerTitle.setOnClickListener((view) -> {
                Intent intent = new Intent(this, PlayerActivity.class);

                startActivity(intent);
            });

            // Setting the background color of the PlayerBar
            barPlayerLayout.setBackgroundColor(track.getColor(this));

            // Set up the PlayerBar controller for resume/pause state
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

            // Setting the PlayerBar visible
            barPlayerLayout.setVisibility(View.VISIBLE);
        }
    }

    public void onChangeCurrentTrack() {
        MediaPlayer mediaPlayer = playerServiceConnection.getMediaPlayer();

        if(mediaPlayer != null)
            updatePlayerBar(mediaPlayer.getCurrentTrack(), mediaPlayer.getPlaybackState());
    }

    public void startService(Bundle bundle) {
        playerServiceConnection.onStartService(bundle);
    }

    private void onAttachControllerListener(boolean state) {
        barPlayerController.setOnClickListener((view) -> {
            MediaPlayer mediaPlayer = playerServiceConnection.getMediaPlayer();

            mediaPlayer.togglePlayer(state);
        });
    }

    protected abstract void onCreateActivity(@Nullable Bundle savedInstanceState);

    @Override
    protected void onResume() {
        super.onResume();

        // Activity is resumed, thus query is performed to get updated current state
        onChangeCurrentTrack();
    }

    @Override
    protected void onDestroy() {
        playerServiceConnection.release();

        super.onDestroy();
    }

    @Override
    public void onCallbackListener(Track track, int playbackStateCompat) {
        updatePlayerBar(track, playbackStateCompat);
    }

    @Override
    public void onConnectionCallbackComplete() {
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
