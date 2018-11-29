package com.example.cchiv.jiggles;

import com.example.cchiv.jiggles.model.Album;
import com.example.cchiv.jiggles.model.Artist;
import com.example.cchiv.jiggles.model.Track;

public class Constants {
    public final static String AUTH_TYPE_KEY = "AUTH_TYPE_KEY";
    public final static String AUTH_SIGN_UP = "AUTH_SIGN_UP";
    public final static String AUTH_SIGN_IN = "AUTH_SIGN_IN";

    /* Preferences */
    public static final String AUTH_TOKEN = "AUTH_TOKEN";
    public static final String OTHER_PREFERENCES = "OTHER_PREFERENCES";

    public static final String PREFERENCE_EMAIL = "PREFERENCE_EMAIL";

    /* Spotify */
    public static final String CLIENT_ID = app_config.CLIENT_ID;
    public static final String REDIRECT_URI = app_config.REDIRECT_URI;

    /* Network */
    public static final String SCHEME = "https";
    public static final String AUTHORITY = app_config.APP;
    public static final String USER = "user";
    public static final String LOGIN = "login";
    public static final String SIGNUP = "signup";
    public static final String ME = "me";
    public static final String TOKEN = "token";
    public static final String RESOURCES = "resources";
    public static final String ACCOUNT = "account";
    public static final String IMAGES = "images";
    public static final String COLLECTION = "collection";

    public static final String THREAD_CREATOR = "thread_creator";
    public static final String THREAD_VIEW = "thread_view";

    public static final String ADD_TOKEN = "add_token";
    public static final String REMOVE_TOKEN = "remove_token";

    public static final String SUCCESS = "success";
    public static final String PENDING = "pending";
    public static final String ERROR = "ERROR";

    public static final String CREATE = "create";
    public static final String NONE = "none";
    public static final String UPDATE = "update";
    public static final String STORE = "store";
    public static final String QUERY = "query";
    public static final String MAIN = "main";
    public static final String FEED = "feed";
    public static final String LIKE = "like";
    public static final String THREAD = "thread";
    public static final String POST = "post";
    public static final String REPLY = "reply";

    public static final String FRESH = "fresh";
    public static final String NEWS = "news";
    public static final String RELEASES = "releases";

    public static final String ALL = "all";
    public static final String ARTIST = "artist";
    public static final String ALBUM = "album";
    public static final String TRACK = "track";


    public enum ENTERTAINMENT_TYPES {
        NEWS(Constants.NEWS),
        RELEASES(Constants.RELEASES);

        ENTERTAINMENT_TYPES(String type) {}
    }

    public enum STORE_ITEMS {
        ARTIST(Constants.ARTIST, Artist.class),
        ALBUM(Constants.ALBUM, Album.class),
        TRACK(Constants.TRACK, Track.class);

        public String type;
        public Class classType;

        STORE_ITEMS(String type, Class classType) {}
    }
}
