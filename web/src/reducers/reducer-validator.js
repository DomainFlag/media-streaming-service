const REDUCER_VALIDATOR = (reducerActions, state, action) => {
    if(reducerActions.hasOwnProperty(action.type))
        return reducerActions[action.type](state, action);
    else return state;
};

export default REDUCER_VALIDATOR;