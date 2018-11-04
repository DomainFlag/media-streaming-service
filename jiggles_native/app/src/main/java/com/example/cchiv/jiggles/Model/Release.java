package com.example.cchiv.jiggles.model;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import com.example.cchiv.jiggles.data.ContentContract.ReviewEntry;
import com.example.cchiv.jiggles.data.ContentContract.ReleaseEntry;
import com.example.cchiv.jiggles.utilities.JigglesLoader;

import java.util.ArrayList;
import java.util.List;

public class Release {

    private String _id;
    private String title;
    private String artist;
    private String url;

    private List<Review> reviews;

    public Release(String _id, String title, String artist, String url, List<Review> reviews) {
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

    public List<Review> getReviews() {
        return reviews;
    }

    public static List<Release> parseValues(Cursor cursor) {
        List<Release> releases = new ArrayList<>();

        int indexReleaseId = cursor.getColumnIndex(ReleaseEntry._ID);
        int indexReleaseIdentifier = cursor.getColumnIndex(ReleaseEntry.COL_RELEASE_IDENTIFIER);
        int indexReleaseArtist = cursor.getColumnIndexOrThrow(ReleaseEntry.COL_RELEASE_ARTIST);
        int indexReleaseUrl = cursor.getColumnIndexOrThrow(ReleaseEntry.COL_RELEASE_URL);
        int indexReleaseTitle = cursor.getColumnIndexOrThrow(ReleaseEntry.COL_RELEASE_TITLE);

        while(cursor.moveToNext()) {
            int releaseId = cursor.getInt(indexReleaseId);
            String releaseIdentifier = cursor.getColumnName(indexReleaseIdentifier);
            String releaseUrl = cursor.getString(indexReleaseUrl);
            String releaseArtist = cursor.getString(indexReleaseArtist);
            String releaseTitle = cursor.getString(indexReleaseTitle);

            Release release = new Release(releaseIdentifier, releaseTitle, releaseUrl, releaseUrl, new ArrayList<>());
            releases.add(release);
        }

        return releases;
    }

    public static ContentValues parseValues(Release release) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ReleaseEntry.COL_RELEASE_IDENTIFIER, release.get_id());
        contentValues.put(ReleaseEntry.COL_RELEASE_ARTIST, release.getArtist());
        contentValues.put(ReleaseEntry.COL_RELEASE_TITLE, release.getTitle());
        contentValues.put(ReleaseEntry.COL_RELEASE_URL, release.getUrl());

        return contentValues;
    }

    public static List<ContentProviderOperation> batchOperations(List<Release> releases) {
        List<ContentProviderOperation> operations = new ArrayList<>();

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
