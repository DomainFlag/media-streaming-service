package com.example.cchiv.jiggles.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.cchiv.jiggles.data.ContentContract.NewsEntry;
import com.example.cchiv.jiggles.data.ContentContract.ReleaseEntry;

public class JigglesProvider extends ContentProvider {

    private static final String TAG = "JigglesProvider";

    private static final int ALBUM_SINGLE = 21;
    private static final int ALBUM_MANY = 22;

    private static final int TRACK_SINGLE = 24;
    private static final int TRACK_MANY = 25;

    private static final int ARTIST_SINGLE = 27;
    private static final int ARTIST_MANY = 28;

    private static final int THREAD_SINGLE = 30;
    private static final int THREAD_MANY = 31;

    private static final int NEWS_SINGLE = 33;
    private static final int NEWS_MANY = 34;

    private static final int RELEASE_SINGLE = 36;
    private static final int RELEASE_MANY = 37;

    public static UriMatcher uriMatcher = buildUriMatcher();
    public static UriMatcher uriTableMatcher = buildUriMatcher();

    public enum Tables {
        Table1(ContentContract.PATH_ALBUMS, ContentContract.AlbumEntry.TABLE_NAME),
        Table2(ContentContract.PATH_TRACKS, ContentContract.TrackEntry.TABLE_NAME),
        Table3(ContentContract.PATH_ARTISTS, ContentContract.ArtistEntry.TABLE_NAME);

        Tables(String path, String table) {}
    }

    public static UriMatcher buildUriMatcher() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

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
                String sql = "SELECT * FROM " + NewsEntry.TABLE_NAME;
                Log.v(TAG, String.valueOf(sqLiteDatabase.rawQuery(sql, null).getCount()));

                cursor = sqLiteDatabase.query(ContentContract.NewsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, orderBy);
                break;
            }
            case RELEASE_MANY : {
                cursor = sqLiteDatabase.query(ContentContract.ReleaseEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, orderBy);
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

        String path = uri.getPath();
        String table = path.substring(path.indexOf("/")+1);

        long nbRowsInserted = sqLiteDatabase.insert(ReleaseEntry.TABLE_NAME,null, contentValues);

        if(getContext() != null)
            getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, nbRowsInserted);
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase sqLiteDatabase = contentDbHelper.getWritableDatabase();
        int insertedRows = 0;

        switch(uriMatcher.match(uri)) {
            case NEWS_MANY : {
                sqLiteDatabase.beginTransaction();

                for(ContentValues value : values) {
                    insertedRows += sqLiteDatabase.insert(NewsEntry.TABLE_NAME, null, value);
                }

                sqLiteDatabase.setTransactionSuccessful();

                sqLiteDatabase.endTransaction();
                break;
            }
            case RELEASE_MANY : {
                sqLiteDatabase.beginTransaction();

                for(ContentValues value : values) {
                    insertedRows += sqLiteDatabase.insert(ReleaseEntry.TABLE_NAME, null, value);
                }

                sqLiteDatabase.setTransactionSuccessful();

                sqLiteDatabase.endTransaction();
                break;
            }
            default : {
                Log.v(TAG, "Unkown operation");
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
}
