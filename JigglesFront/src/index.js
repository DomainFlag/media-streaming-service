import React from 'react';
import {Provider} from "react-redux"
import {createStore} from "redux"
import {render} from 'react-dom';
import {applyMiddleware} from "redux"
import ThunkMiddleware from "redux-thunk"
import logger from "redux-logger"
import rootReducer from "./reducers/root-reducer"

import './style.sass';
import Speaker from "./components/Speaker/Speaker";
import Slider from "./components/Slider/Slider";

let app = {};

let store = createStore(rootReducer, app, applyMiddleware(
    ThunkMiddleware,
    logger
));

render(
    <Provider store={store}>
        <Speaker />
    </Provider>,
    document.getElementById("root")
);
