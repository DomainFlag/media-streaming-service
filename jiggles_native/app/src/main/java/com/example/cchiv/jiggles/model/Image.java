package com.example.cchiv.jiggles.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;

import com.example.cchiv.jiggles.data.ContentContract.ImageEntry;

public class Image {

    private static final String TAG = "Image";

    public String id;
    public int height;
    public int width;
    public int color;
    private String url = null;
    private Bitmap bitmap = null;

    public Image(String url, int color, int height, int width) {
        this.color = color;
        this.height = height;
        this.width = width;
        this.url = url;
    }

    public Image(String id, int color, int height, int width, String url) {
        this.id = id;
        this.color = color;
        this.height = height;
        this.width = width;
        this.url = url;
    }

    public Image(String url) {
        this.url = url;
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

    public Uri getUrl() {
        if(url != null)
            return Uri.parse(url);
        else return null;
    }

    public String getId() {
        return id;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public static boolean isUnique(Image image, Cursor cursor) {
        if(image == null)
            return false;

        int indexImageId = cursor.getColumnIndex(ImageEntry._ID);
        int id = cursor.getInt(indexImageId);

        return image.getId().equals(String.valueOf(id));
    }

    public static Image parseCursor(Cursor cursor) {
        int indexImageId = cursor.getColumnIndex(ImageEntry._ID);
        int indexImageColor = cursor.getColumnIndex(ImageEntry.COL_IMAGE_COLOR);
        int indexImageHeight = cursor.getColumnIndex(ImageEntry.COL_IMAGE_HEIGHT);
        int indexImageWidth = cursor.getColumnIndex(ImageEntry.COL_IMAGE_WIDTH);
        int indexImageUri = cursor.getColumnIndex(ImageEntry.COL_IMAGE_URI);

        int id = cursor.getInt(indexImageId);
        int imageColor = cursor.getInt(indexImageColor);
        int imageHeight = cursor.getInt(indexImageHeight);
        int imageWidth = cursor.getInt(indexImageWidth);
        String imageUri = cursor.getString(indexImageUri);

        return new Image(String.valueOf(id), imageColor, imageHeight, imageWidth, imageUri);
    }

    public static ContentValues parseValues(Image image) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ImageEntry.COL_IMAGE_COLOR, image.getColor());
        contentValues.put(ImageEntry.COL_IMAGE_HEIGHT, image.getWidth());
        contentValues.put(ImageEntry.COL_IMAGE_WIDTH, image.getWidth());
        contentValues.put(ImageEntry.COL_IMAGE_URI, image.getUrl().toString());

        return contentValues;
    }
}