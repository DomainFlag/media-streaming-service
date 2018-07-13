import REDUCER_VALIDATOR from "./reducer-validator"
import UriBuilder from "./../utils/uri-builder";
const SEARCH_QUERY = "SEARCH_QUERY";

export const ACTIONS = {
    SEARCH_QUERY : (status, response = null) => {
        let action = {type: SEARCH_QUERY};

        if(status === "error")
            return {...action, status, response: null};
        else if(status === "success")
            return {...action, status, response};
        //Pending case
        else  return {...action, status, response: null};
    },
    FETCH_QUERY : (payload = null) => {
        return (dispatch) => {

            dispatch(ACTIONS.SEARCH_QUERY("pending"));

            // return queryFetch()
            //     .then((data) => {
            //         console.log(data.body);
            //         return data;
            //     })
            //     .then((data) => data.json())
            //     .then(console.log)
            //     .then((data) => dispatch(ACTIONS.SEARCH_QUERY("success", data)))
            //     .catch(console.error);
        }
    }
};


const main = (() => {
    const REDUCER_ACTIONS = {
        FETCH_QUERY : (state, action) => {
            if(action.status === "success") {
                console.log(action);
                return Object.assign({}, state, {});
            } else return state;
        }
    };

    return (state = [], action) => REDUCER_VALIDATOR(REDUCER_ACTIONS, state, action);
})();

export default main;