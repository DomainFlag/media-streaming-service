package com.example.cchiv.jiggles.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class ContentContract {

    public static final String AUTHORITY = "com.example.android.JigglesProvider";

    public static final String PATH_ALBUMS = "albums";
    public static final String PATH_TRACKS = "tracks";
    public static final String PATH_ARTISTS = "artists";
    public static final String PATH_USERS = "users";
    public static final String PATH_THREADS = "threads";
    public static final String PATH_REVIEWS = "reviews";
    public static final String PATH_RELEASES = "releases";
    public static final String PATH_NEWS = "news";
    public static final String PATH_IMAGES = "images";
    public static final String PATH_COMMENTS = "comments";


    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    /**
     * Table with images and artists
     */
    public static final class AlbumEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ALBUMS).build();

        public static final String TABLE_NAME = "album";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_ALBUM_NAME = "name";
        public static final String COL_ALBUM_RELEASE_DATE = "release_date";
        public static final String COL_ALBUM_URI = "uri";
        public static final String COL_ALBUM_TYPE = "type";
        public static final String COL_ALBUM_FAVOURITE = "favourite";
    }

    /**
     * Table with genres and images
     */
    public static final class ArtistEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTISTS).build();

        public static final String TABLE_NAME = "artist";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_ARTIST_NAME = "name";
        public static final String COL_ARTIST_URI = "uri";
        public static final String COL_ARTIST_TYPE = "type";
        public static final String COL_ARTIST_FAVOURITE = "favourite";
    }

    /**
     * Table with artists and images
     */
    public static final class TrackEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRACKS).build();

        public static final String TABLE_NAME = "track";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_TRACK_NAME = "name";
        public static final String COL_TRACK_URI = "uri";
        public static final String COL_TRACK_TYPE = "type";
        public static final String COL_TRACK_FAVOURITE = "favourite";
    }

    /**
     * Table with content(tracks, albums, artists) and users
     */
    public static final class UserEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USERS).build();

        public static final String TABLE_NAME = "user";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_USER_EMAIL = "email";
        public static final String COL_USER_NAME = "name";
        public static final String COL_USER_CAPTION = "caption";
        public static final String COL_USER_TYPE = "type";
    }

    /**
     * Table with comments
     */
    public static final class ThreadEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_THREADS).build();

        public static final String TABLE_NAME = "thread";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_THREAD_AUTHOR = "author";
        public static final String COL_THREAD_CAPTION = "caption";
        public static final String COL_THREAD_CONTENT = "content";
        public static final String COL_THREAD_VOTES = "votes";
    }

    /**
     * No relationship
     */
    public static final class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

        public static final String TABLE_NAME = "review";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_REVIEW_AUTHOR = "author";
        public static final String COL_REVIEW_RELEASE = "release";
        public static final String COL_REVIEW_CONTENT = "content";
        public static final String COL_REVIEW_URL = "url";
        public static final String COL_REVIEW_SCORE = "score";
    }


    /**
     * Table with reviews
     */
    public static final class ReleaseEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RELEASES).build();

        public static final String TABLE_NAME = "release";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_RELEASE_IDENTIFIER = "identifier";
        public static final String COL_RELEASE_TITLE = "title";
        public static final String COL_RELEASE_ARTIST = "artist";
        public static final String COL_RELEASE_URL = "url";
    }


    /**
     * No relationship
     */
    public static final class NewsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_NEWS).build();

        public static final String TABLE_NAME = "news";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_NEWS_IDENTIFIER = "identifier";
        public static final String COL_NEWS_AUTHOR = "author";
        public static final String COL_NEWS_HEADER = "header";
        public static final String COL_NEWS_CAPTION = "caption";
    }

    /**
     * No relationship
     */
    public static final class ImageEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_IMAGES).build();

        public static final String TABLE_NAME = "image";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_IMAGE_WIDTH = "width";
        public static final String COL_IMAGE_HEIGHT = "height";
        public static final String COL_IMAGE_URL = "url";
    }

    /**
     * Table with thread
     */
    public static final class CommentEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_COMMENTS).build();

        public static final String TABLE_NAME = "comment";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_COMMENT_AUTHOR = "author";
        public static final String COL_COMMENT_THREAD = "thread";
        public static final String COL_COMMENT_PARENT = "parent";
        public static final String COL_COMMENT_DEPTH = "depth";
        public static final String COL_COMMENT_CONTENT = "content";
        public static final String COL_COMMENT_LIKES = "likes";
    }
}
