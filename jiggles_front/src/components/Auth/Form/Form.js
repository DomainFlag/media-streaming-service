import React from "react"
import {Component} from "react"
import { withRouter } from "react-router";

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
            name : null,
            validated: null,
            email : this.getRememberedMyContent(),
            rememberMe : false,
            submitted : false
        };
    }

    componentWillReceiveProps = (nextProps) => {
        if(nextProps.account.status !== null &&
            nextProps.account.state === CONSTANTS.ADD_TOKEN &&
            nextProps.account.status === CONSTANTS.SUCCESS) nextProps.history.push('/main');
    };

    onToggleAuth = (account) => {
        this.setState({
            type : account
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

    onParentChangeName = (e) => {
        this.setState({
            name : e.target.value
        });
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

        this.props.auth(this.state.type, { email : this.state.email,
            name : this.state.name, password :
            this.state.password
        });
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
                    (this.props.account.status === CONSTANTS.ERROR && this.props.account.message) && (
                        <div className="form-error">
                            <p className="form-error-message">{this.props.account.message}</p>
                        </div>
                    )
                }
                <div className="form-content-container">
                    <Input {...{label : "E-mail", placeholder : "E-mail...", type : "email", value : this.state.email}}
                           onParentChange={this.onParentChangeEmail}/>
                    {
                        this.state.type === CONSTANTS.SIGNUP && (
                            <Input {...{label : "Name", placeholder : "Your name...", type : "text"}}
                                   onParentChange={this.onParentChangeName}/>
                        )
                    }
                    <Input {...{label : "Password", placeholder : "Password...", type : "password"}}
                           onParentChange={this.onParentChangePassword} />
                    {
                        this.state.type === CONSTANTS.SIGNUP && (
                            <Input {...{label : "Repeat Password", placeholder : "Repeat Password...", type : "password"}}
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
    account : state.account
});

export default withRouter(connect(mapStateToProps, null)(Form));