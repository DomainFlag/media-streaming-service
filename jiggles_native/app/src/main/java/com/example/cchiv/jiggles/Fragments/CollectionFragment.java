package com.example.cchiv.jiggles.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.adapters.ContentAdapter;
import com.example.cchiv.jiggles.model.Collection;

public class CollectionFragment extends Fragment {

    private static final String TAG = "CollectionFragment";

    private Context context;

    private Collection collection = null;

    private View rootView = null;

    private ContentAdapter.OnClickAlbumListener onClickAlbumListener;

    private ContentAdapter contentAdapter = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_layout_collection, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.collection_list);

        if(contentAdapter == null)
            contentAdapter = new ContentAdapter(context, onClickAlbumListener, collection);
        recyclerView.setAdapter(contentAdapter);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        updateLayout();

        return rootView;
    }

    private void updateLayout() {
        rootView.findViewById(R.id.collection_menu).setOnClickListener((view) -> {
            PopupMenu popup = new PopupMenu(context, view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.collection_menu, popup.getMenu());

            Menu menu = popup.getMenu();

            menu.findItem(R.id.filter_album).setOnMenuItemClickListener(menuItem -> {
                Toast.makeText(context, "Filter by album", Toast.LENGTH_SHORT).show();
                return false;
            });

            menu.findItem(R.id.filter_artist).setOnMenuItemClickListener(menuItem -> {
                Toast.makeText(context, "Filter by artist", Toast.LENGTH_SHORT).show();
                return false;
            });

            menu.findItem(R.id.filter_track).setOnMenuItemClickListener(menuItem -> {
                Toast.makeText(context, "Filter by track", Toast.LENGTH_SHORT).show();
                return false;
            });

            menu.findItem(R.id.filter_all).setOnMenuItemClickListener(menuItem -> {
                Toast.makeText(context, "Filter by all", Toast.LENGTH_SHORT).show();
                return false;
            });

            popup.show();
        });
    }

    public void onAttachOnClickAlbumListener(ContentAdapter.OnClickAlbumListener onClickAlbumListener) {
        this.onClickAlbumListener = onClickAlbumListener;
    }

    public void onAttachCollection(Collection collection) {
        this.collection = collection;
    }
}
