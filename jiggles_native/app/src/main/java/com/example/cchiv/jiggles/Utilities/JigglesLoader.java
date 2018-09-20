package com.example.cchiv.jiggles.utilities;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

public class JigglesLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "JigglesLoader";

    public static final String BUNDLE_URI_KEY = "BUNDLE_URI_KEY";
    public static final String BUNDLE_PROJECTION_KEY = "BUNDLE_PROJECTION_KEY";
    public static final String BUNDLE_SELECTION_KEY = "BUNDLE_SELECTION_KEY";
    public static final String BUNDLE_SELECTION_ARGS_KEY = "BUNDLE_SELECTION_ARGS_KEY";
    public static final String BUNDLE_SORT_ORDER_KEY = "BUNDLE_SORT_ORDER_KEY";

    public interface OnPostLoaderCallback {
        void onPostLoaderCallback(Cursor cursor);
    }

    private OnPostLoaderCallback onPostLoaderCallback;

    private Context context;

    public JigglesLoader(Context context, OnPostLoaderCallback onPostLoaderCallback) {
        this.context = context;

        this.onPostLoaderCallback = onPostLoaderCallback;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new AsyncTaskContentLoader(context, bundle);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        onPostLoaderCallback.onPostLoaderCallback(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public static class AsyncTaskContentLoader extends AsyncTaskLoader<Cursor> {

        private Bundle bundle = null;

        private AsyncTaskContentLoader(Context context) {
            super(context);
        }

        private AsyncTaskContentLoader(Context context, Bundle bundle) {
            super(context);

            this.bundle = bundle;
        }

        @Override
        public Cursor loadInBackground() {
            ContentResolver contentResolver = getContext().getContentResolver();

            if(bundle != null) {
                return contentResolver.query(
                        Uri.parse(bundle.getString(BUNDLE_URI_KEY)),
                        bundle.getStringArray(BUNDLE_PROJECTION_KEY),
                        bundle.getString(BUNDLE_SELECTION_KEY, null),
                        bundle.getStringArray(BUNDLE_SELECTION_ARGS_KEY),
                        bundle.getString(BUNDLE_SORT_ORDER_KEY, null)
                );
            } else return null;
        }
    }
}
