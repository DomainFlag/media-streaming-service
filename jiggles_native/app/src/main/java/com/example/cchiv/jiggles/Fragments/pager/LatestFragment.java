package com.example.cchiv.jiggles.fragments.pager;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.adapters.HighlightAdapter;
import com.example.cchiv.jiggles.adapters.ReleaseAdapter;
import com.example.cchiv.jiggles.data.ContentContract.NewsEntry;
import com.example.cchiv.jiggles.data.ContentContract.ReleaseEntry;
import com.example.cchiv.jiggles.model.News;
import com.example.cchiv.jiggles.model.Release;
import com.example.cchiv.jiggles.utilities.JigglesLoader;
import com.example.cchiv.jiggles.utilities.NetworkUtilities;
import com.example.cchiv.jiggles.utilities.Tools;

import java.util.ArrayList;
import java.util.List;

public class LatestFragment extends Fragment {

    private static final String TAG = "LatestFragment";

    private static final int HOME_NEWS_LOADER_ID = 21;
    private static final int HOME_RELEASES_LOADER_ID = 22;

    private NetworkUtilities networkUtilities = new NetworkUtilities();;

    private HighlightAdapter highlightAdapter;
    private ReleaseAdapter releaseAdapter;

    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_latest_layout, container, false);

        SwipeRefreshLayout swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if(Tools.checkInternetConnectivity(context))
                fetchLiveContent();
        });

        highlightAdapter = new HighlightAdapter(context, new ArrayList<>());
        createListView(rootView, R.id.home_news_list, highlightAdapter);

        releaseAdapter = new ReleaseAdapter(context, new ArrayList<>());
        createListView(rootView, R.id.home_release_list, releaseAdapter);

        fetchCachedContent();

        return rootView;
    }

    public void createListView(View rootView, int resource, RecyclerView.Adapter adapter) {
        RecyclerView recyclerView = rootView.findViewById(resource);
        setRecyclerView(recyclerView, adapter);
    }

    public void fetchCachedContent() {
        LoaderManager loaderManager = ((AppCompatActivity) context).getSupportLoaderManager();

        JigglesLoader jigglesNewsLoader = new JigglesLoader<>(context,
                (JigglesLoader.OnPostLoaderCallback<List<News>>) this::updateLayoutNews,
                News::parseValues
        );

        loaderManager.initLoader(HOME_NEWS_LOADER_ID, News.bundleValues(), jigglesNewsLoader).forceLoad();

        JigglesLoader jigglesReleasesLoader = new JigglesLoader<>(context,
                (JigglesLoader.OnPostLoaderCallback<List<Release>>) this::updateLayoutReleases,
                Release::parseValues
        );

        loaderManager.initLoader(HOME_RELEASES_LOADER_ID, Release.bundleValues(), jigglesReleasesLoader).forceLoad();
    }

    public void setRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        RecyclerView.LayoutManager layoutManager =
                new GridLayoutManager(context,
                        getResources().getInteger(R.integer.layout_grid_columns_count),
                        LinearLayoutManager.VERTICAL,
                        false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }

    public void fetchLiveContent() {
        networkUtilities.fetchReleases(releases -> {
            // Do something with releases
            if(releases != null) {
                ContentValues[] contentValues = new ContentValues[releases.size()];
                for(int it = 0; it < releases.size(); it++) {
                    contentValues[it] = Release.parseValues(releases.get(it));
                }

                long insertedRows = context.getContentResolver().bulkInsert(
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

                context.getContentResolver().bulkInsert(
                        NewsEntry.CONTENT_URI,
                        contentValues
                );

                updateLayoutNews(news);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }

    public void updateLayoutNews(List<News> news) {
        highlightAdapter.onSwapData(news);
        highlightAdapter.notifyDataSetChanged();
    }

    public void updateLayoutReleases(List<Release> releases) {
        releaseAdapter.onSwapData(releases);
        releaseAdapter.notifyDataSetChanged();
    }
}
