package com.example.cchiv.jiggles.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class ModelAdapter<T extends RecyclerView.ViewHolder, B> extends RecyclerView.Adapter<T> {

    private static final String TAG = "ModelAdapter";

    public List<B> data = new ArrayList<>();
    private Cursor cursor = null;

    public void onSwapData(List<B> objects) {
        data.clear();
        data.addAll(objects);
    }

    public Cursor getCursor() {
        return cursor;
    }

    public List<B> getData() {
        return data;
    }

    @Override
    public int getItemCount() {
        if(getCursor() != null)
            return getCursor().getCount();
        else return data.size();
    }
}
