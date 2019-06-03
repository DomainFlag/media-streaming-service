package com.example.cchiv.jiggles.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.interfaces.RemoteMediaCallback;
import com.example.cchiv.jiggles.model.player.PlayerState;
import com.example.cchiv.jiggles.model.player.Store;
import com.example.cchiv.jiggles.model.player.content.Track;
import com.example.cchiv.jiggles.player.MediaPlayer;
import com.example.cchiv.jiggles.services.PlayerConnection;
import com.example.cchiv.jiggles.services.PlayerService;
import com.google.android.exoplayer2.Player;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class PlayerAppCompatActivity extends AppCompatActivity implements
        PlayerConnection.PlayerServiceConnection, RemoteMediaCallback {

    private static final String TAG = "PlayerAppCompatActivity";

    private PlayerConnection playerConnection;

    @BindView(R.id.home_player) View barPlayerLayout;
    @BindView(R.id.home_player_title) TextView barPlayerTitle;
    @BindView(R.id.home_player_controller) ImageView barPlayerController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateActivity(savedInstanceState);

        ButterKnife.bind(this);

        playerConnection = new PlayerConnection(this);
        playerConnection.onStartService(null);
    }

    private void inflateMediaPlayer(MediaPlayer mediaPlayer) {
        Player player = mediaPlayer.getPlayer();

        if(player == null) {
            // no active player
            barPlayerLayout.setVisibility(View.GONE);
        } else {
            PlayerState playerState = mediaPlayer.getPlayerState();
            Store store = playerState.getStore();

            Track track = store.getTrack(playerState.getPosition());

            // setting the title and intent action
            barPlayerTitle.setText(getString(R.string.home_bar_title, track.getArtistName(), track.getName()));
            barPlayerTitle.setOnClickListener((view) -> {
                Intent intent = new Intent(this, PlayerActivity.class);

                startActivity(intent);
            });

            // set PlayerBar's background color
            barPlayerLayout.setBackgroundColor(track.getColor(this));

            boolean state = playerState.isPlaying;
            int controlDrawableResource = state ? R.drawable.exo_controls_pause : R.drawable.exo_controls_play;

            barPlayerController.setImageDrawable(ContextCompat.getDrawable(this, controlDrawableResource));

            barPlayerController.setOnClickListener((view) -> {
                mediaPlayer.toggle(state);
            });

            barPlayerLayout.setVisibility(View.VISIBLE);
        }
    }

    public void startService(Bundle bundle) {
        playerConnection.onStartService(bundle);
    }

    @Override
    public void onRemoteMediaClick(String uri) {
        Bundle bundle = new Bundle();
        bundle.putString(PlayerService.RESOURCE_SOURCE, PlayerService.RESOURCE_REMOTE);
        bundle.putString(PlayerService.RESOURCE_IDENTIFIER, uri);

        startService(bundle);
    }

    @Override
    public void onPlayerServiceCallback(PlayerService playerService) {
        MediaPlayer mediaPlayer = playerService.getMediaPlayer();

        // inflate the views and manage player controller
        inflateMediaPlayer(mediaPlayer);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // activity is resumed, request current state
        playerConnection.requestCallbackCall();
    }

    @Override
    protected void onDestroy() {
        playerConnection.release();

        super.onDestroy();
    }

    protected abstract void onCreateActivity(@Nullable Bundle savedInstanceState);
}
