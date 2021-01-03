package com.example.cchiv.jiggles.activities;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.databinding.ActivityAuthBinding;
import com.example.cchiv.jiggles.databinding.ActivityPlayerBinding;
import com.example.cchiv.jiggles.model.Image;
import com.example.cchiv.jiggles.model.player.PlayerState;
import com.example.cchiv.jiggles.model.player.Store;
import com.example.cchiv.jiggles.model.player.content.Track;
import com.example.cchiv.jiggles.player.MediaPlayer;
import com.example.cchiv.jiggles.player.protocol.RemotePlayer;
import com.example.cchiv.jiggles.prediction.Speech;
import com.example.cchiv.jiggles.services.PlayerConnection;
import com.example.cchiv.jiggles.services.PlayerService;
import com.example.cchiv.jiggles.utilities.Tools;
import com.example.cchiv.jiggles.views.ToggleImageView;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;
import com.squareup.picasso.Picasso;


public class PlayerActivity extends AppCompatActivity implements
        PlayerConnection.PlayerServiceConnection, RemotePlayer.OnUpdateInterface {

    private static final String TAG = "PlayerActivity";

    public PlayerView playerView = null;

    public PlayerConnection playerConnection = null;
    
    private ActivityPlayerBinding binding;

    private Speech speech = new Speech(this, (action, position) -> {
        if(playerConnection != null) {
            MediaPlayer mediaPlayer = playerConnection.getMediaPlayer();

            switch(action) {
                case "left" : {
                    mediaPlayer.skip(false);

                    break;
                }
                case "right" : {
                    mediaPlayer.skip(true);

                    break;
                }
                case "stop" : {
                    mediaPlayer.toggle(false);

                    break;
                }
                case "go" : {
                    mediaPlayer.toggle(true);

                    break;
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Back command
        findViewById(R.id.player_back).setOnClickListener((view) -> {
            finish();
        });

        // Menu utilities
        ((ToggleImageView) binding.playerMenu).setOnActiveCallback((view, isActive) -> {
            if(isActive) {
                binding.playerUtilities.setVisibility(View.VISIBLE);
            } else {
                binding.playerUtilities.setVisibility(View.GONE);
            }
        });

        // Lyrics
        ((ToggleImageView) binding.playerMediaLyrics).setOnActiveCallback((view, isActive) -> {
            // Do something later with song lyrics
            if(isActive) {
                speech.initiate();
            } else {
                speech.release();
            }
        });

        // Identifier
        ((ToggleImageView) binding.playerMediaIdentifier).setOnActiveCallback(((view, isActive) -> {
            // Do something later with song identifier
        }));

        // Share
        ((ToggleImageView) binding.playerMediaShare).setOnActiveCallback((view, isActive) -> {
//            LocalPlayer localPlayer = playerConnection.getMediaPlayer().getLocalAlphaPlayer();
//            RemotePlayer remotePlayer = localPlayer.getRemotePlayer();
//
//            if(isActive) {
//                remotePlayer.onAttachUpdateInterfaceCallback(this);
//                remotePlayer.createRemoteConnection();
//            } else {
//                remotePlayer.release();
//            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        playerView = findViewById(R.id.player);

        playerConnection = new PlayerConnection(this);
        playerConnection.onStartService(bundle);
    }

    public void inflateMediaPlayer(MediaPlayer mediaPlayer) {
        PlayerState playerState = mediaPlayer.getPlayerState();

        // bind current active player
        Player player = mediaPlayer.getPlayer();
        if(player != null) {
            playerView.showController();
            playerView.setPlayer(player);
        }

        Store store = playerState.getStore();
        if(store != null) {

            // inflate the views
            Track track = store.getTrack(playerState.getPosition());

            Tools.setWeightedGradientBackground(getBaseContext(), binding.playerUnderlay,
                    track.getColor(this));

            binding.playerTrack.setText(track.getName());
            binding.playerAlbum.setText(track.getAlbumName());
            binding.playerArtist.setText(track.getArtistName());

            Image artwork = track.getArt();
            if(artwork != null) {
                Picasso
                        .get()
                        .load(artwork.getUrl())
                        .placeholder(R.drawable.ic_artwork_placeholder)
                        .error(R.drawable.ic_artwork_placeholder)
                        .into(binding.playerThumbnail);
            } else {
                binding.playerThumbnail.setImageBitmap(track.getBitmap(this));
            }

            Tools.setGradientBackground(this, binding.playerBackground,
                    track.getColor(this), 145);

            Tools.setStatusBarColor(this, track.getColor(this));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        speech.release();

//        if(playerView != null)
//            playerConnection.onDetachPlayerView();

        unbindService(playerConnection);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        speech.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPlayerServiceCallback(PlayerService playerService) {
        MediaPlayer mediaPlayer = playerService.getMediaPlayer();

        // inflate the views and manage player controller
        inflateMediaPlayer(mediaPlayer);
    }

    @Override
    public void onUpdateInterface(BluetoothDevice bluetoothDevice, boolean isActive) {
        if(isActive) {
            binding.playerDevices.setAlpha(1.0f);
            binding.playerDevices.setText(getString(R.string.player_share_message, bluetoothDevice.getName()));
        } else {
            binding.playerDevices.setAlpha(0.4f);
            binding.playerDevices.setText(null);
        }
    }
}