package com.example.cchiv.jiggles.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cchiv.jiggles.data.ContentContract.AlbumEntry;
import com.example.cchiv.jiggles.data.ContentContract.ArtistEntry;
import com.example.cchiv.jiggles.data.ContentContract.ReviewEntry;
import com.example.cchiv.jiggles.data.ContentContract.ThreadEntry;
import com.example.cchiv.jiggles.data.ContentContract.TrackEntry;
import com.example.cchiv.jiggles.data.ContentContract.UserEntry;
import com.example.cchiv.jiggles.data.ContentContract.ReleaseEntry;
import com.example.cchiv.jiggles.data.ContentContract.NewsEntry;
import com.example.cchiv.jiggles.data.ContentContract.ImageEntry;
import com.example.cchiv.jiggles.data.ContentContract.ReplyEntry;

public class ContentDbHelper extends SQLiteOpenHelper {

    private final static String TAG = "ContentDbHelper";

    private final static String DATABASE_NAME = "jiggles.db";

    private Context context;

    public ContentDbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sqlArtistQuery = "CREATE TABLE " + ArtistEntry.TABLE_NAME + " (" +
                ArtistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ArtistEntry.COL_ARTIST_NAME + " TEXT NOT NULL UNIQUE, " +
                ArtistEntry.COL_ARTIST_URI + " TEXT, " +
                ArtistEntry.COL_ARTIST_LOCAL + " INTEGER DEFAULT 0, " +
                ArtistEntry.COL_ARTIST_TYPE + " TEXT DEFAULT 'album', " +
                ArtistEntry.COL_ARTIST_FAVOURITE + " INTEGER DEFAULT 1 " +
                ")";

        sqLiteDatabase.execSQL(sqlArtistQuery);

        String sqlAlbumQuery = "CREATE TABLE " + AlbumEntry.TABLE_NAME + " (" +
                AlbumEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AlbumEntry.COL_ALBUM_ARTIST + " INTEGER REFERENCES " +
                    ArtistEntry.TABLE_NAME + "(" + ArtistEntry._ID + ")" + ", " +
                AlbumEntry.COL_ALBUM_NAME + " TEXT NOT NULL UNIQUE, " +
                AlbumEntry.COL_ALBUM_RELEASE_DATE + " TEXT NOT NULL, " +
                AlbumEntry.COL_ALBUM_URI + " TEXT, " +
                AlbumEntry.COL_ALBUM_LOCAL + " INTEGER DEFAULT 0, " +
                AlbumEntry.COL_ALBUM_TYPE + " TEXT DEFAULT 'album', " +
                AlbumEntry.COL_ALBUM_FAVOURITE + " INTEGER DEFAULT 1 " +
                ")";

        sqLiteDatabase.execSQL(sqlAlbumQuery);

        String sqlTrackQuery = "CREATE TABLE " + TrackEntry.TABLE_NAME + " (" +
                TrackEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TrackEntry.COL_TRACK_ALBUM + " INTEGER REFERENCES " +
                    AlbumEntry.TABLE_NAME + "(" + AlbumEntry._ID + ")" + ", " +
                TrackEntry.COL_TRACK_NAME + " TEXT NOT NULL, " +
                TrackEntry.COL_TRACK_URI + " TEXT UNIQUE, " +
                TrackEntry.COL_TRACK_LOCAL + " INTEGER DEFAULT 0, " +
                TrackEntry.COL_TRACK_TYPE + " TEXT DEFAULT 'album', " +
                TrackEntry.COL_TRACK_FAVOURITE + " TEXT DEFAULT 1 " +
                ")";

        sqLiteDatabase.execSQL(sqlTrackQuery);

        String sqlUserQuery = "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UserEntry.COL_USER_EMAIL + " TEXT NOT NULL, " +
                UserEntry.COL_USER_NAME + " TEXT NOT NULL, " +
                UserEntry.COL_USER_TYPE + " TEXT DEFAULT 'user', " +
                UserEntry.COL_USER_CAPTION + " TEXT NOT NULL " +
                ")";

        sqLiteDatabase.execSQL(sqlUserQuery);

        String sqlImageQuery = "CREATE TABLE " + ImageEntry.TABLE_NAME + " (" +
                ImageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ImageEntry.COL_IMAGE_ALBUM + " INTEGER REFERENCES " +
                    AlbumEntry.TABLE_NAME + "(" + AlbumEntry._ID + ")" + ", " +
                ImageEntry.COL_IMAGE_WIDTH + " INTEGER, " +
                ImageEntry.COL_IMAGE_HEIGHT + " INTEGER, " +
                ImageEntry.COL_IMAGE_COLOR + " INTEGER, " +
                ImageEntry.COL_IMAGE_URI + " TEXT NOT NULL UNIQUE" +
                ")";

        sqLiteDatabase.execSQL(sqlImageQuery);

        String sqlReviewQuery = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " TEXT PRIMARY KEY, " +
                ReviewEntry.COL_REVIEW_AUTHOR + " TEXT NOT NULL, " +
                ReviewEntry.COL_REVIEW_RELEASE + " INTEGER REFERENCES " +
                    ReleaseEntry.TABLE_NAME + "(" + ReleaseEntry._ID + ")" + ", " +
                ReviewEntry.COL_REVIEW_CONTENT + " TEXT NOT NULL, " +
                ReviewEntry.COL_REVIEW_URL + " TEXT NOT NULL, " +
                ReviewEntry.COL_REVIEW_SCORE + " INTEGER DEFAULT 0 " +
                ")";

        sqLiteDatabase.execSQL(sqlReviewQuery);

        String sqlReleaseQuery = "CREATE TABLE " + ReleaseEntry.TABLE_NAME + " (" +
                ReleaseEntry._ID + " TEXT PRIMARY KEY, " +
                ReleaseEntry.COL_RELEASE_TITLE + " TEXT NOT NULL, " +
                ReleaseEntry.COL_RELEASE_ARTIST + " TEXT NOT NULL, " +
                ReleaseEntry.COL_RELEASE_URL + " TEXT NOT NULL, " +
                ReleaseEntry.COL_RELEASE_URI + " TEXT NOT NULL " +
                ")";

        sqLiteDatabase.execSQL(sqlReleaseQuery);

        String sqlNewsQuery = "CREATE TABLE " + NewsEntry.TABLE_NAME + " (" +
                NewsEntry._ID + " TEXT PRIMARY KEY, " +
                NewsEntry.COL_NEWS_AUTHOR + " TEXT, " +
                NewsEntry.COL_NEWS_HEADER + " TEXT NOT NULL, " +
                NewsEntry.COL_NEWS_CAPTION + " TEXT NOT NULL " +
                ")";

        sqLiteDatabase.execSQL(sqlNewsQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}
}
