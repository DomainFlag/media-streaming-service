import React from "react"
import {Component} from "react"

import logo from "../../resources/assets/logo.svg"
import Form from "../Form/Form";

import "./style.sass"

export default class Authentication extends Component {
    constructor(props) {
        super(props);
    }

    render = () => (
        <div className="authentication">
            <div className="authentication-container">
            </div>
            <div className="authentication-container">
                <Form />
            </div>
        </div>
    )
}