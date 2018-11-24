package com.example.cchiv.jiggles.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import com.example.cchiv.jiggles.data.ContentContract.NewsEntry;
import com.example.cchiv.jiggles.utilities.JigglesLoader;

import java.util.ArrayList;
import java.util.List;

public class News {

    private String _id;
    private String author;
    private String header;
    private String caption;

    public News(String _id, String author, String header, String caption) {
        this._id = _id;
        this.author = author;
        this.header = header;
        this.caption = caption;
    }

    public String get_id() {
        return _id;
    }

    public String getAuthor() {
        return author;
    }

    public String getHeader() {
        return header;
    }

    public String getCaption() {
        return caption;
    }

    public static List<News> parseValues(Cursor cursor) {
        List<News> news = new ArrayList<>();

        int indexNewsId = cursor.getColumnIndex(NewsEntry._ID);
        int indexNewsAuthor = cursor.getColumnIndexOrThrow(NewsEntry.COL_NEWS_AUTHOR);
        int indexNewsCaption = cursor.getColumnIndexOrThrow(NewsEntry.COL_NEWS_CAPTION);
        int indexNewsHeader = cursor.getColumnIndexOrThrow(NewsEntry.COL_NEWS_HEADER);

        while(cursor.moveToNext()) {
            String id = cursor.getString(indexNewsId);
            String newsAuthor = cursor.getString(indexNewsAuthor);
            String newsCaption = cursor.getString(indexNewsCaption);
            String newsHeader = cursor.getString(indexNewsHeader);

            news.add(
                    new News(id, newsAuthor, newsHeader, newsCaption)
            );
        }

        return news;
    }

    public static ContentValues parseValues(News news) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(NewsEntry._ID, news.get_id());
        contentValues.put(NewsEntry.COL_NEWS_AUTHOR, news.getAuthor());
        contentValues.put(NewsEntry.COL_NEWS_CAPTION, news.getCaption());
        contentValues.put(NewsEntry.COL_NEWS_HEADER, news.getHeader());

        return contentValues;
    }

    public static Bundle bundleValues() {
        Bundle bundle = new Bundle();
        bundle.putString(JigglesLoader.BUNDLE_URI_KEY, NewsEntry.CONTENT_URI.toString());

        return bundle;
    }
}
