package com.example.cchiv.jiggles.adapters;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class ModelAdapter<T extends RecyclerView.ViewHolder, B> extends RecyclerView.Adapter<T> {

    private static final String TAG = "ModelAdapter";

    public List<B> data = new ArrayList<>();

    public void onSwapData(List<B> objects) {
        data = objects;
    }

    public List<B> getData() {
        return data;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
