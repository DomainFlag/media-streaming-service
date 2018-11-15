package com.example.cchiv.jiggles.data;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.cchiv.jiggles.data.ContentContract.NewsEntry;
import com.example.cchiv.jiggles.data.ContentContract.ReleaseEntry;
import com.example.cchiv.jiggles.data.ContentContract.ArtistEntry;
import com.example.cchiv.jiggles.data.ContentContract.AlbumEntry;
import com.example.cchiv.jiggles.data.ContentContract.TrackEntry;
import com.example.cchiv.jiggles.data.ContentContract.ImageEntry;
import com.example.cchiv.jiggles.data.ContentContract.ReviewEntry;
import com.example.cchiv.jiggles.data.ContentContract.NotificationEntry;

import java.util.ArrayList;

public class JigglesProvider extends ContentProvider {

    private static final String TAG = "JigglesProvider";

    private static final int ALBUM_SINGLE = 21;
    private static final int ALBUM_MANY = 22;

    private static final int TRACK_SINGLE = 23;
    private static final int TRACK_MANY = 24;

    private static final int ARTIST_SINGLE = 25;
    private static final int ARTIST_MANY = 26;

    private static final int THREAD_SINGLE = 27;
    private static final int THREAD_MANY = 28;

    private static final int NEWS_SINGLE = 29;
    private static final int NEWS_MANY = 30;

    private static final int RELEASE_SINGLE = 31;
    private static final int RELEASE_MANY = 32;

    private static final int REVIEW_SINGLE = 33;
    private static final int REVIEW_MANY = 34;

    private static final int IMAGE_SINGLE = 35;
    private static final int IMAGE_MANY = 36;

    private static final int NOTIFICATION_SINGLE = 37;
    private static final int NOTIFICATION_MANY = 38;

    private static final int COLLECTION = 101;

    public static UriMatcher uriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /* Independent uri matchers */
        uriMatcher.addURI(ContentContract.AUTHORITY, ContentContract.PATH_ALBUMS, ALBUM_MANY);
        uriMatcher.addURI(ContentContract.AUTHORITY, ContentContract.PATH_ALBUMS + "/#", ALBUM_SINGLE);

        uriMatcher.addURI(ContentContract.AUTHORITY, ContentContract.PATH_ARTISTS, ARTIST_MANY);
        uriMatcher.addURI(ContentContract.AUTHORITY, ContentContract.PATH_ARTISTS + "/#", ARTIST_SINGLE);

        uriMatcher.addURI(ContentContract.AUTHORITY, ContentContract.PATH_TRACKS, TRACK_MANY);
        uriMatcher.addURI(ContentContract.AUTHORITY, ContentContract.PATH_TRACKS + "/#", TRACK_SINGLE);

        uriMatcher.addURI(ContentContract.AUTHORITY, ContentContract.PATH_RELEASES, RELEASE_MANY);
        uriMatcher.addURI(ContentContract.AUTHORITY, ContentContract.PATH_RELEASES + "/#", RELEASE_SINGLE);

        uriMatcher.addURI(ContentContract.AUTHORITY, ContentContract.PATH_NEWS, NEWS_MANY);
        uriMatcher.addURI(ContentContract.AUTHORITY, ContentContract.PATH_NEWS + "/#", NEWS_SINGLE);

        uriMatcher.addURI(ContentContract.AUTHORITY, ContentContract.PATH_REVIEWS, REVIEW_MANY);
        uriMatcher.addURI(ContentContract.AUTHORITY, ContentContract.PATH_REVIEWS + "/#", REVIEW_SINGLE);

        uriMatcher.addURI(ContentContract.AUTHORITY, ContentContract.PATH_IMAGES, IMAGE_MANY);
        uriMatcher.addURI(ContentContract.AUTHORITY, ContentContract.PATH_IMAGES + "/#", IMAGE_SINGLE);

        uriMatcher.addURI(ContentContract.AUTHORITY, ContentContract.PATH_NOTIFICATIONS, NOTIFICATION_MANY);
        uriMatcher.addURI(ContentContract.AUTHORITY, ContentContract.PATH_NOTIFICATIONS + "/#", NOTIFICATION_SINGLE);

        /* Custom uri matchers */
        uriMatcher.addURI(ContentContract.AUTHORITY, ContentContract.PATH_COLLECTION, COLLECTION);

        return uriMatcher;
    };

    private ContentDbHelper contentDbHelper;

    @Override
    public boolean onCreate() {
        contentDbHelper = new ContentDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String orderBy) {
        SQLiteDatabase sqLiteDatabase = contentDbHelper.getReadableDatabase();
        Cursor cursor = null;

        switch(uriMatcher.match(uri)) {
            case NEWS_MANY : {
                cursor = sqLiteDatabase.query(ContentContract.NewsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, orderBy);
                break;
            }
            case RELEASE_MANY : {
                cursor = sqLiteDatabase.query(ContentContract.ReleaseEntry.TABLE_NAME_RELEASES, projection, selection, selectionArgs, null, null, orderBy);
                break;
            }
            case COLLECTION : {
                cursor = sqLiteDatabase.query(ContentContract.Collection.TABLE_NAME, projection, selection, selectionArgs, null, null, orderBy);
                break;
            }
        }

        if(getContext() != null && cursor != null)
            cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase sqLiteDatabase = contentDbHelper.getWritableDatabase();

        long insertedRowId = -1;
        switch(uriMatcher.match(uri)) {
            case ARTIST_MANY : {
                insertedRowId = sqLiteDatabase.insert(ArtistEntry.TABLE_NAME,null, contentValues);
                break;
            }
            case ALBUM_MANY : {
                insertedRowId = sqLiteDatabase.insert(AlbumEntry.TABLE_NAME,null, contentValues);
                break;
            }
            case TRACK_MANY : {
                insertedRowId = sqLiteDatabase.insert(TrackEntry.TABLE_NAME,null, contentValues);
                break;
            }
            case IMAGE_MANY : {
                insertedRowId = sqLiteDatabase.insert(ImageEntry.TABLE_NAME,null, contentValues);
                break;
            }
            case RELEASE_MANY : {
                insertedRowId = sqLiteDatabase.insert(ReleaseEntry.TABLE_NAME,null, contentValues);
                break;
            }
            case REVIEW_MANY : {
                insertedRowId = sqLiteDatabase.insert(ReviewEntry.TABLE_NAME, null, contentValues);
                break;
            }
            case NOTIFICATION_MANY : {
                insertedRowId = sqLiteDatabase.insert(NotificationEntry.TABLE_NAME, null, contentValues);
                break;
            }
        }

        if(getContext() != null)
            getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, insertedRowId);
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase sqLiteDatabase = contentDbHelper.getWritableDatabase();
        int insertedRows = 0;

        switch(uriMatcher.match(uri)) {
            case NEWS_MANY : {
                sqLiteDatabase.beginTransaction();

                for(ContentValues value : values) {
                    long rowId = sqLiteDatabase.insert(NewsEntry.TABLE_NAME, null, value);
                    if(rowId != -1)
                        insertedRows++;
                }

                sqLiteDatabase.setTransactionSuccessful();

                sqLiteDatabase.endTransaction();
                break;
            }
            case RELEASE_MANY : {
                sqLiteDatabase.beginTransaction();

                for(ContentValues value : values) {
                    long rowId = sqLiteDatabase.insert(ReleaseEntry.TABLE_NAME, null, value);
                    if(rowId != -1)
                        insertedRows++;
                }

                sqLiteDatabase.setTransactionSuccessful();

                sqLiteDatabase.endTransaction();
                break;
            }
            default : {
                Log.v(TAG, "Unknown operation");
            }
        }

        return insertedRows;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        SQLiteDatabase sqLiteDatabase = contentDbHelper.getWritableDatabase();
        sqLiteDatabase.beginTransaction();

        ContentProviderResult[] results;
        try {
            results = super.applyBatch(operations);

            sqLiteDatabase.setTransactionSuccessful();

            return results;
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }
}
