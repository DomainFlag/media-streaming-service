package com.example.cchiv.jiggles.utilities;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import androidx.loader.content.AsyncTaskLoader;

public abstract class JigglesAsyncTaskLoader<T> extends AsyncTaskLoader<T> {

    public interface OnExecuteCallback<T> {
        T onDoInBackground(Cursor cursor);
    }

    private Bundle bundle = null;
    private OnExecuteCallback<T> onPostLoaderCallback;

    public JigglesAsyncTaskLoader(Context context) {
        super(context);
    }

    public JigglesAsyncTaskLoader(Context context, Bundle bundle, OnExecuteCallback<T> onPostLoaderCallback) {
        super(context);

        this.bundle = bundle;
        this.onPostLoaderCallback = onPostLoaderCallback;
    }

    @Override
    abstract public T loadInBackground();

    public Bundle getBundle() {
        return bundle;
    }

    public OnExecuteCallback<T> getOnPostLoaderCallback() {
        return onPostLoaderCallback;
    }
}
