import REDUCER_VALIDATOR from "./reducer-validator"
import UriBuilder from "../utils/UriBuilder";

export const ACTIONS = {
    ENTERTAINER_STATE : (status, response = null) => {
        if(status === "error")
            return {status, response: null};
        else if(status === "success")
            return {status, response};
        //Pending case
        else  return {status, response: null};
    },
    ENTERTAINER_NEWS : () => {
        return (dispatch) => {
            dispatch(ACTIONS.USER_AUTH_STATE("pending"));

            return fetch(new UriBuilder()
                .setScheme("https")
                .setAuthority("YOUR&APP")
                .appendPath("entertainer")
                .appendPath("news")
                .build()
            )
                .then((response) => response.json())
                .then((body) => dispatch(ACTIONS.ENTERTAINER_STATE("success", body)))
                .catch(console.error);
        }
    },
    ENTERTAINER_REVIEWS : () => {
        return (dispatch) => {
            dispatch(ACTIONS.USER_AUTH_STATE("pending"));

            return fetch(new UriBuilder()
                .setScheme("https")
                .setAuthority("YOUR&APP")
                .appendPath("entertainer")
                .appendPath("reviews")
                .build()
            )
                .then((response) => response.json())
                .then((body) => dispatch(ACTIONS.ENTERTAINER_STATE("success", body)))
                .catch(console.error);
        }
    }
};


const auth = (() => {
    const REDUCER_ACTIONS = {
        ENTERTAINER_NEWS : (state, action) => {
            if(action.status === "success") {
                return Object.assign({}, action.response);
            } else return state;
        },
        ENTERTAINER_REVIEWS : (state, action) => {
            if(action.status === "success") {
                return Object.assign({}, action.response);
            } else return state;
        }
    };

    return (state = [], action) => REDUCER_VALIDATOR(REDUCER_ACTIONS, state, action);
})();

export default auth;