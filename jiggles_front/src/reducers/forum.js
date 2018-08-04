import REDUCER_VALIDATOR from "./reducer-validator"
import UriBuilder from "../utils/UriBuilder";
import CONSTANTS from "../utils/Constants";

const FORUM_STATE = "FORUM_STATE";
const HANDLE_THREAD = "HANDLE_THREAD";
const HANDLE_REPLY = "HANDLE_REPLY";

export const ACTIONS = {
    FORUM_STATE : (status, requestType, response = null) => ({
        type : FORUM_STATE,
        requestType,
        status,
        response
    }),
    HANDLE_FORUM_STATE : (type, status, requestType, response = null) => ({
        type,
        requestType,
        status,
        response
    }),
    /**
     * @param type one of [ "DELETE", "POST", CONSTANTS.GET, "PUT" ]
     * @param {null} body { content, caption }
     * @returns {function(*): Promise<Response>}
     * @constructor
     */
    HANDLE_THREAD : (type = CONSTANTS.GET, body = null) => {
        return (dispatch) => {
            dispatch(ACTIONS.FORUM_STATE(CONSTANTS.PENDING, type));

            let headers = new Headers();
            headers.set("Content-Type", "application/json");
            headers.set("X-Auth", localStorage.getItem("token"));

            let url = new UriBuilder()
                .setScheme(CONSTANTS.SCHEME)
                .setAuthority(CONSTANTS.AUTHORITY)
                .appendPath(CONSTANTS.FORUM)
                .appendPath(CONSTANTS.THREAD)
                .build();

            let bodyInput = {};
            if(body) {
                bodyInput["body"] = JSON.stringify(body);
            }

            return fetch(url, { method : type, headers, ...bodyInput })
                .then((response) => response.json())
                .then((data) => {
                    if (type === CONSTANTS.GET)
                        return dispatch(ACTIONS.HANDLE_FORUM_STATE(HANDLE_THREAD, CONSTANTS.SUCCESS, type, {threads : data}));
                    else if (type === CONSTANTS.PUT)
                        return dispatch(ACTIONS.HANDLE_FORUM_STATE(HANDLE_THREAD, CONSTANTS.SUCCESS, type, data));
                    else if (type === CONSTANTS.DELETE)
                        return dispatch(ACTIONS.HANDLE_FORUM_STATE(HANDLE_THREAD, CONSTANTS.SUCCESS, type, body));
                    else if (type === CONSTANTS.POST)
                        return dispatch(ACTIONS.HANDLE_FORUM_STATE(HANDLE_THREAD, CONSTANTS.SUCCESS, type, data));
                    else dispatch(ACTIONS.FORUM_STATE(CONSTANTS.ERROR, type, data));
                })
                .catch((error) => dispatch(ACTIONS.FORUM_STATE(CONSTANTS.ERROR, type, error)));
        }
    },
    /**
     * @param type one of [ "DELETE", "POST", "PUT" ]
     * @param {null} body { _id, content, parent, depth }
     * @returns {function(*): Promise<Response>}
     * @constructor
     */
    HANDLE_REPLY : (type = "POST", body = null) => {
        return (dispatch) => {
            dispatch(ACTIONS.FORUM_STATE(CONSTANTS.PENDING, type));

            let headers = new Headers();
            headers.set("Content-Type", "application/json");
            headers.set("X-Auth", localStorage.getItem("token"));

            let url = new UriBuilder()
                .setScheme(CONSTANTS.SCHEME)
                .setAuthority(CONSTANTS.AUTHORITY)
                .appendPath(CONSTANTS.FORUM)
                .appendPath(CONSTANTS.THREAD)
                .appendPath(CONSTANTS.COMMENT)
                .build();

            let bodyInput = {};
            if(body) {
                bodyInput["body"] = JSON.stringify(body);
            }

            return fetch(url, { method : type, headers, ...bodyInput })
                .then((response) => response.json())
                .then((data) => {
                    if (type === CONSTANTS.GET)
                        return dispatch(ACTIONS.HANDLE_FORUM_STATE(HANDLE_REPLY, CONSTANTS.SUCCESS, type, { replies : data }));
                    else if (type === CONSTANTS.PUT)
                        return dispatch(ACTIONS.HANDLE_FORUM_STATE(HANDLE_REPLY, CONSTANTS.SUCCESS, type, data));
                    else if (type === CONSTANTS.DELETE)
                        return dispatch(ACTIONS.HANDLE_FORUM_STATE(HANDLE_REPLY, CONSTANTS.SUCCESS, type, body));
                    else if (type === CONSTANTS.POST)
                        return dispatch(ACTIONS.HANDLE_FORUM_STATE(HANDLE_REPLY, CONSTANTS.SUCCESS, type, data));
                    else dispatch(ACTIONS.FORUM_STATE(CONSTANTS.ERROR, type, data));
                })
                .catch(() => dispatch(ACTIONS.ENTERTAINER_STATE(CONSTANTS.ERROR, type)));
        }
    }
};

const forum = (() => {
    const REDUCER_ACTIONS = {
        ENTERTAINER_STATE : (state, action) => {
            if(action.status === CONSTANTS.SUCCESS) {
                return Object.assign({}, state, action.response);
            } else return state;
        },
        HANDLE_THREAD : (state, action) => {
            switch(action.requestType) {
                case CONSTANTS.GET : {
                    if(action.status === CONSTANTS.SUCCESS) {
                        return Object.assign({}, state, action.response);
                    } else return state;
                }
                case CONSTANTS.POST : return ((action.status === CONSTANTS.SUCCESS) ? Object.assign({}, {
                    threads : state.threads.concat(action.response)
                }) : state);
                case CONSTANTS.PUT : return ((action.status === CONSTANTS.SUCCESS) ? Object.assign({}, {
                    threads : state.threads.map((thread) => {
                        return (thread._id === action.response._id) ? action.response : thread
                    })
                }) : state);
                case CONSTANTS.DELETE : return ((action.status === CONSTANTS.SUCCESS) ? Object.assign({}, {
                    threads : state.threads.filter((thread) => thread._id !== action.response._id)
                }) : state);
                default : return state;
            }
        },
        HANDLE_REPLY : (state, action) => {
            switch(action.requestType) {
                case CONSTANTS.GET : {
                    if(action.status === CONSTANTS.SUCCESS) {
                        return Object.assign({}, state, action.response);
                    } else return state;
                }
                case CONSTANTS.POST : return ((action.status === CONSTANTS.SUCCESS) ? Object.assign({}, {
                    threads: state.threads.map((thread) => thread._id === action.response._id ? action.response : thread)
                }) : state);
                case CONSTANTS.PUT : return ((action.status === CONSTANTS.SUCCESS) ? Object.assign({}, {
                    threads : state.threads.map((thread) => {
                        return (thread._id === action.response._id) ? action.response : thread
                    })
                }) : state);
                case CONSTANTS.DELETE : return ((action.status === CONSTANTS.SUCCESS) ? Object.assign({}, {
                    threads : state.threads.filter((thread) => thread._id !== action.response._id)
                }) : state);
                default : return state;
            }
        }
    };

    return (state = [], action) => REDUCER_VALIDATOR(REDUCER_ACTIONS, state, action);
})();

export default forum;