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
import {ACTIONS} from "./reducers/account";
import Welcome from "./components/Welcome/Welcome";
import Main from "./components/Main/Main";
import Auth from "./components/Auth/Auth/Auth";
import Forum from "./components/Forum/Forum";
import Studio from "./components/Studio/Studio";
import Account from "./components/Settings/Account/Account";

let app = {
    account : {
        state : null,
        status : null,
        user : {},
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


if(store.getState().account.token !== null)
    store.dispatch(ACTIONS.USER_ACCOUNT());

store.subscribe(() => {
    let state = store.getState();

    if(state.account.state === null || state.account.status !== CONSTANTS.SUCCESS)
        return;

    if(state.account.state === CONSTANTS.ADD_TOKEN) {
        localStorage.setItem("token", state.account.token);
    } else if(state.account.state === CONSTANTS.REMOVE_TOKEN) {
        localStorage.removeItem("token");
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
                <Route exact path="/account" component={Account}/>
                <Route exact path="/forum" component={Forum}/>
                <Route exact path="/studio" component={Studio}/>
            </Switch>
        </BrowserRouter>
    </Provider>,
    document.getElementById("root")
);
