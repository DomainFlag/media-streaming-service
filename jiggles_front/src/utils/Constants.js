const {APP} = require("./../app_config");

const CONSTANTS = {
    APP,
    SCHEME : "http",
    USERS : "users",
    LOGIN : "login",
    SIGNUP : "signup",
    ME : "me",
    TOKEN : "token",

    SUCCESS : "success",
    PENDING : "pending",
    ERROR : "ERROR",

    ENTERTAINER : "entertainer",
    NEWS : "news",
    REVIEWS : "reviews",

    GIG : "GIG",

    ARTIST : "artist",
    ALBUM : "album",
    TRACK : "track",

    CREATE : "create",

    TYPES : [
        "artist",
        "album",
        "track"
    ]
};

export default CONSTANTS;