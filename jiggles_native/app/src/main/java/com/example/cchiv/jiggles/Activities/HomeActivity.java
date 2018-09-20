package com.example.cchiv.jiggles.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    private static final int HOME_LOADER_ID = 21;

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

        if(checkInternetConnectivity()) {
            fetchReleases();
            fetchNews();
        } else {
            JigglesLoader jigglesLoader = new JigglesLoader(this, (cursor -> {
                Log.v(TAG, String.valueOf(cursor.getCount()));
            }));

            Bundle bundle = new Bundle();
            bundle.putString(JigglesLoader.BUNDLE_URI_KEY, NewsEntry.CONTENT_URI.toString());
            bundle.putStringArray(JigglesLoader.BUNDLE_PROJECTION_KEY, new String[] {
                    NewsEntry._ID,
                    NewsEntry.COL_NEWS_IDENTIFIER,
                    NewsEntry.COL_NEWS_CAPTION,
                    NewsEntry.COL_NEWS_HEADER,
                    NewsEntry.COL_NEWS_AUTHOR
            });

            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(HOME_LOADER_ID, bundle, jigglesLoader).forceLoad();
        }
    }

    private boolean checkInternetConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if(connectivityManager == null)
            return false;

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
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

                ContentValues[] contentValues = new ContentValues[releases.size()];
                for(int it = 0; it < releases.size(); it++) {
                    contentValues[it] = parseReleaseValues(releases.get(it));
                }

                long insertedRows = getContentResolver().bulkInsert(
                        ReleaseEntry.CONTENT_URI,
                        contentValues
                );

                Log.v(TAG, String.valueOf(insertedRows));
            }
        });
    }

    public ContentValues parseReleaseValues(Release release) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ReleaseEntry.COL_RELEASE_IDENTIFIER, release.get_id());
        contentValues.put(ReleaseEntry.COL_RELEASE_ARTIST, release.getArtist());
        contentValues.put(ReleaseEntry.COL_RELEASE_TITLE, release.getTitle());
        contentValues.put(ReleaseEntry.COL_RELEASE_URL, release.getUrl());

        return contentValues;
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

                ContentValues[] contentValues = new ContentValues[news.size()];
                for(int it = 0; it < news.size(); it++) {
                    contentValues[it] = parseNewsValues(news.get(it));
                }

                getContentResolver().bulkInsert(
                        NewsEntry.CONTENT_URI,
                        contentValues
                );
            }
        });
    }

    public ContentValues parseNewsValues(News news) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(NewsEntry.COL_NEWS_IDENTIFIER, news.get_id());
        contentValues.put(NewsEntry.COL_NEWS_AUTHOR, news.getAuthor());
        contentValues.put(NewsEntry.COL_NEWS_CAPTION, news.getCaption());
        contentValues.put(NewsEntry.COL_NEWS_HEADER, news.getHeader());

        return contentValues;
    }
}
