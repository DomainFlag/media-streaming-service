package com.example.cchiv.jiggles.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.activities.AlbumActivity;
import com.example.cchiv.jiggles.adapters.ContentAdapter;
import com.example.cchiv.jiggles.data.ContentContract;
import com.example.cchiv.jiggles.model.Store;
import com.example.cchiv.jiggles.utilities.ItemScanner;
import com.example.cchiv.jiggles.utilities.JigglesLoader;

public class StoreFragment extends Fragment {

    private static final String TAG = "CollectionActivity";

    private static final int COLLECTION_LOADER_ID = 191;

    private Context context;

    private ContentAdapter contentAdapter = null;

    private final static int[] MENU_ITEMS = new int[] {
            R.id.filter_artist,
            R.id.filter_album,
            R.id.filter_track,
            R.id.filter_all
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_store_layout, container, false);

        getActivity().findViewById(R.id.home_menu).setOnClickListener((view) -> {
            PopupMenu popup = new PopupMenu(context, view);

            Menu menu = popup.getMenu();
            popup.getMenuInflater().inflate(R.menu.store_menu, menu);
            menu.findItem(R.id.collection_refresh).setOnMenuItemClickListener(item -> {
                resolveLocalMedia();

                return false;
            });

            for(int itemMenu : MENU_ITEMS) {
                menu.findItem(itemMenu).setOnMenuItemClickListener(menuItem -> {
                    Toast.makeText(context, "Filter by", Toast.LENGTH_SHORT).show();
                    return false;
                });
            }

            popup.show();
        });

        JigglesLoader jigglesLoader = new JigglesLoader<>(context,
                (JigglesLoader.OnPostLoaderCallback<Store>) this::onUpdateFragment,
                Store::parseCursor
        );

        Bundle args = new Bundle();
        args.putString(JigglesLoader.BUNDLE_URI_KEY, ContentContract.CONTENT_COLLECTION_URI.toString());

        LoaderManager loaderManager = ((AppCompatActivity) context).getSupportLoaderManager();
        loaderManager.initLoader(COLLECTION_LOADER_ID, args, jigglesLoader).forceLoad();

        contentAdapter = new ContentAdapter(context, null, ContentAdapter.MODE_ALBUM, store -> {
            Intent intent = new Intent(context, AlbumActivity.class);
            intent.putExtra(AlbumActivity.ALBUM_ID, store.getAlbum(0).getId());
            startActivity(intent);
        });

        RecyclerView recyclerView = rootView.findViewById(R.id.collection_list);
        recyclerView.setAdapter(contentAdapter);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }

    public void onUpdateFragment(Store store) {
        contentAdapter.swapCollection(store);
        contentAdapter.notifyDataSetChanged();
    }

    public void resolveLocalMedia() {
        ItemScanner.AsyncItemScanner asyncItemScanner = new ItemScanner.AsyncItemScanner(context, this::onUpdateFragment);
        asyncItemScanner.execute();
    }
}
