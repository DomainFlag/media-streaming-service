package com.example.cchiv.jiggles.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.adapters.ContentAdapter;
import com.example.cchiv.jiggles.model.Collection;
import com.example.cchiv.jiggles.utilities.ItemScanner;

public class CollectionActivity extends AppCompatActivity {

    private static final String TAG = "CollectionActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        Collection collection = ItemScanner.media(this);

        final ContentAdapter contentAdapter;
        contentAdapter = new ContentAdapter(this, (album, track) -> {
            Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
            intent.putExtra("albumIndex", album);
            intent.putExtra("trackIndex", track);
            startActivity(intent);
        }, collection);

        RecyclerView recyclerView = findViewById(R.id.collection_list);
        recyclerView.setAdapter(contentAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setHasFixedSize(true);


        findViewById(R.id.collection_menu).setOnClickListener((view) -> {
            PopupMenu popup = new PopupMenu(this, view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.collection_menu, popup.getMenu());

            Menu menu = popup.getMenu();

            menu.findItem(R.id.filter_album).setOnMenuItemClickListener(menuItem -> {
                Toast.makeText(this, "Filter by album", Toast.LENGTH_SHORT).show();
                return false;
            });

            menu.findItem(R.id.filter_artist).setOnMenuItemClickListener(menuItem -> {
                Toast.makeText(this, "Filter by artist", Toast.LENGTH_SHORT).show();
                return false;
            });

            menu.findItem(R.id.filter_track).setOnMenuItemClickListener(menuItem -> {
                Toast.makeText(this, "Filter by track", Toast.LENGTH_SHORT).show();
                return false;
            });

            menu.findItem(R.id.filter_all).setOnMenuItemClickListener(menuItem -> {
                Toast.makeText(this, "Filter by all", Toast.LENGTH_SHORT).show();
                return false;
            });

            popup.show();
        });
    }
}
