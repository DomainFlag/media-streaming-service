const {APP} = require("./../app_config");

const CONSTANTS = {
    SCHEME : "http",
    AUTHORITY : APP,
    USERS : "users",
    LOGIN : "login",
    SIGNUP : "signup",
    ME : "me",
    TOKEN : "token",
    RESOURCES : "resources",
    ACCOUNT : "account",
    IMAGES : "images",
    COLLECTION : "collection",

    THREAD_CREATOR : "thread_creator",
    THREAD_VIEW : "thread_view",

    ADD_TOKEN : "add_token",
    REMOVE_TOKEN : "remove_token",

    SUCCESS : "success",
    PENDING : "pending",
    ERROR : "ERROR",

    GET : "GET",
    POST : "POST",
    PUT : "PUT",
    DELETE : "DELETE",

    CREATE : "create",
    NONE : "none",
    UPDATE : "update",
    QUERY : "query",
    MAIN : "main",
    FORUM : "forum",
    THREAD : "thread",
    COMMENT : "comment",

    ENTERTAINMENT_TYPES : {
        NEWS : "news",
        RELEASES : "releases"
    },

    CONTENT_TYPES : {
        ARTIST : "artist",
        ALBUM : "album",
        TRACK : "track"
    }
};

export default CONSTANTS;