package com.example.cchiv.jiggles.model;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;

import com.example.cchiv.jiggles.data.ContentContract.ImageEntry;

public class Image {

    public int height;
    public int width;
    public int color;
    private Uri uri = null;
    private Bitmap bitmap = null;

    public Image(Uri uri, int height, int width) {
        this.height = height;
        this.width = width;
        this.uri = uri;
    }

    public Image(Uri uri) {
        this.uri = uri;
    }

    public Image(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getColor() {
        return color;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Uri getUri() {
        return uri;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public static ContentValues parseValues(Image image) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ImageEntry.COL_IMAGE_COLOR, image.getColor());
        contentValues.put(ImageEntry.COL_IMAGE_HEIGHT, image.getWidth());
        contentValues.put(ImageEntry.COL_IMAGE_WIDTH, image.getWidth());
        contentValues.put(ImageEntry.COL_IMAGE_URI, image.getUri().toString());

        return contentValues;
    }
}