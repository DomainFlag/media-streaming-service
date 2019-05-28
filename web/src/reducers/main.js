import REDUCER_VALIDATOR from "./reducer-validator"
import UriBuilder from "../utils/UriBuilder";
import CONSTANTS from "../utils/Constants";

const ENTERTAINER_STATE = "ENTERTAINER_STATE";

export const ACTIONS = {
    ENTERTAINER_STATE : (status, response = null) => {
        return {type : ENTERTAINER_STATE, status, response};
    },
    /**
     * fetch types [ 'News', 'Releases' ]
     * @returns {function(*): Promise<Response>}
     * @constructor
     */
    ENTERTAINER_CONTENT : () => {
        return (dispatch) => {
            dispatch(ACTIONS.ENTERTAINER_STATE(CONSTANTS.PENDING));

            let promises = [];

            for(let entertainmentTypeKey in CONSTANTS.ENTERTAINMENT_TYPES) {
                if(CONSTANTS.ENTERTAINMENT_TYPES.hasOwnProperty(entertainmentTypeKey)) {
                    let entertainmentType = CONSTANTS.ENTERTAINMENT_TYPES[entertainmentTypeKey];

                    let headers = new Headers();
                    headers.set("Content-Type", "application/json");

                    let url = new UriBuilder()
                        .setScheme(CONSTANTS.SCHEME)
                        .setAuthority(CONSTANTS.AUTHORITY)
                        .appendPath(CONSTANTS.MAIN)
                        .appendPath(entertainmentType)
                        .build();

                    promises.push(
                        fetch(url, { headers })
                            .then((response) => response.json())
                            .then((body) => {
                                let content = {};
                                content[entertainmentType] = body;

                                return content;
                            })
                            .catch(() => dispatch(ACTIONS.ENTERTAINER_STATE(CONSTANTS.ERROR)))
                    );
                }
            }

            return Promise.all(promises)
                .then((values) => values.reduce((acc, value) => ({...acc, ...value}), {}))
                .then((content) => ({ content }))
                .then((body) => dispatch(ACTIONS.ENTERTAINER_STATE(CONSTANTS.SUCCESS, body)))
                .catch(() => dispatch(ACTIONS.ENTERTAINER_STATE(CONSTANTS.ERROR)));
        }
    },
    /**
     * @param querySearch for every type of [ 'Artist', 'Album', 'Track' ]
     * @returns {function(*): Promise<Response>}
     * @constructor
     */
    ENTERTAINER_QUERY : (querySearch) => {
        return (dispatch) => {
            dispatch(ACTIONS.ENTERTAINER_STATE(CONSTANTS.PENDING));

            let headers = new Headers();
            headers.set("X-Auth", localStorage.getItem("token"));
            headers.set("Content-Type", "application/json");

            let promises = [];

            for(let contentTypeKey in CONSTANTS.CONTENT_TYPES) {
                if(CONSTANTS.CONTENT_TYPES.hasOwnProperty(contentTypeKey)) {
                    let contentType = CONSTANTS.CONTENT_TYPES[contentTypeKey];

                    let queryParameter = {};
                    queryParameter[contentType] = querySearch;

                    let uriBuilder = new UriBuilder()
                        .setScheme(CONSTANTS.SCHEME)
                        .setAuthority(CONSTANTS.AUTHORITY)
                        .appendPath(CONSTANTS.QUERY)
                        .appendPath(contentType)
                        .appendQueryParameter(queryParameter)
                        .build();

                    promises.push(
                        fetch(uriBuilder, {headers})
                            .then((response) => response.json())
                            .catch(() => dispatch(ACTIONS.ENTERTAINER_STATE(CONSTANTS.ERROR)))
                    );
                }
            }

            return Promise.all(promises)
                .then((values) => values.reduce((acc, value) => ({...acc, ...value}), {}))
                .then((search) => ({ search }))
                .then((body) => dispatch(ACTIONS.ENTERTAINER_STATE(CONSTANTS.SUCCESS, body)))
                .catch(() => dispatch(ACTIONS.ENTERTAINER_STATE(CONSTANTS.ERROR)));
        };
    }
};


const main = (() => {
    const REDUCER_ACTIONS = {
        ENTERTAINER_STATE : (state, action) => {
            if(action.status === CONSTANTS.SUCCESS) {
                return Object.assign({}, state, action.response);
            } else return state;
        }
    };

    return (state = [], action) => REDUCER_VALIDATOR(REDUCER_ACTIONS, state, action);
})();

export default main;