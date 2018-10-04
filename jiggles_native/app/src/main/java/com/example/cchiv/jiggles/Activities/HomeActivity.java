package com.example.cchiv.jiggles.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.cchiv.jiggles.Constants;
import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.adapters.NewsAdapter;
import com.example.cchiv.jiggles.adapters.ReleaseAdapter;
import com.example.cchiv.jiggles.data.ContentContract.NewsEntry;
import com.example.cchiv.jiggles.data.ContentContract.ReleaseEntry;
import com.example.cchiv.jiggles.model.News;
import com.example.cchiv.jiggles.model.Release;
import com.example.cchiv.jiggles.utilities.JigglesLoader;
import com.example.cchiv.jiggles.utilities.NetworkUtilities;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    private static final int HOME_NEWS_LOADER_ID = 21;
    private static final int HOME_RELEASES_LOADER_ID = 22;

    private NetworkUtilities networkUtilities;

    private NewsAdapter newsAdapter;
    private ReleaseAdapter releaseAdapter;

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
            Intent intent = new Intent(this, CollectionActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.home_forum).setOnClickListener(view -> {
            Intent intent = new Intent(this, ForumActivity.class);
            startActivity(intent);
        });

        RecyclerView recyclerNewsView = findViewById(R.id.home_news_list);
        newsAdapter = new NewsAdapter(this, new ArrayList<>());
        setRecyclerView(recyclerNewsView, newsAdapter);

        RecyclerView recyclerReleaseView = findViewById(R.id.home_release_list);
        releaseAdapter = new ReleaseAdapter(this, new ArrayList<>());
        setRecyclerView(recyclerReleaseView, releaseAdapter);

        networkUtilities = new NetworkUtilities();

        if(checkInternetConnectivity()){
            // fetchLiveContent();
        } else fetchCachedContent();
    }

    public void fetchCachedContent() {
        LoaderManager loaderManager = getSupportLoaderManager();

        JigglesLoader jigglesNewsLoader = new JigglesLoader(this, (this::updateLayoutNews));
        loaderManager.initLoader(HOME_NEWS_LOADER_ID, News.bundleValues(), jigglesNewsLoader).forceLoad();

        JigglesLoader jigglesReleasesLoader = new JigglesLoader(this, (this::updateLayoutReleases));
        loaderManager.initLoader(HOME_RELEASES_LOADER_ID, Release.bundleValues(), jigglesReleasesLoader).forceLoad();
    }

    public void setRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(true);

        RecyclerView.LayoutManager layoutManager =
                new GridLayoutManager(this,
                        getResources().getInteger(R.integer.layout_grid_columns_count),
                        LinearLayoutManager.VERTICAL,
                        false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }

    public void onClickSearch(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    private boolean checkInternetConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if(connectivityManager == null)
            return false;

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void fetchLiveContent() {
        networkUtilities.fetchReleases(releases -> {
            // Do something with releases
            if(releases != null) {
                ContentValues[] contentValues = new ContentValues[releases.size()];
                for(int it = 0; it < releases.size(); it++) {
                    contentValues[it] = Release.parseValues(releases.get(it));
                }

                long insertedRows = getContentResolver().bulkInsert(
                        ReleaseEntry.CONTENT_URI,
                        contentValues
                );

                updateLayoutReleases(releases);
            }
        });

        networkUtilities.fetchNews(news -> {
            if(news != null) {
                ContentValues[] contentValues = new ContentValues[news.size()];
                for(int it = 0; it < news.size(); it++) {
                    contentValues[it] = News.parseValues(news.get(it));
                }

                getContentResolver().bulkInsert(
                        NewsEntry.CONTENT_URI,
                        contentValues
                );

                updateLayoutNews(news);
            }
        });
    }

    public void updateLayoutReleases(Cursor cursor) {
        releaseAdapter.onSwapData(cursor);
        releaseAdapter.notifyDataSetChanged();
    }

    public void updateLayoutReleases(ArrayList<Release> releases) {
        releaseAdapter.onSwapData(releases);
        releaseAdapter.notifyDataSetChanged();
    }

    public void updateLayoutNews(Cursor cursor) {
        newsAdapter.onSwapData(cursor);
        newsAdapter.notifyDataSetChanged();
    }

    public void updateLayoutNews(ArrayList<News> news) {
        newsAdapter.onSwapData(news);
        newsAdapter.notifyDataSetChanged();
    }
}
