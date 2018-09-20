package com.example.cchiv.jiggles.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.cchiv.jiggles.adapters.ThreadAdapter;
import com.example.cchiv.jiggles.fragments.ThreadCreatorFragment;
import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.utilities.NetworkUtilities;

public class ForumActivity extends AppCompatActivity {

    private static final String TAG = "ForumActivity";

    private RecyclerView recyclerView;
    private ThreadAdapter threadAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        findViewById(R.id.forum_thread_creator).setOnClickListener((view) -> {
            Fragment fragment = new ThreadCreatorFragment();

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.forum_thread, fragment)
                    .commit();
        });

        recyclerView = findViewById(R.id.forum_list);
        recyclerView.setNestedScrollingEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        threadAdapter = new ThreadAdapter(this, null);
        recyclerView.setAdapter(threadAdapter);

        NetworkUtilities networkUtilities = new NetworkUtilities();
        networkUtilities.fetchThreads((threads) -> {
            threadAdapter.swapContent(threads);
            threadAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.forum_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.menu.forum_menu : {
                // Handle the forum menu event
                Log.v(TAG, String.valueOf(R.menu.forum_menu));
            }
            default: return false;
        }
    }
}
