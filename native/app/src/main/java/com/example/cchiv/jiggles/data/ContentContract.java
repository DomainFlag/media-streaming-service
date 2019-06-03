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
    public static final String PATH_POSTS = "posts";
    public static final String PATH_FEED = "feed";
    public static final String PATH_REVIEWS = "reviews";
    public static final String PATH_RELEASES = "releases";
    public static final String PATH_NEWS = "news";
    public static final String PATH_IMAGES = "images";
    public static final String PATH_REPLIES = "replies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static String getColumnAliased(String tableName, String columnName) {
        return tableName + "." + columnName;
    }

    /* Custom authorities */
    public static final String PATH_COLLECTION = "collection";

    public static final Uri CONTENT_COLLECTION_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_COLLECTION).build();

    //*************************************************************/
    /**
     * Custom queries
     */
    public static final class Collection {
        public static final String TABLE_NAME = ArtistEntry.TABLE_NAME +
                " INNER JOIN " + AlbumEntry.TABLE_NAME +
                    " ON (" + getColumnAliased(ArtistEntry.TABLE_NAME, ArtistEntry._ID) + " = " + getColumnAliased(AlbumEntry.TABLE_NAME, AlbumEntry.COL_ALBUM_ARTIST) + ")" +
                " INNER JOIN " + TrackEntry.TABLE_NAME +
                    " ON (" + getColumnAliased(AlbumEntry.TABLE_NAME, AlbumEntry._ID) + " = " + getColumnAliased(TrackEntry.TABLE_NAME, TrackEntry.COL_TRACK_ALBUM) + ")" +
                " LEFT OUTER JOIN " + ImageEntry.TABLE_NAME +
                    " ON (" + getColumnAliased(AlbumEntry.TABLE_NAME, AlbumEntry._ID) + " = " + getColumnAliased(ImageEntry.TABLE_NAME, ImageEntry.COL_IMAGE_ALBUM) + ")";
    }

    //*************************************************************/
    /**
     * Table with genres and images
     */
    public static final class ArtistEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTISTS).build();

        public static final String TABLE_NAME = "artist";

        public static final String _ID = TABLE_NAME + BaseColumns._ID;
        public static final String COL_ARTIST_NAME = "artist_name";
        public static final String COL_ARTIST_URI = "artist_uri";
        public static final String COL_ARTIST_TYPE = "artist_type";
        public static final String COL_ARTIST_LOCAL = "artist_local";
        public static final String COL_ARTIST_FAVOURITE = "artist_favourite";
    }

    /**
     * Table with images and artists
     */
    public static final class AlbumEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ALBUMS).build();

        public static final String TABLE_NAME = "album";

        public static final String _ID = TABLE_NAME + BaseColumns._ID;
        public static final String COL_ALBUM_ARTIST = "album_artist";
        public static final String COL_ALBUM_NAME = "album_name";
        public static final String COL_ALBUM_RELEASE_DATE = "album_release_date";
        public static final String COL_ALBUM_URI = "album_uri";
        public static final String COL_ALBUM_LOCAL = "album_local";
        public static final String COL_ALBUM_TYPE = "album_type";
        public static final String COL_ALBUM_FAVOURITE = "album_favourite";
    }

    /**
     * Table with artists and images
     */
    public static final class TrackEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRACKS).build();

        public static final String TABLE_NAME = "track";

        public static final String _ID = TABLE_NAME + BaseColumns._ID;
        public static final String COL_TRACK_ALBUM = "track_album";
        public static final String COL_TRACK_NAME = "track_name";
        public static final String COL_TRACK_URI = "track_uri";
        public static final String COL_TRACK_NUMBER = "track_number";
        public static final String COL_TRACK_LOCAL = "track_local";
        public static final String COL_TRACK_TYPE = "track_type";
        public static final String COL_TRACK_FAVOURITE = "track_favourite";
    }

    /**
     * Table with content(tracks, albums, artists) and users
     */
    public static final class UserEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USERS).build();

        public static final String TABLE_NAME = "user";

        public static final String _ID = TABLE_NAME + BaseColumns._ID;
        public static final String COL_USER_EMAIL = "user_email";
        public static final String COL_USER_NAME = "user_name";
        public static final String COL_USER_CAPTION = "user_caption";
        public static final String COL_USER_TYPE = "user_type";
    }

    /**
     * Table with replies
     */
    public static final class ThreadEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_THREADS).build();

        public static final String TABLE_NAME = "thread";

        public static final String _ID = TABLE_NAME + BaseColumns._ID;
        public static final String COL_THREAD_AUTHOR = "thread_author";
        public static final String COL_THREAD_CAPTION = "thread_caption";
        public static final String COL_THREAD_CONTENT = "thread_content";
        public static final String COL_THREAD_VOTES = "thread_votes";
    }

    /**
     * TODO(0) Needs to be done, do we need really caching?
     * Table with replies
     */
    public static final class PostEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_POSTS).build();

        public static final String TABLE_NAME = "post";

        public static final String _ID = TABLE_NAME + BaseColumns._ID;
        public static final String COL_POST_AUTHOR = "post_author";
        public static final String COL_POST_CAPTION = "post_caption";
        public static final String COL_POST_CONTENT = "post_content";
        public static final String COL_POST_VOTES = "post_votes";
    }

    /**
     * Table with replies
     */
    public static final class FeedItemEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FEED).build();

        public static final String TABLE_NAME = "feed_it";

        public static final String _ID = TABLE_NAME + BaseColumns._ID;
        public static final String COL_FEED_AUTHOR = "feed_author";
        public static final String COL_FEED_TYPE = "feed_type";
        public static final String COL_FEED_RESOURCE = "feed_resource";
        public static final String COL_FEED_VOTES = "feed_votes";
    }

    /**
     * No relationship
     */
    public static final class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

        public static final String TABLE_NAME = "review";

        public static final String _ID = TABLE_NAME + BaseColumns._ID;
        public static final String COL_REVIEW_AUTHOR = "review_author";
        public static final String COL_REVIEW_RELEASE = "review_release";
        public static final String COL_REVIEW_CONTENT = "review_content";
        public static final String COL_REVIEW_URL = "review_url";
        public static final String COL_REVIEW_SCORE = "review_score";
    }


    /**
     * Table with reviews
     */
    public static final class ReleaseEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RELEASES).build();

        public static final String TABLE_NAME = "release";

        public static final String TABLE_NAME_RELEASES = TABLE_NAME + " INNER JOIN " + ReviewEntry.TABLE_NAME +
                " ON (" + getColumnAliased(TABLE_NAME, ReleaseEntry._ID) + " = " + getColumnAliased(ReviewEntry.TABLE_NAME, ReviewEntry.COL_REVIEW_RELEASE) + ");";

        public static final String _ID = TABLE_NAME + BaseColumns._ID;
        public static final String COL_RELEASE_TITLE = "release_title";
        public static final String COL_RELEASE_ARTIST = "release_artist";
        public static final String COL_RELEASE_URL = "release_url";
        public static final String COL_RELEASE_URI = "release_uri";
    }


    /**
     * No relationship
     */
    public static final class NewsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_NEWS).build();

        public static final String TABLE_NAME = "news";

        public static final String _ID = TABLE_NAME + BaseColumns._ID;
        public static final String COL_NEWS_AUTHOR = "news_author";
        public static final String COL_NEWS_HEADER = "news_header";
        public static final String COL_NEWS_CAPTION = "news_caption";
    }

    /**
     * No relationship
     */
    public static final class ImageEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_IMAGES).build();

        public static final String TABLE_NAME = "image";

        public static final String _ID = TABLE_NAME + BaseColumns._ID;
        public static final String COL_IMAGE_WIDTH = "image_width";
        public static final String COL_IMAGE_HEIGHT = "image_height";
        public static final String COL_IMAGE_COLOR = "image_color";
        public static final String COL_IMAGE_URI = "image_uri";
        public static final String COL_IMAGE_ALBUM = "image_album";
    }

    /**
     * Table with thread
     */
    public static final class ReplyEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REPLIES).build();

        public static final String TABLE_NAME = "comment";

        public static final String _ID = TABLE_NAME + BaseColumns._ID;
        public static final String COL_REPLY_AUTHOR = "reply_author";
        public static final String COL_REPLY_THREAD = "reply_thread";
        public static final String COL_REPLY_PARENT = "reply_parent";
        public static final String COL_REPLY_DEPTH = "reply_depth";
        public static final String COL_REPLY_CONTENT = "reply_content";
        public static final String COL_REPLY_LIKES = "reply_likes";
    }
}
