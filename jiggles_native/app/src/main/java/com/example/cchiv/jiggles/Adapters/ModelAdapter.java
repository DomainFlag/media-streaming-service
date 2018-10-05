package com.example.cchiv.jiggles.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public abstract class ModelAdapter<T extends RecyclerView.ViewHolder, B> extends RecyclerView.Adapter<T> {

    private static final String TAG = "ModelAdapter";

    public ArrayList<B> data = new ArrayList<>();
    private Cursor cursor = null;

    public void onSwapData(ArrayList<B> objects) {
        data.clear();
        data.addAll(objects);
    }

    public void onSwapData(Cursor cursor) {
        if(this.cursor != null)
            this.cursor.close();

        this.cursor = cursor;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public ArrayList<B> getData() {
        return data;
    }

    @Override
    public int getItemCount() {
        if(getCursor() != null)
            return getCursor().getCount();
        else return data.size();
    }
}
