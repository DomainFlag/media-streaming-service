import React from "react"
import {Component} from "react"

import "./style.sass"
import logo from "./../../resources/assets/logo.svg"
import Button from "../Button/Button";
import Input from "../Input/Input";
import Checkbox from "../Checkbox/Checkbox";

import auth_google from "../../resources/icons/social/auth-google.svg"
import auth_facebook from "../../resources/icons/social/auth_facebook.svg"
import auth_twitter from "../../resources/icons/social/auth-twitter.svg"

export default class Form extends Component {
    constructor(props) {
        super(props);
    }

    render = () => (
        <div className="form">
            <div className="form-header">
                <img src={logo} className="form-header-welcoming"/>
            </div>
            <div className="form-content">
                <div className="form-content-social-auth">
                    <div className="form-content-auth-container">
                        <img className="form-content-auth" src={auth_google}/>
                        <img className="form-content-auth" src={auth_facebook}/>
                        <img className="form-content-auth" src={auth_twitter}/>
                    </div>
                    <div className="form-content-auth-header">
                        <p className="form-content-auth-header-text">
                            Social Login with any of...
                        </p>
                    </div>
                </div>
                <div className="form-content-container">
                    <Input {...{label : "E-mail", placeholder : "E-mail..."}}/>
                    <Input {...{label : "Password", placeholder : "Password..."}}/>
                    <Input {...{label : "Repeat Password", placeholder : "Repeat Password..."}}/>
                </div>
            </div>
            <div className="form-action">
                <Checkbox {...{label : "Remember Me"}}/>
                <Button value="submit" selectable={null}/>
            </div>
        </div>
    )
}