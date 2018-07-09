import React from 'react';
import {Provider} from "react-redux"
import {createStore} from "redux"
import {render} from 'react-dom';
import {applyMiddleware} from "redux"
import ThunkMiddleware from "redux-thunk"
import logger from "redux-logger"
import rootReducer from "./reducers/root-reducer"

import './style.sass';
import Gig from "./components/Gig/FirePlace/FirePlace";

let app = {

};

let store = createStore(rootReducer, app, applyMiddleware(
    ThunkMiddleware,
    logger
));

const social = {
    profile: {
        name: "John Doe",
        connection: 1
    },
    friends: [
        {
            id: 0,
            name: "Joe Davin",
            date: "3 hours ago",
            portraitPath: "./account.svg",
            messages: [
                {
                    id: 0,
                    name: "Jane Doe",
                    body: "Hey!"
                }
            ]
        }, {
            id: 1,
            name: "Jerry Man",
            date: "3 hours ago",
            portraitPath: "./account.svg",
            messages: [
                {
                    id: 0,
                    name: "Jane Doe",
                    body: "Hey!"
                }
            ]
        }, {
            id: 2,
            name: "Terry Luke",
            date: "3 hours ago",
            portraitPath: "./account.svg",
            messages: [
                {
                    id: 0,
                    name: "Jane Doe",
                    body: "Hey!"
                }
            ]
        }, {
            id: 3,
            name: "Rick Malvin",
            date: "3 hours ago",
            portraitPath: "./account.svg",
            messages: [
                {
                    id: 0,
                    name: "Jane Doe",
                    body: "Hey!"
                }
            ]
        }
    ]
};

let comments = [
    {
        "id" : 1,
        "parent" : 0,
        "depth" : 0,
        likes: 6,
        "content" : {
            "text" : "I suppose the concert will be bonkers"
        },
        "header" : {
            "avatar" : "./tests/account.svg",
            "date" : (new Date((Date.now()-Math.random()*50000+25000))).toDateString()
        }
    }, {
        "id" : 2,
        "parent" : 0,
        "depth" : 0,
        likes: 5,
        "content" : {
            "text" : "No shit, I won't miss this gig, yeah!"
        },
        "header" : {
            "avatar" : "./tests/account.svg",
            "date" : (new Date((Date.now()-Math.random()*50000+25000))).toDateString()
        }
    }, {
        "id" : 3,
        "parent" : 2,
        "depth" : 1,
        likes: 0,
        "content" : {
            "text" : "What is the price for the concert ticket??"
        },
        "header" : {
            "avatar" : "./tests/account.svg",
            "date" : (new Date((Date.now()-Math.random()*50000+25000))).toDateString()
        }
    }, {
        "id" : 4,
        "parent" : 3,
        "depth" : 2,
        likes: 13,
        "content" : {
            "text" : "I missed once, not gonna miss it twice"
        },
        "header" : {
            "avatar" : "./tests/account.svg",
            "date" : (new Date((Date.now()-Math.random()*50000+25000))).toDateString()
        }
    }, {
        "id" : 5,
        "parent" : 1,
        "depth" : 1,
        likes: 2,
        "content" : {
            "text" : "Yeahhhh"
        },
        "header" : {
            "avatar" : "./tests/account.svg",
            "date" : (new Date((Date.now()-Math.random()*50000+25000))).toDateString()
        }
    }, {
        "id" : 7,
        "parent" : 5,
        "depth" : 2,
        likes: 2,
        "content" : {
            "text" : "Let's go yeah"
        },
        "header" : {
            "avatar" : "./tests/account.svg",
            "date" : (new Date((Date.now()-Math.random()*50000+25000))).toDateString()
        }
    }, {
        "id" : 6,
        "parent" : 1,
        "depth" : 1,
        likes: 20,
        "content" : {
            "text" : "Oh, Hi Mark!"
        },
        "header" : {
            "avatar" : "./tests/account.svg",
            "date" : (new Date((Date.now()-Math.random()*50000+25000))).toDateString()
        }
    }
];

const gig = [
    {
        id: 0,
        caption: "./tests/interpol_concert.jpg",
        content: "Interpol concert on 21 July, book your tickets here.",
        created_by: "John Doe",
        votes: 25,
        comments: comments,
        created_when: "08/07/2018"
    }, {
        id: 1,
        caption: "./tests/bloc_party.jpg",
        content: "Bloc Party album reissue on 31 July, pre-order right now.",
        created_by: "Jane Doe",
        votes: 13,
        comments: comments,
        created_when: "02/07/2017"
    }
];

render(
    <Provider store={store}>
        <Gig gig={gig}/>
    </Provider>,
    document.getElementById("root")
);
