package com.example.cchiv.jiggles.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import com.example.cchiv.jiggles.data.ContentContract.NewsEntry;
import com.example.cchiv.jiggles.utilities.JigglesLoader;

import java.util.ArrayList;

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

    public static ArrayList<News> parseValues(Cursor cursor) {
        ArrayList<News> news = new ArrayList<>();

        int indexNewsId = cursor.getColumnIndex(NewsEntry._ID);
        int indexNewsIdentifier = cursor.getColumnIndex(NewsEntry.COL_NEWS_IDENTIFIER);
        int indexNewsAuthor = cursor.getColumnIndexOrThrow(NewsEntry.COL_NEWS_AUTHOR);
        int indexNewsCaption = cursor.getColumnIndexOrThrow(NewsEntry.COL_NEWS_CAPTION);
        int indexNewsHeader = cursor.getColumnIndexOrThrow(NewsEntry.COL_NEWS_HEADER);

        while(cursor.moveToNext()) {
            int newsId = cursor.getInt(indexNewsId);
            String newsIdentifier = cursor.getColumnName(indexNewsIdentifier);
            String newsAuthor = cursor.getString(indexNewsAuthor);
            String newsCaption = cursor.getString(indexNewsCaption);
            String newsHeader = cursor.getString(indexNewsHeader);

            news.add(
                    new News(newsIdentifier, newsAuthor, newsHeader, newsCaption)
            );
        }

        return news;
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
