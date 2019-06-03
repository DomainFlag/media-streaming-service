package com.example.cchiv.jiggles.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.adapters.ContentAdapter;
import com.example.cchiv.jiggles.data.ContentContract;
import com.example.cchiv.jiggles.data.ContentContract.AlbumEntry;
import com.example.cchiv.jiggles.data.ContentContract.TrackEntry;
import com.example.cchiv.jiggles.model.player.Store;
import com.example.cchiv.jiggles.model.player.content.Album;
import com.example.cchiv.jiggles.services.PlayerService;
import com.example.cchiv.jiggles.utilities.JigglesLoader;

public class AlbumActivity extends PlayerAppCompatActivity {

    private static final int LOADER_ALBUM_ID = 721;

    public static final String ALBUM_ID = "ALBUM_ID";

    private static final String TAG = "AlbumActivity";

    private ContentAdapter contentTrackAdapter = new ContentAdapter(this, null,
            ContentAdapter.MODE_TRACK, this::createPlayerIntent);

    private ContentAdapter contentAlbumAdapter;

    @Override
    protected void onCreateActivity(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_album);

        contentAlbumAdapter = new ContentAdapter(this, null, ContentAdapter.MODE_SCROLL,
                findViewById(R.id.album_background), (album, position) -> {});

        RecyclerView recyclerView = createRecyclerView(contentAlbumAdapter, RecyclerView.HORIZONTAL, R.id.album_list);

        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(recyclerView);

        createRecyclerView(contentTrackAdapter, RecyclerView.VERTICAL, R.id.album_tracks);

        Intent intent = getIntent();
        String id = intent.getStringExtra(ALBUM_ID);

        JigglesLoader<Store> jigglesLoader = new JigglesLoader<>(this, (JigglesLoader
                .OnPostLoaderCallback<Store>) this::updateLayout, Store::parseCursor);

        Bundle bundle = new Bundle();
        bundle.putString(JigglesLoader.BUNDLE_URI_KEY, ContentContract.CONTENT_COLLECTION_URI.toString());
        bundle.putString(JigglesLoader.BUNDLE_SELECTION_KEY, ContentContract.AlbumEntry._ID + "=?");
        bundle.putStringArray(JigglesLoader.BUNDLE_SELECTION_ARGS_KEY, new String[] { id });

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(LOADER_ALBUM_ID, bundle, jigglesLoader).forceLoad();
    }

    public RecyclerView createRecyclerView(ContentAdapter contentAdapter, int orientation, int resourceView) {
        RecyclerView recyclerView = findViewById(resourceView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                orientation, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(contentAdapter);

        return recyclerView;
    }

    private void createPlayerIntent(Album album, int position) {
        Intent intent = new Intent(this, PlayerActivity.class);

        Log.v(TAG, String.valueOf(position));

        Bundle bundle = new Bundle();
        bundle.putString(PlayerService.RESOURCE_SOURCE, PlayerService.RESOURCE_LOCAL);
        bundle.putInt(PlayerService.RESOURCE_IDENTIFIER, position);
        bundle.putString(PlayerService.RESOURCE_TYPE, TrackEntry._ID);
        bundle.putString(PlayerService.RESOURCE_PARENT_IDENTIFIER, album.getId());
        bundle.putString(PlayerService.RESOURCE_PARENT_TYPE, AlbumEntry._ID);

        intent.putExtras(bundle);

        startActivity(intent);
    }

    private void updateLayout(Store store) {
        contentAlbumAdapter.swapCollection(store);
        contentAlbumAdapter.notifyDataSetChanged();

        contentTrackAdapter.swapCollection(store);
        contentTrackAdapter.notifyDataSetChanged();
    }
}
