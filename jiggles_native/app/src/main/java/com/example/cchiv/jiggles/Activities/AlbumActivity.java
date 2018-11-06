package com.example.cchiv.jiggles.activities;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.example.cchiv.jiggles.utilities.JigglesLoader;
import com.example.cchiv.jiggles.utilities.Tools;
import com.squareup.picasso.Picasso;

public class AlbumActivity extends AppCompatActivity {

    private static final int LOADER_ALBUM_ID = 721;

    public static final String ALBUM_ID = "ALBUM_ID";

    private static final String TAG = "AlbumActivity";

    private ContentAdapter contentAdapter = new ContentAdapter(this, null, ContentAdapter.MODE_TRACK, id -> {
        createPlayerIntent(id, false);
    });;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_layout_album);

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

    private void createPlayerIntent(String resource, boolean whole) {
        Intent intent = new Intent(this, PlayerActivity.class);

        intent.putExtra(PlayerActivity.RESOURCE_ID, resource);
        intent.putExtra(PlayerActivity.RESOURCE_WHOLE, whole);

        startActivity(intent);
    }

    private void updateLayout(Collection collection) {
        Album album = collection.getAlbums().get(0);

        TextView albumPlayView = findViewById(R.id.album_play);
        albumPlayView.setOnClickListener((view) -> {
            createPlayerIntent(album.getId(), true);
        });

        ImageView thumbnail = findViewById(R.id.album_thumbnail);
        Image image = album.getArt();
        Picasso
                .get()
                .load(image.getUri())
                .placeholder(R.drawable.ic_artwork_placeholder)
                .error(R.drawable.ic_artwork_placeholder)
                .into(thumbnail);

        LinearLayout linearLayout = findViewById(R.id.album_background);
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[] {
                        image.getColor(),
                        ContextCompat.getColor(this, R.color.primaryTextColor)
                }
        );

        Tools.setStatusBarColor(this, image.getColor());

        ViewCompat.setBackground(linearLayout, gradientDrawable);

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
