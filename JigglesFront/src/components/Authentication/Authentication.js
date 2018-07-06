import React from "react"
import {Component} from "react"

import Form from "../Form/Form";

import "./style.sass"
import AmusingAuth from "../AmusingAuth/PlayfullAuth";

export default class Authentication extends Component {
    constructor(props) {
        super(props);
    }

    render = () => (
        <div className="authentication">
            <div className="authentication-container">
                <AmusingAuth />
            </div>
            <div className="authentication-container">
                <Form />
            </div>
        </div>
    )
}