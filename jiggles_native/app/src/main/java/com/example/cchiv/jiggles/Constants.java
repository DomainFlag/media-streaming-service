package com.example.cchiv.jiggles;

public class Constants {
    public final static String AUTH_TYPE_KEY = "AUTH_TYPE_KEY";
    public final static String AUTH_SIGN_UP = "AUTH_SIGN_UP";
    public final static String AUTH_SIGN_IN = "AUTH_SIGN_IN";

    /* Preferences */
    public static final String AUTH_TOKEN = "AUTH_TOKEN";
    public static final String OTHER_PREFERENCES = "OTHER_PREFERENCES";

    public static final String PREFERENCE_EMAIL = "PREFERENCE_EMAIL";

    /* Network */
    public static final String SCHEME = "https";
    public static final String AUTHORITY = app_config.APP;
    public static final String USERS = "users";
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

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";

    public static final String CREATE = "create";
    public static final String NONE = "none";
    public static final String UPDATE = "update";
    public static final String QUERY = "query";
    public static final String MAIN = "main";
    public static final String FORUM = "forum";
    public static final String THREAD = "thread";
    public static final String COMMENT = "comment";
    public static final String ALL = "all";

    public static final String NEWS = "news";
    public static final String RELEASES = "releases";

    public static final String ARTIST = "artist";
    public static final String ALBUM = "album";
    public static final String TRACK = "track";


    public enum ENTERTAINMENT_TYPES {
        NEWS(Constants.NEWS),
        RELEASES(Constants.RELEASES);

        ENTERTAINMENT_TYPES(String type) {}
    }

    public enum CONTENT_TYPES {
        ARTIST(Constants.ARTIST),
        ALBUM(Constants.ALBUM),
        TRACK(Constants.TRACK);

        CONTENT_TYPES(String type) {}
    }
}
