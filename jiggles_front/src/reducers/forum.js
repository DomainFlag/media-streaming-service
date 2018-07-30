import REDUCER_VALIDATOR from "./reducer-validator"
import UriBuilder from "../utils/UriBuilder";
import CONSTANTS from "../utils/Constants";

const FORUM_STATE = "FORUM_STATE";
const HANDLE_THREAD = "HANDLE_THREAD";

export const ACTIONS = {
    FORUM_STATE : (status, requestType, response = null) => ({
        type : FORUM_STATE,
        requestType,
        status,
        response
    }),
    /**
     * @param type one of [ "DELETE", "POST", "GET", "PUT" ]
     * @param {null} body { content, caption }
     * @returns {function(*): Promise<Response>}
     * @constructor
     */
    HANDLE_THREAD : (type = "GET", body = null) => {
        return (dispatch) => {
            dispatch(ACTIONS.FORUM_STATE(CONSTANTS.PENDING, type));

            let headers = new Headers();
            headers.set("Content-Type", "application/json");

            let url = new UriBuilder()
                .setScheme(CONSTANTS.SCHEME)
                .setAuthority(CONSTANTS.APP)
                .appendPath(CONSTANTS.FORUM)
                .appendPath(CONSTANTS.THREAD)
                .build();

            return fetch(url, { method : type, headers, body })
                .then((response) => response.json())
                .then((body) => (type === "GET") ? dispatch(({
                    type : HANDLE_THREAD,
                    requestType : "GET",
                    response : { threads : body },
                    status : CONSTANTS.SUCCESS
                })) : dispatch(ACTIONS.FORUM_STATE(CONSTANTS.SUCCESS, type, body)))
                .catch(() => dispatch(ACTIONS.FORUM_STATE(CONSTANTS.ERROR, type)));
        }
    },
    /**
     * @param type one of [ "DELETE", "POST", "PUT" ]
     * @param {null} body { _id, content, parent, depth }
     * @returns {function(*): Promise<Response>}
     * @constructor
     */
    HANDLE_MESSAGE : (type = "POST", body = null) => {
        return (dispatch) => {
            dispatch(ACTIONS.FORUM_STATE(CONSTANTS.PENDING, type));

            let headers = new Headers();
            headers.set("Content-Type", "application/json");

            let url = new UriBuilder()
                .setScheme(CONSTANTS.SCHEME)
                .setAuthority(CONSTANTS.APP)
                .appendPath(CONSTANTS.FORUM)
                .appendPath(CONSTANTS.THREAD)
                .appendPath(CONSTANTS.COMMENT)
                .build();

            // TODO(0) do the image

            return fetch(url, { method : type, headers, body })
                .then((response) => response.json())
                .then((body) => dispatch(ACTIONS.ENTERTAINER_STATE(CONSTANTS.SUCCESS, type, body)))
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
            console.log(state);
            console.log(action);
            if(action.requestType === "GET") {
                if(action.status === CONSTANTS.SUCCESS) {
                    return Object.assign({}, state, action.response);
                } else return state;
            }
        }
    };

    return (state = [], action) => REDUCER_VALIDATOR(REDUCER_ACTIONS, state, action);
})();

export default forum;