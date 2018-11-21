package com.example.cchiv.jiggles.activities;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.model.Album;
import com.example.cchiv.jiggles.model.Artist;
import com.example.cchiv.jiggles.model.Image;
import com.example.cchiv.jiggles.model.Track;
import com.example.cchiv.jiggles.player.PlayerRemote;
import com.example.cchiv.jiggles.services.PlayerService;
import com.example.cchiv.jiggles.services.PlayerServiceConnection;
import com.example.cchiv.jiggles.utilities.Tools;
import com.google.android.exoplayer2.ui.PlayerView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import jp.wasabeef.blurry.Blurry;

public class PlayerActivity extends AppCompatActivity implements
        PlayerService.OnCallbackListener, PlayerServiceConnection.OnCallbackConnectionComplete,
        PlayerRemote.OnUpdateInterface {

    private static final String TAG = "PlayerActivity";

    private static final int TRACK_LOADER_ID = 221;

    public static final String RESOURCE_ID = "RESOURCE_ID";
    public static final String RESOURCE_WHOLE = "RESOURCE_WHOLE";

    private boolean toolsToggle = false;

    public PlayerView playerView = null;

    private boolean resourceWhole;

    public PlayerServiceConnection playerServiceConnection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        findViewById(R.id.player_back).setOnClickListener((view) -> {
            finish();
        });

        findViewById(R.id.player_media_lyrics).setOnClickListener((view) -> {
            // Do something later with lyrics
        });

        findViewById(R.id.player_media_identifier).setOnClickListener((view) -> {
            // Do something later with identifier
        });

        findViewById(R.id.player_media_share).setOnClickListener((view) -> {
            PlayerRemote playerRemote = new PlayerRemote(this, playerServiceConnection.getMediaPlayer());
            playerRemote.createRemoteConnection();
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        playerView = findViewById(R.id.player);
        playerServiceConnection = new PlayerServiceConnection(this, playerView);
        playerServiceConnection.onStartService(bundle);
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

        Album album = track.getAlbum();
        Image artwork = album.getArt();

        ImageView thumbnail = findViewById(R.id.player_thumbnail);
        Picasso
                .get()
                .load(artwork.getUrl())
                .placeholder(R.drawable.ic_artwork_placeholder)
                .error(R.drawable.ic_artwork_placeholder)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        thumbnail.setImageBitmap(bitmap);

                        Blurry.with(getBaseContext())
                                .radius(6)
                                .sampling(3)
                                .async()
                                .from(bitmap)
                                .into(findViewById(R.id.player_underlay));
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        thumbnail.setImageDrawable(errorDrawable);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        thumbnail.setImageDrawable(placeHolderDrawable);
                    }
                });

        View view = findViewById(R.id.player_background);
        Tools.setGradientBackground(this, view, artwork.getColor(), 145);
        Tools.setStatusBarColor(this, artwork.getColor());

        Artist artist = album.getArtist();
        String artistName = artist.getName();

        TextView textArtistView = findViewById(R.id.player_artist);
        textArtistView.setText(artistName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(playerView != null)
            playerServiceConnection.onDetachPlayerView();

        unbindService(playerServiceConnection);
    }

    @Override
    public void onCallbackConnectionComplete() {
        Track track = playerServiceConnection.getCurrentTrack();
        if(track != null) {
            attachTrack(track);
        }
    }

    @Override
    public void onCallbackListener(Track track, int playbackStateCompat) {
        attachTrack(track);

        playerView.setPlayer(playerServiceConnection.getMediaPlayer().getExoPlayer());
        playerView.showController();
    }

    @Override
    public void onUpdateInterface(BluetoothDevice bluetoothDevice) {
        TextView textView = findViewById(R.id.player_devices);
        textView.setAlpha(1.0f);
        textView.setText(getString(R.string.player_share_message, bluetoothDevice.getName()));
    }
}