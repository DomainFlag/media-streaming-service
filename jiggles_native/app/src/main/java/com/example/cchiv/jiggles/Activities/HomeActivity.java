package com.example.cchiv.jiggles.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.cchiv.jiggles.Adapters.NewsAdapter;
import com.example.cchiv.jiggles.Adapters.ReleaseAdapter;
import com.example.cchiv.jiggles.Constants;
import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.Utilities.NetworkUtilities;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    private static final int GRID_COLS = 2;

    private NetworkUtilities networkUtilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.AUTH_TOKEN, MODE_PRIVATE);
        String token = sharedPreferences.getString(Constants.TOKEN, null);
        if(token != null) {
            // Do something
        } else {
            finish();
        }

        findViewById(R.id.home_player).setOnClickListener(view -> {
            Intent intent = new Intent(this, PlayerActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.home_forum).setOnClickListener(view -> {
            Intent intent = new Intent(this, ForumActivity.class);
            startActivity(intent);
        });

        networkUtilities = new NetworkUtilities();

        fetchReleases();
        fetchNews();
    }

    public void onClickSearch(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void fetchReleases() {
        networkUtilities.fetchReleases(releases -> {
            // Do something with releases
            if(releases != null) {
                // Do something with Releases
                RecyclerView recyclerView = findViewById(R.id.home_release_list);
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(true);

                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,
                        GRID_COLS,
                        LinearLayoutManager.VERTICAL,
                        false);
                recyclerView.setLayoutManager(layoutManager);

                ReleaseAdapter releaseAdapter = new ReleaseAdapter(this, releases);
                recyclerView.setAdapter(releaseAdapter);
            }
        });
    }

    public void fetchNews() {
        networkUtilities.fetchNews(news -> {
            if(news != null) {
                // Do something with News
                RecyclerView recyclerView = findViewById(R.id.home_news_list);
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(true);

                RecyclerView.LayoutManager layoutManager =
                        new GridLayoutManager(this,
                                GRID_COLS,
                                LinearLayoutManager.VERTICAL,
                                false);
                recyclerView.setLayoutManager(layoutManager);

                NewsAdapter newsAdapter = new NewsAdapter(this, news);
                recyclerView.setAdapter(newsAdapter);
            }
        });
    }
}
