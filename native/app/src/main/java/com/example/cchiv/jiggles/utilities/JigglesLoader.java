package com.example.cchiv.jiggles.utilities;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

public class JigglesLoader<T> implements LoaderManager.LoaderCallbacks<T> {

    private static final String TAG = "JigglesLoader";

    public static final String BUNDLE_URI_KEY = "BUNDLE_URI_KEY";
    public static final String BUNDLE_PROJECTION_KEY = "BUNDLE_PROJECTION_KEY";
    public static final String BUNDLE_SELECTION_KEY = "BUNDLE_SELECTION_KEY";
    public static final String BUNDLE_SELECTION_ARGS_KEY = "BUNDLE_SELECTION_ARGS_KEY";
    public static final String BUNDLE_SORT_ORDER_KEY = "BUNDLE_SORT_ORDER_KEY";

    public interface OnPostLoaderCallback<T> {
        void onPostLoaderCallback(T result);
    }

    private OnPostLoaderCallback onPostLoaderCallback = null;
    private JigglesAsyncTaskLoader.OnExecuteCallback<T> onExecuteCallback = null;

    private Context context;

    public JigglesLoader(Context context, OnPostLoaderCallback onPostLoaderCallback,
                         JigglesAsyncTaskLoader.OnExecuteCallback<T> onExecuteCallback) {
        this.context = context;

        this.onPostLoaderCallback = onPostLoaderCallback;
        this.onExecuteCallback = onExecuteCallback;
    }

    @NonNull
    public Loader<T> onCreateLoader(int i, Bundle bundle) {
        return new AsyncTaskContentLoader<T>(context, bundle, onExecuteCallback);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<T> loader, T data) {
        onPostLoaderCallback.onPostLoaderCallback(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<T> loader) {}

    public static class AsyncTaskContentLoader<T> extends JigglesAsyncTaskLoader<T> {

        public AsyncTaskContentLoader(Context context, Bundle bundle, OnExecuteCallback<T> onExecuteCallback) {
            super(context, bundle, onExecuteCallback);
        }

        @Override
        public T loadInBackground() {
            ContentResolver contentResolver = getContext().getContentResolver();

            Bundle bundle = getBundle();

            Cursor cursor = null;
            if(bundle != null) {
                cursor = contentResolver.query(
                        Uri.parse(bundle.getString(BUNDLE_URI_KEY)),
                        bundle.getStringArray(BUNDLE_PROJECTION_KEY),
                        bundle.getString(BUNDLE_SELECTION_KEY, null),
                        bundle.getStringArray(BUNDLE_SELECTION_ARGS_KEY),
                        bundle.getString(BUNDLE_SORT_ORDER_KEY, null)
                );
            }

            return getOnPostLoaderCallback().onDoInBackground(cursor);
        }
    }
}
