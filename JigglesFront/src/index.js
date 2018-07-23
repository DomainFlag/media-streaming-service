import React from 'react';
import {Provider} from "react-redux"
import {createStore} from "redux"
import {render} from 'react-dom';
import {applyMiddleware} from "redux"
import ThunkMiddleware from "redux-thunk"
import logger from "redux-logger"
import rootReducer from "./reducers/root-reducer"

import './style.sass';

import { BrowserRouter as Router, Route, Switch} from "react-router-dom";
import Auth from "./components/Auth/Auth/Auth";
import {Main} from "./components/Main/Main";
import Welcome from "./components/Welcome/Welcome";

let app = {};

let store = createStore(rootReducer, app, applyMiddleware(
    ThunkMiddleware,
    logger
));

// const history = syncHistoryWithStore(browserHistory, store);

render(
    <Provider store={store}>
        <Router>
            <Switch>
                <Route path="/" component={Auth}/>
            </Switch>
        </Router>
    </Provider>,
    document.getElementById("root")
);
