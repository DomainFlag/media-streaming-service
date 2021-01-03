package com.example.cchiv.jiggles.fragments;

import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.adapters.FeatureAdapter;
import com.example.cchiv.jiggles.adapters.FeatureAlbumAdapter;
import com.example.cchiv.jiggles.adapters.FeatureLatestAdapter;
import com.example.cchiv.jiggles.data.ContentContract;
import com.example.cchiv.jiggles.data.ContentContract.NewsEntry;
import com.example.cchiv.jiggles.model.News;
import com.example.cchiv.jiggles.model.Release;
import com.example.cchiv.jiggles.utilities.JigglesLoader;
import com.example.cchiv.jiggles.utilities.NetworkUtilities;
import com.example.cchiv.jiggles.utilities.Tools;

import java.util.List;

public class LatestFragment extends Fragment {

    private static final String TAG = "LatestFragment";

    private static final int HOME_NEWS_LOADER_ID = 21;
    private static final int HOME_RELEASES_LOADER_ID = 22;

    private SwipeRefreshLayout swipeRefreshLayout;

    private FeatureLatestAdapter featureLatestAdapter;
    private FeatureAlbumAdapter featureAlbumAdapter;

    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_latest_layout, container, false);

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if(Tools.checkInternetConnectivity(context)) {
                fetchLiveReleasesContent();
                fetchLiveNewsContent();
            } else {
                Toast.makeText(context, R.string.app_connectivity_negative, Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        featureLatestAdapter = new FeatureLatestAdapter(context, null, "Latest", rootView.findViewById(R.id.feature_latest));
        featureAlbumAdapter = new FeatureAlbumAdapter(context, null, "New", rootView.findViewById(R.id.feature_highlights));

        fetchCachedContent();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        featureAlbumAdapter.resume();
        featureLatestAdapter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();

        featureAlbumAdapter.pause();
        featureLatestAdapter.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        featureAlbumAdapter.release();
        featureLatestAdapter.release();
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

    public void fetchLiveReleasesContent() {
        NetworkUtilities.FetchReleases fetchReleases = new NetworkUtilities.FetchReleases(releases -> {
            // Do something with releases
            if(releases != null) {
                try {
                    ContentProviderResult[] results = context.getContentResolver()
                            .applyBatch(ContentContract.AUTHORITY, Release.parseValues(releases));

                    // Do something with the results
                } catch (RemoteException e) {
                    Log.v(TAG, e.toString());
                } catch (OperationApplicationException e) {
                    Log.v(TAG, e.toString());
                }

                updateLayoutReleases(releases);
            }

            swipeRefreshLayout.setRefreshing(false);
        });
    }

    public void fetchLiveNewsContent() {
        NetworkUtilities.FetchNews fetchNews = new NetworkUtilities.FetchNews(news -> {
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
        if(news == null || news.isEmpty()) {
            if(Tools.checkInternetConnectivity(context))
                fetchLiveNewsContent();
        }

        FeatureAdapter.Feature feature = featureLatestAdapter.onCreateFeature("Latest", news);
        featureLatestAdapter.onSwapData(feature);
    }

    public void updateLayoutReleases(List<Release> releases) {
        if(releases == null || releases.isEmpty()) {
            if(Tools.checkInternetConnectivity(context))
                fetchLiveReleasesContent();
        }

        FeatureAdapter.Feature feature = featureAlbumAdapter.onCreateFeature("New", releases);
        featureAlbumAdapter.onSwapData(feature);
    }
}
