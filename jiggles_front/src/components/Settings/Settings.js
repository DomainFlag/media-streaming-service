import React from "react"
import {Component} from "react";

import {ACTIONS} from "./../../reducers/auth"

import account from "./../../resources/icons/account.svg"

import "./style.sass"
import {connect} from "react-redux";
import {withRouter} from "react-router";
import CONSTANTS from "../../utils/Constants";
import UriBuilder from "../../utils/UriBuilder";

export class Settings extends Component {
    constructor(props) {
        super(props);

        this.options = [
            {
                optionName : "Account",
                callback : () => {}
            }, {
                optionName : "Feedback",
                callback : () => {}
            }, {
                optionName : "About",
                callback : () => {}
            }, {
                optionName: "Log out",
                callback: this.props.logout
            }
        ];

        this.state = {
            toggled : false,
            caption : this.setCaption(props)
        }
    }

    setCaption = (props) => (props.authState.account) ? new UriBuilder()
        .setScheme(CONSTANTS.SCHEME)
        .setAuthority(CONSTANTS.AUTHORITY)
        .appendPath(props.authState.account.caption)
        .build() : null;

    componentWillReceiveProps = (nextProps) => {
        if(nextProps.authState.status !== null &&
            nextProps.authState.state === CONSTANTS.REMOVE_TOKEN &&
            nextProps.authState.status === CONSTANTS.SUCCESS)
            nextProps.history.push('/');


        this.setState({
            caption : this.setCaption(nextProps)
        })
    };

    onToggleSettings = () => {
        this.setState((prevState) => ({
            toggled: !prevState.toggled
        }))
    };

    render = () => (
        <div className="settings-profile-container">
            {
                this.props.authState.account && (
                    <img className="settings-profile-icon" src={this.state.caption} onClick={this.onToggleSettings} />
                )
            }
            {
                this.state.toggled && (
                    <div className="settings-profile">
                        <div className="settings-arrow"/>
                        {
                            this.options.map((option) => (
                                <p className="settings-option" onClick={option.callback}>{option.optionName}</p>
                            ))
                        }
                    </div>
                )
            }
        </div>
    )
}

const mapDispatchToProps = (dispatch) => ({
    logout : () => dispatch(ACTIONS.USER_AUTH_EXITING())
});

const mapStateToProps = (state) => ({
    authState : state.auth
});

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(Settings));