package com.example.cchiv.jiggles.utilities;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ItemScanner {

    private static final String TAG = "ItemScanner";

    private Context context;

    public ItemScanner(Context context) {
        this.context = context;
    }

    public List<String> scan() {
        AssetManager assetManager = context.getAssets();
        try {
            String[] files = assetManager.list("samples");

            return Arrays.asList(files);
        } catch(IOException e) {
            Log.v(TAG, e.toString());
        }

        return null;
    }
}
