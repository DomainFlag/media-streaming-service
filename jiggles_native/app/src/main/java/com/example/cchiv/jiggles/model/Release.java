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

    private String id;
    private String title;
    private String artist;
    private String url;

    private List<Review> reviews;

    public Release(String id, String title, String artist, String url, List<Review> reviews) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.url = url;
        this.reviews = reviews;
    }

    public String getId() {
        return id;
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
        int id = cursor.getInt(indexReleaseId);

        return release.getId().equals(String.valueOf(id));
    }

    public static List<Release> parseValues(Cursor cursor) {
        List<Release> releases = new ArrayList<>();

        int indexReleaseId = cursor.getColumnIndex(ReleaseEntry._ID);
        int indexReleaseIdentifier = cursor.getColumnIndex(ReleaseEntry.COL_RELEASE_IDENTIFIER);
        int indexReleaseArtist = cursor.getColumnIndexOrThrow(ReleaseEntry.COL_RELEASE_ARTIST);
        int indexReleaseUrl = cursor.getColumnIndexOrThrow(ReleaseEntry.COL_RELEASE_URL);
        int indexReleaseTitle = cursor.getColumnIndexOrThrow(ReleaseEntry.COL_RELEASE_TITLE);

        Release release = null;
        Review review = null;
        while(cursor.moveToNext()) {
            if(!Release.isUnique(release, cursor)) {
                int id = cursor.getInt(indexReleaseId);
                String releaseIdentifier = cursor.getColumnName(indexReleaseIdentifier);
                String releaseUrl = cursor.getString(indexReleaseUrl);
                String releaseArtist = cursor.getString(indexReleaseArtist);
                String releaseTitle = cursor.getString(indexReleaseTitle);

                release = new Release(String.valueOf(id), releaseTitle, releaseArtist, releaseUrl, new ArrayList<>());
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

        contentValues.put(ReleaseEntry.COL_RELEASE_IDENTIFIER, release.getId());
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

        int index = 0;
        for(int g = 0; g < releases.size(); g++) {
            Release release = releases.get(g);
            int releaseIndex = index;

            contentProviderOperations.add(
                    ContentProviderOperation
                            .newInsert(ReleaseEntry.CONTENT_URI)
                            .withValues(Release.parseValues(release))
                            .build());

            index++;

            List<Review> reviews = release.getReviews();
            for(int h = 0; h < reviews.size(); h++) {
                Review review = reviews.get(h);

                contentProviderOperations.add(
                        ContentProviderOperation
                                .newInsert(ReviewEntry.CONTENT_URI)
                                .withValues(Review.parseValues(review))
                                .withValueBackReference(ReviewEntry.COL_REVIEW_RELEASE, releaseIndex)
                                .build());

                index++;
            }
        }

        return contentProviderOperations;
    }
}
