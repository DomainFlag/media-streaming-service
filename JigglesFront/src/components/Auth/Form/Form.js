import React from "react"
import {Component} from "react"
import { withRouter } from "react-router";

import "./style.sass"
import logo from "./../../../resources/assets/logo.svg"
import Button from "../../Button/Button";
import Input from "../../Input/Input";
import Checkbox from "../../Checkbox/Checkbox";

import auth_google from "../../../resources/icons/social/auth-google.svg"
import auth_facebook from "../../../resources/icons/social/auth_facebook.svg"
import auth_twitter from "../../../resources/icons/social/auth-twitter.svg"

class Form extends Component {
    constructor(props) {
        super(props);

        this.state = {
            password : null,
            validated: false,
            email : this.getRememberedMyContent(),
            rememberMe : false,
            submitted : false
        };
    }

    getRememberedMyContent = () => {
        return document.cookie.replace(/(?:(?:^|.*;\s*)email\s*=\s*([^;]*).*$)|^.*$/, "$1");
    };

    onParentChangeEmail = (e) => {
        this.setState({
            email : e.target.value
        })
    };

    onParentChangePassword = (e) => {
        this.setState({
            password : e.target.value
        })
    };

    onParentChangeValidatePassword = (e) => {
        if(this.state.password !== e.target.value) {
            console.log("Not the same password");

            this.setState({
                validated : false
            });
        } else {
            console.log("Password validated");

            this.setState({
                validated : false
            });
        }
    };

    onParentToggle = (state) => {
        this.setState({
            rememberMe : state
        });
    };

    onSubmitUser = () => {
        if(this.state.rememberMe)
            document.cookie = `email=${this.state.email.trim()}`;

        this.props.login({ email : this.state.email, password : this.state.password});

        this.setState((prevState) => ({
            submitted : !prevState.submitted
        }));
    };

    render = () => (
        <div className="form">
            <div className="form-header">
                <img className="form-header-welcoming" alt="logo" src={logo}/>
            </div>
            <div className="form-content">
                <div className="form-content-social-auth">
                    <div className="form-content-auth-container">
                        <img className="form-content-auth" alt="google" src={auth_google}/>
                        <img className="form-content-auth" alt="facebook" src={auth_facebook}/>
                        <img className="form-content-auth" alt="twitter" src={auth_twitter}/>
                    </div>
                    <div className="form-content-auth-header">
                        <p className="form-content-auth-header-text">
                            Social Login with any of...
                        </p>
                    </div>
                </div>
                <div className="form-content-container">
                    <Input {...{label : "E-mail", placeholder : "E-mail...", type : "email", value : this.state.email}}
                           cleanState={this.state.submitted}
                           onParentChange={this.onParentChangeEmail}/>
                    <Input {...{label : "Password", placeholder : "Password...", type : "password"}}
                           cleanState={this.state.submitted}
                           onParentChange={this.onParentChangePassword} />
                    <Input {...{label : "Repeat Password", placeholder : "Repeat Password...", type : "password"}}
                           cleanState={this.state.submitted}
                           onParentChange={this.onParentChangeValidatePassword}/>
                </div>
            </div>
            <div className="form-action">
                <Checkbox {...{label : "Remember Me"}} onParentToggle={this.onParentToggle}/>
                <Button value="submit" selectable={null} onParentClick={this.onSubmitUser}/>
            </div>
        </div>
    )
}

export default withRouter(Form);