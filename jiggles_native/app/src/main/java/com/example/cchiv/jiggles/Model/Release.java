package com.example.cchiv.jiggles.model;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.os.Bundle;

import com.example.cchiv.jiggles.data.ContentContract.ReviewEntry;
import com.example.cchiv.jiggles.data.ContentContract.ReleaseEntry;
import com.example.cchiv.jiggles.utilities.JigglesLoader;

import java.util.ArrayList;

public class Release {

    private String _id;
    private String title;
    private String artist;
    private String url;

    private ArrayList<Review> reviews;

    public Release(String _id, String title, String artist, String url, ArrayList<Review> reviews) {
        this._id = _id;
        this.title = title;
        this.artist = artist;
        this.url = url;
        this.reviews = reviews;
    }

    public String get_id() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getUrl() {
        return url;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public static ContentValues parseValues(Release release) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ReleaseEntry.COL_RELEASE_IDENTIFIER, release.get_id());
        contentValues.put(ReleaseEntry.COL_RELEASE_ARTIST, release.getArtist());
        contentValues.put(ReleaseEntry.COL_RELEASE_TITLE, release.getTitle());
        contentValues.put(ReleaseEntry.COL_RELEASE_URL, release.getUrl());

        return contentValues;
    }

    public static ArrayList<ContentProviderOperation> batchOperations(ArrayList<Release> releases) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        int previousResult = 0;
        for(Release release : releases) {
            ContentProviderOperation contentProviderReleaseOperation = ContentProviderOperation
                    .newInsert(ReleaseEntry.CONTENT_URI)
                    .withValues(parseValues(release))
                    .build();

            operations.add(contentProviderReleaseOperation);
            for(Review review : release.getReviews()) {
                ContentProviderOperation contentProviderReviewOperation = ContentProviderOperation
                        .newInsert(ReviewEntry.CONTENT_URI)
                        .withValueBackReference(ReviewEntry.COL_REVIEW_RELEASE, previousResult)
                        .withValues(Review.parseValues(review))
                        .build();

                operations.add(contentProviderReviewOperation);
            }

            previousResult += (release.getReviews().size() + 1);
        }

        return operations;
    };

    public static Bundle bundleValues() {
        Bundle bundle = new Bundle();
        bundle.putString(JigglesLoader.BUNDLE_URI_KEY, ReleaseEntry.CONTENT_URI.toString());
        bundle.putStringArray(JigglesLoader.BUNDLE_PROJECTION_KEY, new String[] {
                ReleaseEntry._ID,
                ReleaseEntry.COL_RELEASE_IDENTIFIER,
                ReleaseEntry.COL_RELEASE_TITLE,
                ReleaseEntry.COL_RELEASE_ARTIST,
                ReleaseEntry.COL_RELEASE_URL
        });

        return bundle;
    }
}
