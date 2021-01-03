package com.example.cchiv.jiggles.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.databinding.PlayerBarLayoutBinding;
import com.example.cchiv.jiggles.interfaces.RemoteMediaCallback;
import com.example.cchiv.jiggles.model.player.PlayerState;
import com.example.cchiv.jiggles.model.player.Store;
import com.example.cchiv.jiggles.model.player.content.Track;
import com.example.cchiv.jiggles.player.MediaPlayer;
import com.example.cchiv.jiggles.services.PlayerConnection;
import com.example.cchiv.jiggles.services.PlayerService;
import com.google.android.exoplayer2.Player;


public abstract class PlayerAppCompatActivity extends AppCompatActivity implements
        PlayerConnection.PlayerServiceConnection, RemoteMediaCallback {

    private static final String TAG = "PlayerAppCompatActivity";

    private PlayerConnection playerConnection;
    private PlayerBarLayoutBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateActivity(savedInstanceState);

        binding = PlayerBarLayoutBinding.inflate(getLayoutInflater());
        // TODO(0): Should we keep it that way?
        // setContentView(binding.getRoot());

        playerConnection = new PlayerConnection(this);
        playerConnection.onStartService(null);
    }

    private void inflateMediaPlayer(MediaPlayer mediaPlayer) {
        Player player = mediaPlayer.getPlayer();

        if(player == null) {
            // no active player
            binding.homePlayer.setVisibility(View.GONE);
        } else {
            PlayerState playerState = mediaPlayer.getPlayerState();
            Store store = playerState.getStore();

            Track track = store.getTrack(playerState.getPosition());

            // setting the title and intent action
            binding.homePlayerTitle.setText(getString(R.string.home_bar_title, track.getArtistName(), track.getName()));
            binding.homePlayerTitle.setOnClickListener((view) -> {
                Intent intent = new Intent(this, PlayerActivity.class);

                startActivity(intent);
            });

            // set PlayerBar's background color
            binding.homePlayer.setBackgroundColor(track.getColor(this));

            boolean state = playerState.isPlaying;
            int controlDrawableResource = state ? R.drawable.exo_controls_pause : R.drawable.exo_controls_play;

            binding.homePlayerController.setImageDrawable(ContextCompat.getDrawable(this, controlDrawableResource));

            binding.homePlayerController.setOnClickListener((view) -> {
                mediaPlayer.toggle(state);
            });

            binding.homePlayer.setVisibility(View.VISIBLE);
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
