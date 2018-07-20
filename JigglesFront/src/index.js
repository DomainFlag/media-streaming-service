import React from 'react';
import {Provider} from "react-redux"
import {createStore} from "redux"
import {render} from 'react-dom';
import {applyMiddleware} from "redux"
import ThunkMiddleware from "redux-thunk"
import logger from "redux-logger"
import rootReducer from "./reducers/root-reducer"

import './style.sass';
import Studio from "./components/Studio/Studio";

let app = {};

let store = createStore(rootReducer, app, applyMiddleware(
    ThunkMiddleware,
    logger
));

render(
    <Provider store={store}>
        <Studio />
    </Provider>,
    document.getElementById("root")
);
