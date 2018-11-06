package com.example.cchiv.jiggles.activities;

import android.content.Intent;
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
import com.example.cchiv.jiggles.services.PlayerService;
import com.example.cchiv.jiggles.utilities.Tools;
import com.squareup.picasso.Picasso;

public class PlayerActivity extends AppCompatActivity {

    private static final String TAG = "PlayerActivity";

    private static final int TRACK_LOADER_ID = 221;

    public static final String RESOURCE_ID = "RESOURCE_ID";
    public static final String RESOURCE_WHOLE = "RESOURCE_WHOLE";

    private boolean toolsToggle = false;

    private boolean resourceWhole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        findViewById(R.id.player_lyrics).setOnClickListener((view) -> {
            // Do something later with lyrics
        });

        findViewById(R.id.player_back).setOnClickListener((view) -> {
            finish();
        });

        Intent intent = getIntent();
        String id = intent.getStringExtra(RESOURCE_ID);
        resourceWhole = intent.getBooleanExtra(RESOURCE_WHOLE, false);

        if(id == null)
            finish();

        Intent serviceIntent = new Intent(this, PlayerService.class);
        Bundle bundle = new Bundle();
        bundle.putString(PlayerService.RESOURCE_IDENTIFIER, id);

        serviceIntent.putExtras(bundle);
        startService(serviceIntent);
    }

    public void onAttachTrack(Track track) {
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
                .load(artwork.getUri())
                .placeholder(R.drawable.ic_artwork_placeholder)
                .error(R.drawable.ic_artwork_placeholder)
                .into(thumbnail);

        View view = findViewById(R.id.player_background);
        Tools.setGradientBackground(this, view, artwork.getColor());
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
    }
}