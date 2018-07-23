import REDUCER_VALIDATOR from "./reducer-validator"
import UriBuilder from "../utils/UriBuilder";

const USER_AUTH_STATE = "USER_AUTH_STATE";

export const ACTIONS = {
    USER_AUTH_STATE : (status, response = null) => {
        let action = {type: USER_AUTH_STATE};

        if(status === "error")
            return {...action, status, response: null};
        else if(status === "success")
            return {...action, status, response};
        //Pending case
        else  return {...action, status, response: null};
    },
    USER_AUTH : (body = null) => {
        return (dispatch) => {
            dispatch(ACTIONS.USER_AUTH_STATE("pending"));

            let headers = new Headers();
            headers.set("Content-Type", "application/json");
            headers.set("Access-Control-Expose-Headers", "X-Auth");

            return fetch(new UriBuilder()
                .setScheme("https")
                .setAuthority("YOUR&APP")
                .appendPath("users")
                .build(), { method : "POST", body : JSON.stringify(body), headers }
            )
                .then((response) => response.headers)
                .then((headers) => {
                    console.log(headers.get("X-Auth"));
                    return headers;
                })
                .then((headers) => dispatch(ACTIONS.USER_AUTH_STATE("success", headers)))
                .catch(console.error);
        }
    }
};

const auth = (() => {
    const REDUCER_ACTIONS = {
        USER_AUTH : (state, action) => {
            console.log(action.response);
            console.log(JSON.stringify(action.response));
            console.log(action.response.get("X-Auth"));
            if(action.status === "success") {
                return document.cookie = "token=" + action.response
            } else return state;
        }
    };

    return (state = [], action) => REDUCER_VALIDATOR(REDUCER_ACTIONS, state, action);
})();

export default auth;