package com.example.cchiv.jiggles.activities;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.model.Image;
import com.example.cchiv.jiggles.model.Track;
import com.example.cchiv.jiggles.player.MediaPlayer;
import com.example.cchiv.jiggles.player.players.LocalPlayer;
import com.example.cchiv.jiggles.player.protocol.RemotePlayer;
import com.example.cchiv.jiggles.prediction.Speech;
import com.example.cchiv.jiggles.services.PlayerService;
import com.example.cchiv.jiggles.services.PlayerServiceConnection;
import com.example.cchiv.jiggles.utilities.Tools;
import com.example.cchiv.jiggles.views.ToggleImageView;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerActivity extends AppCompatActivity implements
        PlayerService.OnCallbackListener, PlayerServiceConnection.OnConnectionCallback,
        RemotePlayer.OnUpdateInterface {

    private static final String TAG = "PlayerActivity";

    public PlayerView playerView = null;

    public PlayerServiceConnection playerServiceConnection = null;

    private Speech speech = new Speech(this, (action, position) -> {
        if(playerServiceConnection != null) {
            MediaPlayer mediaPlayer = playerServiceConnection.getMediaPlayer();

            switch(action) {
                case "left" : {
                    mediaPlayer.seekWindow(false);

                    break;
                }
                case "right" : {
                    mediaPlayer.seekWindow(true);

                    break;
                }
                case "stop" : {
                    mediaPlayer.togglePlayer(false);

                    break;
                }
                case "go" : {
                    mediaPlayer.togglePlayer(true);

                    break;
                }
            }
        }
    });

    @BindView(R.id.player_underlay) View playerUnderlayView;

    @BindView(R.id.player_track) TextView textTrackView;
    @BindView(R.id.player_album) TextView textAlbumView;
    @BindView(R.id.player_artist) TextView textArtistView;
    @BindView(R.id.player_thumbnail) ImageView imageThumbnailView;
    @BindView(R.id.player_background) LinearLayout imageBackgroundView;

    @BindView(R.id.player_menu) ToggleImageView toggleMenuImageView;
    @BindView(R.id.player_utilities) View utilitiesView;

    @BindView(R.id.player_media_lyrics) ToggleImageView toggleLyricsImageView;
    @BindView(R.id.player_media_identifier) ToggleImageView toggleIdentifierImageView;
    @BindView(R.id.player_media_share) ToggleImageView toggleShareImageView;

    @BindView(R.id.player_devices) TextView textDevicesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        ButterKnife.bind(this);

        // Back command
        findViewById(R.id.player_back).setOnClickListener((view) -> {
            finish();
        });

        // Menu utilities
        toggleMenuImageView.setOnActiveCallback((view, isActive) -> {
            if(isActive) {
                utilitiesView.setVisibility(View.VISIBLE);
            } else {
                utilitiesView.setVisibility(View.GONE);
            }
        });

        // Lyrics
        toggleLyricsImageView.setOnActiveCallback((view, isActive) -> {
            // Do something later with song lyrics
            if(isActive) {
                speech.initiate();
            } else {
                speech.release();
            }
        });

        // Identifier
        toggleIdentifierImageView.setOnActiveCallback(((view, isActive) -> {
            // Do something later with song identifier
        }));

        // Share
        toggleShareImageView.setOnActiveCallback((view, isActive) -> {
            LocalPlayer localPlayer = playerServiceConnection.getMediaPlayer().getLocalAlphaPlayer();
            RemotePlayer remotePlayer = localPlayer.getRemotePlayer();

            if(isActive) {
                remotePlayer.onAttachUpdateInterfaceCallback(this);
                remotePlayer.createRemoteConnection();
            } else {
                remotePlayer.release();
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        playerView = findViewById(R.id.player);

        playerServiceConnection = new PlayerServiceConnection(this, playerView);
        playerServiceConnection.onStartService(bundle);
    }

    public void attachTrack(Track track) {
        Tools.setWeightedGradientBackground(getBaseContext(), playerUnderlayView, track.getColor(this));

        textTrackView.setText(track.getName());
        textAlbumView.setText(track.getAlbumName());
        textArtistView.setText(track.getArtistName());

        Image artwork = track.getArt();
        if(artwork != null) {
            Picasso
                    .get()
                    .load(artwork.getUrl())
                    .placeholder(R.drawable.ic_artwork_placeholder)
                    .error(R.drawable.ic_artwork_placeholder)
                    .into(imageThumbnailView);
        } else {
            imageThumbnailView.setImageBitmap(track.getBitmap(this));
        }

        Tools.setGradientBackground(this, imageBackgroundView, track.getColor(this), 145);
        Tools.setStatusBarColor(this, track.getColor(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        speech.release();

        if(playerView != null)
            playerServiceConnection.onDetachPlayerView();

        unbindService(playerServiceConnection);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        speech.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onConnectionCallbackComplete() {
        Track track = playerServiceConnection.getPlayerService().getCurrentTrack();
        if(track != null) {
            attachTrack(track);
        }

        Player player = playerServiceConnection.getMediaPlayer().getCurrentPlayer();
        if(player != null) {
            playerView.setPlayer(player);
            playerView.showController();
        }
    }

    @Override
    public void onCallbackListener(Track track, int playbackStateCompat) {
        attachTrack(track);

        Player player = playerServiceConnection.getMediaPlayer().getCurrentPlayer();
        if(player != null) {
            playerView.setPlayer(player);
            playerView.showController();
        }
    }

    @Override
    public void onUpdateInterface(BluetoothDevice bluetoothDevice, boolean isActive) {
        if(isActive) {
            textDevicesView.setAlpha(1.0f);
            textDevicesView.setText(getString(R.string.player_share_message, bluetoothDevice.getName()));
        } else {
            textDevicesView.setAlpha(0.4f);
            textDevicesView.setText(null);
        }
    }
}