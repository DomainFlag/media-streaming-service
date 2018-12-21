package com.example.cchiv.jiggles.activities;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.model.Image;
import com.example.cchiv.jiggles.model.Track;
import com.example.cchiv.jiggles.player.players.LocalPlayer;
import com.example.cchiv.jiggles.player.protocol.RemotePlayer;
import com.example.cchiv.jiggles.services.PlayerService;
import com.example.cchiv.jiggles.services.PlayerServiceConnection;
import com.example.cchiv.jiggles.utilities.Tools;
import com.google.android.exoplayer2.ui.PlayerView;
import com.squareup.picasso.Picasso;

public class PlayerActivity extends AppCompatActivity implements
        PlayerService.OnCallbackListener, PlayerServiceConnection.OnConnectionCallback,
        RemotePlayer.OnUpdateInterface {

    private static final String TAG = "PlayerActivity";

    private boolean toolsToggle = false;

    public PlayerView playerView = null;

    public PlayerServiceConnection playerServiceConnection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        findViewById(R.id.player_back).setOnClickListener((view) -> {
            finish();
        });

        findViewById(R.id.player_media_lyrics).setOnClickListener((view) -> {
            // Do something later with song lyrics
        });

        findViewById(R.id.player_media_identifier).setOnClickListener((view) -> {
            // Do something later with song identifier
        });

        findViewById(R.id.player_media_share).setOnClickListener((view) -> {
            LocalPlayer localPlayer = playerServiceConnection.getMediaPlayer().getLocalAlphaPlayer();
            RemotePlayer remotePlayer = localPlayer.getRemotePlayer();

            remotePlayer.onAttachUpdateInterfaceCallback(this);
            remotePlayer.createRemoteConnection();
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null) {
            playerView = findViewById(R.id.player);
            playerServiceConnection = new PlayerServiceConnection(this, playerView);
            playerServiceConnection.onStartService(bundle);
        }
    }

    public void attachTrack(Track track) {
        View tools = findViewById(R.id.player_utilities);
        findViewById(R.id.player_menu).setOnClickListener((view) -> {
            if(toolsToggle) {
                tools.setVisibility(View.GONE);
            } else {
                tools.setVisibility(View.VISIBLE);
            }

            toolsToggle = !toolsToggle;
        });

        TextView textTrackView = findViewById(R.id.player_track);
        textTrackView.setText(track.getName());

        Tools.setWeightedGradientBackground(getBaseContext(), findViewById(R.id.player_underlay), track.getColor(this));

        ImageView thumbnail = findViewById(R.id.player_thumbnail);
        Image artwork = track.getArt();
        if(artwork != null) {
            Picasso
                    .get()
                    .load(artwork.getUrl())
                    .placeholder(R.drawable.ic_artwork_placeholder)
                    .error(R.drawable.ic_artwork_placeholder)
                    .into(thumbnail);
        } else {
            thumbnail.setImageBitmap(track.getBitmap(this));
        }

        View view = findViewById(R.id.player_background);
        Tools.setGradientBackground(this, view, track.getColor(this), 145);
        Tools.setStatusBarColor(this, track.getColor(this));

        TextView textArtistView = findViewById(R.id.player_artist);
        textArtistView.setText(track.getArtistName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(playerView != null)
            playerServiceConnection.onDetachPlayerView();

        unbindService(playerServiceConnection);
    }

    @Override
    public void onConnectionCallbackComplete() {
        Track track = playerServiceConnection.getPlayerService().getCurrentTrack();
        if(track != null) {
            attachTrack(track);
        }
    }

    @Override
    public void onCallbackListener(Track track, int playbackStateCompat) {
        attachTrack(track);

        LocalPlayer localPlayer = playerServiceConnection.getMediaPlayer().getLocalAlphaPlayer();

        playerView.setPlayer(localPlayer.getExoPlayer());
        playerView.showController();
    }

    @Override
    public void onUpdateInterface(BluetoothDevice bluetoothDevice) {
        TextView textView = findViewById(R.id.player_devices);
        textView.setAlpha(1.0f);
        textView.setText(getString(R.string.player_share_message, bluetoothDevice.getName()));

        textView.setOnClickListener(view -> {
            playerServiceConnection.getMediaPlayer().detachTop();

            textView.setAlpha(0.4f);
            textView.setText(null);
        });
    }
}