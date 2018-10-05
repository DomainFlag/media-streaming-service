package com.example.cchiv.jiggles.model;

import android.content.ContentValues;
import android.os.Bundle;

import com.example.cchiv.jiggles.data.ContentContract.ReviewEntry;
import com.example.cchiv.jiggles.utilities.JigglesLoader;

public class Review {

    private String _id;
    private String author;
    private String content;
    private String url;
    private int score;

    public Review(String _id, String author, String content, String url, int score) {
        this._id = _id;
        this.author = author;
        this.content = content;
        this.url = url;
        this.score = score;
    }

    public String get_id() {
        return _id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    public int getScore() {
        return score;
    }

    public static ContentValues parseValues(Review review) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ReviewEntry.COL_REVIEW_AUTHOR, review.getAuthor());
        contentValues.put(ReviewEntry.COL_REVIEW_CONTENT, review.getContent());
        contentValues.put(ReviewEntry.COL_REVIEW_SCORE, review.getScore());
        contentValues.put(ReviewEntry.COL_REVIEW_URL, review.getUrl());

        return contentValues;
    }

    public static Bundle bundleValues() {
        Bundle bundle = new Bundle();
        bundle.putString(JigglesLoader.BUNDLE_URI_KEY, ReviewEntry.CONTENT_URI.toString());
        bundle.putStringArray(JigglesLoader.BUNDLE_PROJECTION_KEY, new String[] {
                ReviewEntry._ID,
                ReviewEntry.COL_REVIEW_RELEASE,
                ReviewEntry.COL_REVIEW_AUTHOR,
                ReviewEntry.COL_REVIEW_CONTENT,
                ReviewEntry.COL_REVIEW_SCORE,
                ReviewEntry.COL_REVIEW_URL
        });

        return bundle;
    }
}
