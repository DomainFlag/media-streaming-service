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
import {Main} from "./components/Main/Main";
import Entertainer from "./components/Entertainer/Entertainer";
import GigCreator from "./components/Gig/GigCreator/GigCreator";

let app = {};

let store = createStore(rootReducer, app, applyMiddleware(
    ThunkMiddleware,
    logger
));

render(
    <Provider store={store}>
        <BrowserRouter>
            <Switch>
                <Route exact path="/" component={GigCreator}/>
            </Switch>
        </BrowserRouter>
    </Provider>,
    document.getElementById("root")
);
