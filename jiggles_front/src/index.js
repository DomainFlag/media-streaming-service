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
import Welcome from "./components/Welcome/Welcome";
import {Entertainer} from "./components/Entertainer/Entertainer";
import Auth from "./components/Auth/Auth/Auth";
import Gig from "./components/Gig/Gig/Gig";
import Studio from "./components/Studio/Studio";

let app = {};

let store = createStore(rootReducer, app, applyMiddleware(
    ThunkMiddleware,
    logger
));

render(
    <Provider store={store}>
        <BrowserRouter>
            <Switch>
                <Route exact path="/" component={Welcome}/>
                <Route exact path="/auth/signup" component={Auth}/>
                <Route exact path="/auth/login" component={Auth}/>
                <Route exact path="/main" component={Entertainer}/>
                <Route exact path="/forum" component={Gig}/>
                <Route exact path="/studio" component={Studio}/>
            </Switch>
        </BrowserRouter>
    </Provider>,
    document.getElementById("root")
);
