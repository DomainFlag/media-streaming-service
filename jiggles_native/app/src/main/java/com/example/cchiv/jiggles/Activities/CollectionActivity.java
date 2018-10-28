package com.example.cchiv.jiggles.activities;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.fragments.AlbumFragment;
import com.example.cchiv.jiggles.fragments.CollectionFragment;
import com.example.cchiv.jiggles.model.Collection;
import com.example.cchiv.jiggles.utilities.ItemScanner;
import com.example.cchiv.jiggles.utilities.JigglesLoader;

public class CollectionActivity extends AppCompatActivity {

    private static final String TAG = "CollectionActivity";

    private static final String FRAGMENT_ALBUM_KEY = "FRAGMENT_ALBUM_KEY";
    private static final String FRAGMENT_COLLECTION_KEY = "FRAGMENT_COLLECTION_KEY";

    private CollectionFragment collectionFragment;
    private AlbumFragment albumFragment = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        collectionFragment = new CollectionFragment();
        collectionFragment.onAttach(this);
        collectionFragment.onAttachOnClickAlbumListener(
                (album, position) -> {
                    albumFragment = new AlbumFragment();
                    albumFragment.onAttach(this);
                    albumFragment.onAttachAlbum(album, position);

                    getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_candy, albumFragment, FRAGMENT_ALBUM_KEY)
                            .commit();
                }
        );

        Collection collection = ItemScanner.resolveLocalMedia(this);
        ItemScanner.resolveCachedMedia(this, new JigglesLoader.OnPostLoaderCallback() {
            @Override
            public void onPostLoaderCallback(Cursor cursor) {
                Log.v(TAG, String.valueOf(cursor.getCount()));
                Collection.parseCursor(cursor);
            }
        });

        collectionFragment.onAttachCollection(collection);
        getFragmentManager().beginTransaction()
                .add(R.id.fragment_candy, collectionFragment, FRAGMENT_COLLECTION_KEY)
                .commit();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getFragmentManager().findFragmentByTag(FRAGMENT_ALBUM_KEY);
        if(fragment != null && fragment.isVisible()) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_candy, collectionFragment)
                    .commit();
        } else super.onBackPressed();
    }
}
