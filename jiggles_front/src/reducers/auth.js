import REDUCER_VALIDATOR from "./reducer-validator"
import UriBuilder from "../utils/UriBuilder";
import CONSTANTS from "../utils/Constants";

const USER_AUTH_STATE = "USER_AUTH_STATE";
const USER_AUTH_LOGGING = "USER_AUTH_LOGGING";
const USER_AUTH_EXITING = "USER_AUTH_EXITING";

export const ACTIONS = {
    USER_AUTH_STATE : (status, type = null, response = null) => ({
        type : type || USER_AUTH_STATE,
        status,
        response
    }),
    USER_AUTH_LOGGING : (type, body = null) => {
        return (dispatch) => {
            dispatch(ACTIONS.USER_AUTH_STATE(CONSTANTS.PENDING));

            let headers = new Headers();
            headers.set("Content-Type", "application/json");
            headers.set("Access-Control-Expose-Headers", "X-Auth");

            let uriBuilder = new UriBuilder()
                .setScheme(CONSTANTS.SCHEME)
                .setAuthority(CONSTANTS.APP)
                .appendPath(CONSTANTS.USERS);

            if(type === CONSTANTS.LOGIN)
                uriBuilder.appendPath(CONSTANTS.LOGIN);

            let url = uriBuilder.build();

            return fetch(url, { method : "POST", body : JSON.stringify(body), headers })
                .then((response) => {
                    if(response.status < 200 || response.status >= 400 || response.headers.get("X-Auth") === null)
                        Promise.reject();
                    else dispatch(ACTIONS.USER_AUTH_STATE(CONSTANTS.SUCCESS, USER_AUTH_LOGGING, response.headers.get("X-Auth")));
                })
                .catch((e) => {
                    dispatch(ACTIONS.USER_AUTH_STATE(CONSTANTS.ERROR, null, e.toString() || `Couldn't ${type}, please try again.`));
                });
        }
    },
    USER_AUTH_EXITING : () => {
        return (dispatch) => {
            dispatch(ACTIONS.USER_AUTH_STATE(CONSTANTS.PENDING));

            let headers = new Headers();
            headers.set("X-Auth", localStorage.getItem("token"));

            let url = new UriBuilder()
                .setScheme(CONSTANTS.SCHEME)
                .setAuthority(CONSTANTS.APP)
                .appendPath(CONSTANTS.USERS)
                .appendPath(CONSTANTS.ME)
                .appendPath(CONSTANTS.TOKEN)
                .build();

            return fetch(url, { method : "DELETE", headers })
                .then((response) => {
                    if(response.status >= 200 && response.status < 400)
                        dispatch(ACTIONS.USER_AUTH_STATE(CONSTANTS.SUCCESS, USER_AUTH_EXITING, response));
                    else Promise.reject();
                })
                .catch((e) => {
                    dispatch(ACTIONS.USER_AUTH_STATE(CONSTANTS.ERROR, null, e.toString() || "Couldn't log out"));
                });
        }
    }
};

const auth = (() => {
    const REDUCER_ACTIONS = {
        USER_AUTH_STATE : (state, action) => {
            if(action.response) {
                return {
                    ...state,
                    status: action.status,
                    message: action.response
                };
            } else {
                return {
                    ...state,
                    status: action.status
                }
            }
        },
        USER_AUTH_LOGGING : (state, action) => {
            return (action.status === "success") ? ({
                ...state,
                state : CONSTANTS.ADD_TOKEN,
                status : action.status,
                token : action.response
            }) : state;
        },
        USER_AUTH_EXITING : (state, action) => {
            return (action.status === "success") ? ({
                ...state,
                state : CONSTANTS.REMOVE_TOKEN,
                status : action.status,
                token : null
            }) : state;
        }
    };

    return (state = [], action) => REDUCER_VALIDATOR(REDUCER_ACTIONS, state, action);
})();

export default auth;