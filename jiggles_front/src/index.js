import React from 'react';
import {Provider} from "react-redux"
import {createStore} from "redux"
import {render} from 'react-dom';
import {applyMiddleware} from "redux"
import ThunkMiddleware from "redux-thunk"
import logger from "redux-logger"
import rootReducer from "./reducers/root-reducer"

import './style.sass';

import { BrowserRouter, Route, Switch} from "react-router-dom";
import CONSTANTS from "./utils/Constants";
import {ACTIONS} from "./reducers/auth";
import Welcome from "./components/Welcome/Welcome";
import Main from "./components/Main/Main";
import Auth from "./components/Auth/Auth/Auth";
import Forum from "./components/Forum/Forum";
import Studio from "./components/Studio/Studio";
import ThreadCreator from "./components/Forum/ThreadCreator/ThreadCreator";

let app = {
    auth : {
        state : null,
        status : null,
        token : localStorage.getItem("token") || null
    },
    main : {
        content : {
            news : [],
            releases : []
        },
        search : null
    },
    forum : {
        threads : []
    }
};

let store = createStore(rootReducer, app, applyMiddleware(
    ThunkMiddleware,
    logger
));

if(store.getState().auth.token !== null)
    store.dispatch(ACTIONS.USER_ACCOUNT());

store.subscribe(() => {
    let state = store.getState();

    if(state.auth === null || state.auth === undefined)
        return;

    if(state.auth.status === CONSTANTS.SUCCESS) {
        if(state.auth.state === CONSTANTS.ADD_TOKEN) {
            localStorage.setItem("token", state.auth.token);
        } else if(state.auth.state === CONSTANTS.REMOVE_TOKEN) {
            localStorage.removeItem("token");
        }
    }
});

render(
    <Provider store={store}>
        <BrowserRouter>
            <Switch>
                <Route exact path="/" component={Welcome}/>
                <Route exact path="/auth/signup" component={Auth}/>
                <Route exact path="/auth/login" component={Auth}/>
                <Route exact path="/main" component={Main}/>
                <Route exact path="/forum" component={Forum}/>
                <Route exact path="/forum/create" component={ThreadCreator}/>
                <Route exact path="/studio" component={Studio}/>
            </Switch>
        </BrowserRouter>
    </Provider>,
    document.getElementById("root")
);