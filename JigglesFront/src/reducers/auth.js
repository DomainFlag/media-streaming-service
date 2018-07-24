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
    USER_AUTH : (redirect, body = null) => {
        return (dispatch) => {
            dispatch(ACTIONS.USER_AUTH_STATE(CONSTANTS.PENDING));

            let headers = new Headers();
            headers.set("Content-Type", "application/json");
            headers.set("Access-Control-Expose-Headers", "X-Auth");

            return fetch(new UriBuilder()
                    .setScheme("https")
                    .setAuthority(CONSTANTS.APP)
                    .appendPath("users")
                    .build(), { method : "POST", body : JSON.stringify(body), headers })
                .then((response) => {
                    if(response.status >= 200 && response.status < 400) {
                        if(response.headers.get("X-Auth") !== null) {
                            dispatch(ACTIONS.USER_AUTH_STATE(CONSTANTS.SUCCESS, response.headers.get("X-Auth")));

                            // Redirection
                            redirect.push("/entertainer");
                        } else Promise.reject();
                    } else Promise.reject();
                })
                .catch(() => {
                    dispatch(ACTIONS.USER_AUTH_STATE(CONSTANTS.ERROR));
                });
        }
    }
};

const auth = (() => {
    const REDUCER_ACTIONS = {
        USER_AUTH_STATE : (state, action) => {
            if(action.status === "success")
                // Token added to keep user logged in
                document.cookie = "token=" + action.response;

            return state;
        }
    };

    return (state = [], action) => REDUCER_VALIDATOR(REDUCER_ACTIONS, state, action);
})();

export default auth;