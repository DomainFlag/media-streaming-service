import REDUCER_VALIDATOR from "./reducer-validator"
import UriBuilder from "../utils/UriBuilder";
import CONSTANTS from "../utils/Constants";

const ACCOUNT_STATE = "ACCOUNT_STATE";
const USER_AUTH_LOGGING = "USER_AUTH_LOGGING";
const USER_AUTH_EXITING = "USER_AUTH_EXITING";
const USER_ACCOUNT = "USER_ACCOUNT";
const USER_COLLECTION = "USER_COLLECTION";

export const ACTIONS = {
    ACCOUNT_STATE : (status, type = ACCOUNT_STATE, response = null, requestType = null) => ({
        status,
        type,
        response,
        requestType
    }),
    USER_ACCOUNT : () => {
        return (dispatch) => {
            dispatch(
                ACTIONS.ACCOUNT_STATE(CONSTANTS.PENDING, USER_ACCOUNT)
            );

            let headers = new Headers({
                "Content-Type" : "application/json",
                "X-Auth" : localStorage.getItem("token")
            });

            let url = new UriBuilder()
                .setScheme(CONSTANTS.SCHEME)
                .setAuthority(CONSTANTS.AUTHORITY)
                .appendPath(CONSTANTS.USERS)
                .appendPath(CONSTANTS.ME)
                .build();

            return fetch(url, { headers })
                .then((response) =>
                    (response.status < 200 || response.status >= 400) ?
                        Promise.reject() : response.json()
                )
                .then((response) => ({ user : response }))
                .then((response) => dispatch(
                    ACTIONS.ACCOUNT_STATE(CONSTANTS.SUCCESS, USER_ACCOUNT, response))
                ).catch((error) => dispatch(
                    ACTIONS.ACCOUNT_STATE(CONSTANTS.ERROR, null, error))
                );
        }
    },
    USER_AUTH_LOGGING : (type, body = null) => {
        return (dispatch) => {
            dispatch(
                ACTIONS.ACCOUNT_STATE(CONSTANTS.PENDING)
            );

            let headers = new Headers({
                "Content-Type" : "application/json",
                "X-Auth" : localStorage.getItem("token")
            });

            let url = new UriBuilder()
                .setScheme(CONSTANTS.SCHEME)
                .setAuthority(CONSTANTS.AUTHORITY)
                .appendPath(CONSTANTS.USERS)
                .appendOptPath(CONSTANTS.LOGIN, type === CONSTANTS.LOGIN)
                .build();

            return fetch(url, { method : "POST", body : JSON.stringify(body), headers })
                .then((response) =>
                    (response.status < 200 || response.status >= 400 || response.headers.get("X-Auth") === null) ?
                        Promise.reject() : response.json().then((json) => ({
                            user : json,
                            token : response.headers.get("X-Auth")
                        })).catch(Promise.reject)
                )
                .then((response) => dispatch(
                    ACTIONS.ACCOUNT_STATE(CONSTANTS.SUCCESS, USER_AUTH_LOGGING, response))
                ).catch((error) => dispatch(
                    ACTIONS.ACCOUNT_STATE(CONSTANTS.ERROR, null, error))
                );
        }
    },
    USER_AUTH_EXITING : () => {
        return (dispatch) => {
            dispatch(
                ACTIONS.ACCOUNT_STATE(CONSTANTS.PENDING)
            );

            let headers = new Headers({
                "X-Auth" : localStorage.getItem("token")
            });

            let url = new UriBuilder()
                .setScheme(CONSTANTS.SCHEME)
                .setAuthority(CONSTANTS.AUTHORITY)
                .appendPath(CONSTANTS.USERS)
                .appendPath(CONSTANTS.ME)
                .appendPath(CONSTANTS.TOKEN)
                .build();

            return fetch(url, { method : "DELETE", headers })
                .then((response) => (response.status >= 200 && response.status < 400) ?
                    response : Promise.reject()
                ).then((response) => dispatch(
                    ACTIONS.ACCOUNT_STATE(CONSTANTS.SUCCESS, USER_AUTH_EXITING, response))
                ).catch((error) => {
                    dispatch(ACTIONS.ACCOUNT_STATE(CONSTANTS.ERROR, null, error));
                });
        }
    },
    /**
     * @param type one of [ "GET", "POST", "DELETE" ]
     * @param {null} body { content, caption, type }
     * @returns {function(*): Promise<Response>}
     * @constructor
     */
    USER_COLLECTION : (type = CONSTANTS.GET, body = null) => {
        return (dispatch) => {
            dispatch(ACTIONS.ACCOUNT_STATE(CONSTANTS.PENDING));

            let headers = new Headers({
                "Content-Type" : "application/json",
                "X-Auth" : localStorage.getItem("token")
            });

            let url = new UriBuilder()
                .setScheme(CONSTANTS.SCHEME)
                .setAuthority(CONSTANTS.AUTHORITY)
                .appendPath(CONSTANTS.USERS)
                .appendPath(CONSTANTS.COLLECTION)
                .appendPath(body["type"], body !== null)
                .build();

            let bodyInput = {};
            if(body) {
                bodyInput["body"] = JSON.stringify(body);
            }

            return fetch(url, { method : type, headers, ...bodyInput })
                .then((response) => (response.status >= 200 && response.status < 400) ?
                    response : Promise.reject()
                ).then((response) =>
                    (type === CONSTANTS.GET) ? {account : response.json()} :
                        (type === CONSTANTS.DELETE) ? body :
                            (type === CONSTANTS.POST) ? response.json() :
                                Promise.reject(response))
                .then((response) => dispatch(ACTIONS.ACCOUNT_STATE(CONSTANTS.SUCCESS, USER_COLLECTION, response, type)))
                .catch((error) => dispatch(ACTIONS.ACCOUNT_STATE(CONSTANTS.ERROR, null, error)));
        }
    }
};

const account = (() => {
    const REDUCER_ACTIONS = {
        ACCOUNT_STATE : (state, action) => {
            if(action.response) {
                return {
                    ...state,
                    status: action.status,
                    message: action.response || "Invalid request"
                };
            } else {
                return {
                    ...state,
                    status: action.status
                }
            }
        },
        USER_ACCOUNT : (state, action) => ({
            ...state,
            ...action.response
        }),
        USER_AUTH_LOGGING : (state, action) => ({
            ...state,
            ...action.response,
            state : CONSTANTS.ADD_TOKEN,
            status : action.status
        }),
        USER_AUTH_EXITING : (state, action) => ({
            state : CONSTANTS.REMOVE_TOKEN,
            status : action.status,
            token : null
        }),
        USER_COLLECTION : (state, action) => (action.requestType === CONSTANTS.POST) ? ({
            ...state,
            ...action.response
        }) : (action.requestType === CONSTANTS.DELETE) ? ({
            ...state,
            user : {
                ...state.user,
                content : {
                    ...state.user.content,
                    [action.response.type + 's'] : state.user.content[action.response.type + 's'].filter((item) => item.id !== action.response.id)
                }
            }
        }) : state
    };

    return (state = [], action) => REDUCER_VALIDATOR(REDUCER_ACTIONS, state, action);
})();

export default account;