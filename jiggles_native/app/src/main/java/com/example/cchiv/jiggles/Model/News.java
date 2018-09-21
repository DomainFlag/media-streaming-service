package com.example.cchiv.jiggles.model;

import android.content.ContentValues;
import android.os.Bundle;

import com.example.cchiv.jiggles.data.ContentContract.NewsEntry;
import com.example.cchiv.jiggles.utilities.JigglesLoader;

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

    public static ContentValues parseValues(News news) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(NewsEntry.COL_NEWS_IDENTIFIER, news.get_id());
        contentValues.put(NewsEntry.COL_NEWS_AUTHOR, news.getAuthor());
        contentValues.put(NewsEntry.COL_NEWS_CAPTION, news.getCaption());
        contentValues.put(NewsEntry.COL_NEWS_HEADER, news.getHeader());

        return contentValues;
    }

    public static Bundle bundleValues() {
        Bundle bundle = new Bundle();
        bundle.putString(JigglesLoader.BUNDLE_URI_KEY, NewsEntry.CONTENT_URI.toString());
        bundle.putStringArray(JigglesLoader.BUNDLE_PROJECTION_KEY, new String[] {
                NewsEntry._ID,
                NewsEntry.COL_NEWS_IDENTIFIER,
                NewsEntry.COL_NEWS_CAPTION,
                NewsEntry.COL_NEWS_HEADER,
                NewsEntry.COL_NEWS_AUTHOR
        });

        return bundle;
    }
}
