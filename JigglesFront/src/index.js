import React from 'react';
import {Provider} from "react-redux"
import {createStore} from "redux"
import {render} from 'react-dom';
import {applyMiddleware} from "redux"
import ThunkMiddleware from "redux-thunk"
import logger from "redux-logger"
import rootReducer from "./reducers/root-reducer"

import './style.sass';
import news from "./dummy/news";
import Entertainer from "./components/Entertainer/Entertainer";

let app = {};

let store = createStore(rootReducer, app, applyMiddleware(
    ThunkMiddleware,
    logger
));

render(
    <Provider store={store}>
        <Entertainer news={news}/>
    </Provider>,
    document.getElementById("root")
);
