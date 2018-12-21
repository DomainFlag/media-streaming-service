const {APP} = require("./../app_config");

const CONSTANTS = {
    SCHEME : "https",
    AUTHORITY : APP,
    USERS : "user",
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
    ALL : "all",

    ENTERTAINMENT_TYPES : {
        NEWS : "news",
        RELEASES : "releases"
    },

    STORE_ITEMS : {
        ARTIST : "artist",
        ALBUM : "album",
        TRACK : "track"
    }
};

export default CONSTANTS;