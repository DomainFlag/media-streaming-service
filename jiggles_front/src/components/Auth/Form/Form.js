import React from "react"
import {Component} from "react"
import { withRouter } from "react-router";
import { Link } from "react-router-dom";

import "./style.sass"
import logo from "./../../../resources/assets/logo.svg"
import Button from "../../Components/Button/Button";
import Input from "../../Components/Input/Input";
import Checkbox from "../../Components/Checkbox/Checkbox";

import auth_google from "../../../resources/icons/social/auth-google.svg"
import auth_facebook from "../../../resources/icons/social/auth_facebook.svg"
import auth_twitter from "../../../resources/icons/social/auth-twitter.svg"
import CONSTANTS from "../../../utils/Constants";
import {connect} from "react-redux";

class Form extends Component {
    constructor(props) {
        super(props);

        this.state = {
            type : props.location.pathname.replace(/(.*)\/(\w+)$/, "$2"),
            password : null,
            validated: null,
            email : this.getRememberedMyContent(),
            rememberMe : false,
            submitted : false
        };
    }

    componentWillReceiveProps = (nextProps) => {
        if(nextProps.authState.status !== null &&
            nextProps.authState.state === CONSTANTS.ADD_TOKEN &&
            nextProps.authState.status === CONSTANTS.SUCCESS) nextProps.history.push('/main');
    };

    onToggleAuth = (authState) => {
        this.setState({
            type : authState
        })
    };

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
        this.setState({
            validated : (e.target.value.length === 0) ? null : this.state.password === e.target.value
        });
    };

    onParentToggle = (state) => {
        this.setState({
            rememberMe : state
        });
    };

    onSubmitUser = () => {
        if(this.state.rememberMe)
            document.cookie = `email=${this.state.email.trim()}`;

        this.props.auth(this.state.type, { email : this.state.email, password : this.state.password});
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
                            {
                                `Social ${this.state.type} with any of...`
                            }
                        </p>
                    </div>
                </div>
                {
                    (this.props.authState.status === CONSTANTS.ERROR && this.props.authState.message) && (
                        <div className="form-error">
                            <p className="form-error-message">{this.props.authState.message}</p>
                        </div>
                    )
                }
                <div className="form-content-container">
                    <Input {...{label : "E-mail", placeholder : "E-mail...", type : "email", value : this.state.email}}
                           cleanState={this.state.submitted}
                           onParentChange={this.onParentChangeEmail}/>
                    <Input {...{label : "Password", placeholder : "Password...", type : "password"}}
                           cleanState={this.state.submitted}
                           onParentChange={this.onParentChangePassword} />
                    {
                        this.state.type === CONSTANTS.SIGNUP && (
                            <Input {...{label : "Repeat Password", placeholder : "Repeat Password...", type : "password"}}
                                   cleanState={this.state.submitted}
                                   onParentChange={this.onParentChangeValidatePassword}
                                   validation={this.state.validated}/>
                        )
                    }
                </div>
            </div>

            <div className="form-action">
                <Checkbox {...{label : "Remember Me"}} onParentToggle={this.onParentToggle}/>
                <Button value={
                    (this.state.type === CONSTANTS.SIGNUP) ? "Sign up" : "Login"
                } selectable={null} onClick={this.onSubmitUser}/>
            </div>

            <div className="form-redirection">
                {
                    this.state.type === CONSTANTS.LOGIN ? (
                        <p className="form-redirection-text">
                            If you have no account, <span className="form-redirection-link"
                                                          onClick={this.onToggleAuth.bind(this, CONSTANTS.SIGNUP)}>Sign up here.</span>
                        </p>
                    ) : (
                        <p className="form-redirection-text">
                            If you already registered, <span className="form-redirection-link"
                                                             onClick={this.onToggleAuth.bind(this, CONSTANTS.LOGIN)}>Log in here.</span>
                        </p>
                    )
                }
            </div>
        </div>
    )
}

const mapStateToProps = (state) => ({
    authState : state.auth
});

export default withRouter(connect(mapStateToProps, null)(Form));