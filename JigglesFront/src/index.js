import React from 'react';
import {Provider} from "react-redux"
import {createStore} from "redux"
import {render} from 'react-dom';
import {applyMiddleware} from "redux"
import ThunkMiddleware from "redux-thunk"
import logger from "redux-logger"
import rootReducer from "./reducers/root-reducer"

import Chat from "./components/Gig/Chat/Chat"

import './style.sass';
import Search from "./components/Search/Search";

let app = {

};

let store = createStore(rootReducer, app, applyMiddleware(
    ThunkMiddleware,
    logger
));

const interaction = {
    id: 6,
    messages: [
        {
            id: 0,
            name: "Jane Doe",
            body: "Hey!"
        }, {
            id: 1,
            name: "John Doe",
            body: "Hi Back!"
        }, {
            id: 2,
            name: "Jane Doe",
            body: "Do you know smth about the weather?"
        }, {
            id: 3,
            name: "Jane Doe",
            body: "You know the weather in MD?"
        }, {
            id: 4,
            name: "John Doe",
            body: "It's like s**t"
        }, {
            id: 5,
            name: "John Doe",
            body: "As always"
        }
    ]
};

const profile = {
    profile: {
        name: "John Doe",
        connection: 1
    },
    friends: [
        {
            id: 0,
            name: "Joe Davin",
            connection: 2,
            portraitPath: "./account.svg"
        }, {
            id: 1,
            name: "Jerry Man",
            connection: 0,
            portraitPath: "./account.svg"
        }, {
            id: 2,
            name: "Terry Luke",
            connection: 1,
            portraitPath: "./account.svg"
        }, {
            id: 3,
            name: "Rick Malvin",
            connection: 2,
            portraitPath: "./account.svg"
        }, {
            id: 4,
            name: "Honey Berry",
            connection: 0,
            portraitPath: "./account.svg"
        }, {
            id: 5,
            name: "Tracey Georgia",
            connection: 2,
            portraitPath: "./account.svg"
        }
    ]
};

render(
    <Provider store={store}>
        <Chat profile={profile} messages={interaction.messages} placeholder="Message here..."/>
    </Provider>,
    document.getElementById("root")
);
