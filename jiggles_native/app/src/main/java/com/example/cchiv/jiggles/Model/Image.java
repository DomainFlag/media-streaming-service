package com.example.cchiv.jiggles.model;

import android.graphics.Bitmap;

public class Image {

    private int height;
    private String url;
    private Bitmap bitmap;
    private int width;

    public Image(int height, String url, int width) {
        this.height = height;
        this.url = url;
        this.width = width;
    }

    public Image(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getHeight() {
        return height;
    }

    public String getUrl() {
        return url;
    }

    public int getWidth() {
        return width;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}