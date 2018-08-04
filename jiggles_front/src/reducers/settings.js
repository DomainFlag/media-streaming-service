import REDUCER_VALIDATOR from "./reducer-validator"
import UriBuilder from "../utils/UriBuilder";
import CONSTANTS from "../utils/Constants";

const USER_AUTH_STATE = "USER_AUTH_STATE";
const DEFAULT_IMAGES_STATE = "DEFAULT_IMAGES_STATE";

export const ACTIONS = {
    USER_AUTH_STATE : (status, response = null) => ({
        type : USER_AUTH_STATE,
        status,
        response
    }),
    DEFAULT_IMAGES_STATE : (status, response = null) => ({
        type : DEFAULT_IMAGES_STATE,
        status,
        response
    }),
    DEFAULT_IMAGES : () => {
        return (dispatch) => {
            dispatch(ACTIONS.DEFAULT_IMAGES_STATE(CONSTANTS.PENDING));

            let headers = new Headers();

            let url = new UriBuilder()
                .setScheme(CONSTANTS.SCHEME)
                .setAuthority(CONSTANTS.AUTHORITY)
                .appendPath(CONSTANTS.ACCOUNT)
                .appendPath(CONSTANTS.IMAGES)
                .build();

            return fetch(url, { method : "GET", headers })
                .then((response) => {
                    if(response.status >= 200 && response.status < 400) {
                        return response.json();
                    } else Promise.reject();
                })
                .then((response) => dispatch(ACTIONS.DEFAULT_IMAGES_STATE(CONSTANTS.SUCCESS, response)))
                .catch(() => {
                    dispatch(ACTIONS.DEFAULT_IMAGES_STATE(CONSTANTS.ERROR));
                });
        }
    },
    USER_LOG_OUT : () => {
        return (dispatch) => {
            dispatch(ACTIONS.USER_AUTH_STATE(CONSTANTS.PENDING));

            let headers = new Headers();
            headers.set("X-Auth", localStorage.getItem("token"));

            let url = new UriBuilder()
                .setScheme(CONSTANTS.SCHEME)
                .setAuthority(CONSTANTS.AUTHORITY)
                .appendPath(CONSTANTS.USERS)
                .appendPath(CONSTANTS.ME)
                .appendPath(CONSTANTS.TOKEN)
                .build();

            return fetch(url, { method : "DELETE", headers })
                .then((response) => {
                    if(response.status >= 200 && response.status < 400) {
                        dispatch(ACTIONS.USER_AUTH_STATE(CONSTANTS.SUCCESS, response));
                    } else Promise.reject();
                })
                .catch(() => {
                    dispatch(ACTIONS.USER_AUTH_STATE(CONSTANTS.ERROR));
                });
        }
    }
};

const settings = (() => {
    const REDUCER_ACTIONS = {
        USER_AUTH_STATE : (state, action) => {
            if(action.status === "success")
                return {
                    ...state,
                    status: CONSTANTS.REMOVE_TOKEN,
                    token: null
                };

            return state;
        },
        DEFAULT_IMAGES_STATE : (state, action) => {
            if(action.status === "success")
                return {
                    ...state,
                    default : action.response,
                };

            return state;
        }
    };

    return (state = [], action) => REDUCER_VALIDATOR(REDUCER_ACTIONS, state, action);
})();

export default settings;