import React from "react"
import {Component} from "react"

import logo from "../../resources/assets/logo.svg"
import Form from "../Form/Form";

export default class Welcome extends Component {
    constructor(props) {
        super(props);
    }

    render = () => (
        <div className="welcome">
            <div className="welcome-header">
                <div className="welcome-header-logo">
                    <img id="welcome-header-logo" src={logo}/>
                </div>
                <div className="welcome-header-menu">
                    <div className="welcome-header-links">
                        <p className="welcome-header-links-item">Home</p>
                        <p className="welcome-header-links-item">About</p>
                        <p className="welcome-header-links-item">Contact</p>
                    </div>
                    <div className="welcome-header-authentication">
                        <p className="welcome-header-authentication-auth">Sign Up</p>
                        <p className="welcome-header-authentication-auth">Login</p>
                    </div>
                </div>
            </div>
            <div className="welcome-body">
                <Form/>
            </div>
        </div>
    )
}