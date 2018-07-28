const {APP} = require("./../app_config");

const CONSTANTS = {
    APP,
    SCHEME : "http",
    USERS : "users",
    LOGIN : "login",
    SIGNUP : "signup",
    ME : "me",
    TOKEN : "token",

    ADD_TOKEN : "add_token",
    REMOVE_TOKEN : "remove_token",

    SUCCESS : "success",
    PENDING : "pending",
    ERROR : "ERROR",

    GIG : "gig",

    CREATE : "create",

    MAIN : "main",

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