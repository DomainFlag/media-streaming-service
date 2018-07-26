import REDUCER_VALIDATOR from "./reducer-validator"
import UriBuilder from "../utils/UriBuilder";
import CONSTANTS from "../utils/Constants";

const ENTERTAINER_STATE = "ENTERTAINER_STATE";

export const ACTIONS = {
    ENTERTAINER_STATE : (status, typeQuery = null, response = null) => {
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
    /**
     * @param type one of [ 'News', 'Reviews' ]
     * @returns {function(*): Promise<Response>}
     * @constructor
     */
    ENTERTAINER_CONTENT : (type) => {
        return (dispatch) => {
            dispatch(ACTIONS.ENTERTAINER_STATE(CONSTANTS.PENDING));

            return fetch(new UriBuilder()
                .setScheme(CONSTANTS.SCHEME)
                .setAuthority(CONSTANTS.APP)
                .appendPath(CONSTANTS.ENTERTAINER)
                .appendPath(type)
                .build()
            )
                .then((response) => response.json())
                .then((body) => dispatch(ACTIONS.ENTERTAINER_STATE(CONSTANTS.SUCCESS, type, body)))
                .catch(() => dispatch(ACTIONS.ENTERTAINER_STATE(CONSTANTS.ERROR)));
        }
    },
    /**
     * @param type one of [ 'Artist', 'Album', 'Track' ]
     * @param querySearch
     * @returns {function(*): Promise<Response>}
     * @constructor
     */
    ENTERTAINER_QUERY : (type, querySearch) => {
        return (dispatch) => {
            dispatch(ACTIONS.ENTERTAINER_STATE(CONSTANTS.PENDING));

            let headers = new Headers();
            headers.set("X-Auth", document.cookie.replace(/(?:(?:^|.*;\s*)token\s*=\s*([^;]*).*$)|^.*$/, "$1"));

            let queryParameter = {};
            queryParameter[type] = querySearch;

            let uriBuilder = new UriBuilder()
                .setScheme(CONSTANTS.SCHEME)
                .setAuthority(CONSTANTS.APP)
                .appendPath(type)
                .appendQueryParameter(queryParameter)
                .build();

            return fetch(uriBuilder, { headers })
                .then((response) => response.json())
                .then((body) => dispatch(ACTIONS.ENTERTAINER_STATE(CONSTANTS.SUCCESS, type, body)))
                .catch(() => dispatch(ACTIONS.ENTERTAINER_STATE(CONSTANTS.ERROR)));
        }
    }
};


const entertainer = (() => {
    const REDUCER_ACTIONS = {
        ENTERTAINER_STATE : (state, action) => {
            if(action.status === CONSTANTS.SUCCESS) {
                console.log(JSON.stringify(action.response));
                return Object.assign({}, state, action.response);
            } else return state;
        }
    };

    return (state = [], action) => REDUCER_VALIDATOR(REDUCER_ACTIONS, state, action);
})();

export default entertainer;