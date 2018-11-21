package com.example.cchiv.jiggles.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.adapters.ContentAdapter;
import com.example.cchiv.jiggles.data.ContentContract;
import com.example.cchiv.jiggles.model.Album;
import com.example.cchiv.jiggles.model.Artist;
import com.example.cchiv.jiggles.model.Collection;
import com.example.cchiv.jiggles.model.Image;
import com.example.cchiv.jiggles.services.PlayerService;
import com.example.cchiv.jiggles.utilities.JigglesLoader;
import com.example.cchiv.jiggles.utilities.Tools;
import com.squareup.picasso.Picasso;

public class AlbumActivity extends PlayerAppCompatActivity {

    private static final int LOADER_ALBUM_ID = 721;

    public static final String ALBUM_ID = "ALBUM_ID";

    private static final String TAG = "AlbumActivity";

    private ContentAdapter contentAdapter = new ContentAdapter(this, null, ContentAdapter.MODE_TRACK, id -> {
        createPlayerIntent(id, ContentContract.TrackEntry._ID);
    });;

    @Override
    protected void onCreateActivity(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_album);

        RecyclerView recyclerView = findViewById(R.id.album_list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(contentAdapter);


        Intent intent = getIntent();
        String id = intent.getStringExtra(ALBUM_ID);

        JigglesLoader<Collection> jigglesLoader = new JigglesLoader<>(this, (JigglesLoader
                .OnPostLoaderCallback<Collection>) this::updateLayout, Collection::parseCursor);

        Bundle bundle = new Bundle();
        bundle.putString(JigglesLoader.BUNDLE_URI_KEY, ContentContract.CONTENT_COLLECTION_URI.toString());
        bundle.putString(JigglesLoader.BUNDLE_SELECTION_KEY, ContentContract.AlbumEntry._ID + "=?");
        bundle.putStringArray(JigglesLoader.BUNDLE_SELECTION_ARGS_KEY, new String[] { id });

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(LOADER_ALBUM_ID, bundle, jigglesLoader).forceLoad();
    }

    @Override
    protected void onDestroyActivity() {}

    private void createPlayerIntent(String resourceId, String resourceType) {
        Intent intent = new Intent(this, PlayerActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString(PlayerService.RESOURCE_IDENTIFIER, resourceId);
        bundle.putString(PlayerService.RESOURCE_TYPE, resourceType);

        intent.putExtras(bundle);

        startActivity(intent);
    }

    private void updateLayout(Collection collection) {
        Album album = collection.getAlbums().get(0);

        TextView albumPlayView = findViewById(R.id.album_play);
        albumPlayView.setOnClickListener((view) -> {
            createPlayerIntent(album.getId(), ContentContract.AlbumEntry._ID);
        });

        ImageView thumbnail = findViewById(R.id.album_thumbnail);
        Image image = album.getArt();
        Picasso
                .get()
                .load(image.getUrl())
                .placeholder(R.drawable.ic_artwork_placeholder)
                .error(R.drawable.ic_artwork_placeholder)
                .into(thumbnail);


        LinearLayout linearLayout = findViewById(R.id.album_background);

        Tools.setGradientBackground(this, linearLayout, image.getColor(), 255);
        Tools.setStatusBarColor(this, image.getColor());

        Artist artist = album.getArtist();
        if(artist != null) {
            ((TextView) findViewById(R.id.album_genres))
                    .setText(artist.getGenres());

            ((TextView) findViewById(R.id.album_artist))
                    .setText(artist.getName());
        }

        ((TextView) findViewById(R.id.album_name))
                .setText(album.getName());


        contentAdapter.swapCollection(collection);
        contentAdapter.notifyDataSetChanged();
    }
}
