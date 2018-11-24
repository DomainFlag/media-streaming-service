package com.example.cchiv.jiggles.model;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import com.example.cchiv.jiggles.data.ContentContract.ReleaseEntry;
import com.example.cchiv.jiggles.data.ContentContract.ReviewEntry;
import com.example.cchiv.jiggles.utilities.JigglesLoader;

import java.util.ArrayList;
import java.util.List;

public class Release {

    private static final String TAG = "Release";

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

    public String getId() {
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

    public static boolean isUnique(Release release, Cursor cursor) {
        if(release == null)
            return false;

        int indexReleaseId = cursor.getColumnIndex(ReleaseEntry._ID);
        String id = cursor.getString(indexReleaseId);

        return release.getId().equals(String.valueOf(id));
    }

    public static List<Release> parseValues(Cursor cursor) {
        List<Release> releases = new ArrayList<>();

        int indexReleaseId = cursor.getColumnIndex(ReleaseEntry._ID);
        int indexReleaseArtist = cursor.getColumnIndexOrThrow(ReleaseEntry.COL_RELEASE_ARTIST);
        int indexReleaseUrl = cursor.getColumnIndexOrThrow(ReleaseEntry.COL_RELEASE_URL);
        int indexReleaseTitle = cursor.getColumnIndexOrThrow(ReleaseEntry.COL_RELEASE_TITLE);

        Release release = null;
        Review review = null;
        while(cursor.moveToNext()) {
            if(!Release.isUnique(release, cursor)) {
                String id = cursor.getString(indexReleaseId);
                String releaseUrl = cursor.getString(indexReleaseUrl);
                String releaseArtist = cursor.getString(indexReleaseArtist);
                String releaseTitle = cursor.getString(indexReleaseTitle);

                release = new Release(id, releaseTitle, releaseArtist, releaseUrl, new ArrayList<>());
                releases.add(release);
            }

            if(!Review.isUnique(review, cursor)) {
                review = Review.parseValues(cursor);
                release.getReviews().add(review);
            }
        }

        return releases;
    }

    public static ContentValues parseValues(Release release) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ReleaseEntry._ID, release.getId());
        contentValues.put(ReleaseEntry.COL_RELEASE_ARTIST, release.getArtist());
        contentValues.put(ReleaseEntry.COL_RELEASE_TITLE, release.getTitle());
        contentValues.put(ReleaseEntry.COL_RELEASE_URL, release.getUrl());

        return contentValues;
    }

    public static Bundle bundleValues() {
        Bundle bundle = new Bundle();
        bundle.putString(JigglesLoader.BUNDLE_URI_KEY, ReleaseEntry.CONTENT_URI.toString());

        return bundle;
    }

    public static ArrayList<ContentProviderOperation> parseValues(List<Release> releases) {
        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<>();

        for(int g = 0; g < releases.size(); g++) {
            Release release = releases.get(g);

            contentProviderOperations.add(
                    ContentProviderOperation
                            .newInsert(ReleaseEntry.CONTENT_URI)
                            .withValues(Release.parseValues(release))
                            .build());

            List<Review> reviews = release.getReviews();
            for(int h = 0; h < reviews.size(); h++) {
                Review review = reviews.get(h);

                contentProviderOperations.add(
                        ContentProviderOperation
                                .newInsert(ReviewEntry.CONTENT_URI)
                                .withValues(Review.parseValues(review, release.getId()))
                                .build());
            }
        }

        return contentProviderOperations;
    }
}
