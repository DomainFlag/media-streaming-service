import REDUCER_VALIDATOR from "./reducer-validator"
import UriBuilder from "../utils/UriBuilder";
import CONSTANTS from "../utils/Constants";

const USER_AUTH_STATE = "USER_AUTH_STATE";

export const ACTIONS = {
    USER_AUTH_STATE : (status, response = null) => ({
        type : USER_AUTH_STATE,
        status,
        response
    }),
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
        }
    };

    return (state = [], action) => REDUCER_VALIDATOR(REDUCER_ACTIONS, state, action);
})();

export default settings;