import REDUCER_VALIDATOR from "./reducer-validator"
import UriBuilder from "../utils/UriBuilder";
import CONSTANTS from "../utils/Constants";

const ENTERTAINER_STATE = "ENTERTAINER_STATE";

export const ACTIONS = {
    ENTERTAINER_STATE : (type, status, typeQuery = null, response = null) => {
        if(typeQuery === null)
            return {type : ENTERTAINER_STATE, status};
        else {
            let payloadType = {};
            payloadType[typeQuery] = response;

            return {
                type : ENTERTAINER_STATE,
                status,
                response : payloadType
            };
        }
    },
    GIG_CONTENT : (filterBy) => {
        return (dispatch) => {
            dispatch(ACTIONS.ENTERTAINER_STATE(CONSTANTS.PENDING));

            return fetch(new UriBuilder()
                .setScheme(CONSTANTS.SCHEME)
                .setAuthority(CONSTANTS.APP)
                .appendPath(CONSTANTS.GIG)
                .appendPath(filterBy)
                .build()
            )
                .then((response) => response.json())
                .then((body) => dispatch(ACTIONS.ENTERTAINER_STATE(CONSTANTS.SUCCESS, filterBy, body)))
                .catch(() => dispatch(ACTIONS.ENTERTAINER_STATE(CONSTANTS.ERROR)));
        }
    },
    GIG_PUT_CONTENT : (payload) => {
        return (dispatch) => {
            dispatch(ACTIONS.ENTERTAINER_STATE(CONSTANTS.PENDING));

            let url = new UriBuilder()
                .setScheme(CONSTANTS.SCHEME)
                .setAuthority(CONSTANTS.APP)
                .appendPath(CONSTANTS.GIG)
                .appendPath(CONSTANTS.CREATE)
                .appendPath(payload)
                .build();

            let headers = new Headers();
            headers.set("Content-Type", "application/json");

            // TODO(0) do the image

            return fetch(url, { method : "POST", }
            )
                .then((response) => response.json())
                .then((body) => dispatch(ACTIONS.ENTERTAINER_STATE(CONSTANTS.SUCCESS, filterBy, body)))
                .catch(() => dispatch(ACTIONS.ENTERTAINER_STATE(CONSTANTS.ERROR)));
        }
    }
};

const gig = (() => {
    const REDUCER_ACTIONS = {
        ENTERTAINER_STATE : (state, action) => {
            if(action.status === CONSTANTS.SUCCESS) {
                return Object.assign({}, state, action.response);
            } else return state;
        }
    };

    return (state = [], action) => REDUCER_VALIDATOR(REDUCER_ACTIONS, state, action);
})();

export default gig;