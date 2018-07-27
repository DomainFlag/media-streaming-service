import React from "react"
import {Component} from "react";

import {ACTIONS} from "./../../reducers/settings"

import account from "./../../resources/icons/account.svg"

import "./style.sass"
import {connect} from "react-redux";
import {withRouter} from "react-router";

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
            toggled: false
        }
    }

    onToggleSettings = () => {
        this.setState((prevState) => ({
            toggled: !prevState.toggled
        }))
    };

    render = () => (
        <div className="gig-profile-container">
            <img className="gig-profile" src={account} onClick={this.onToggleSettings} />
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

const mapDispatchToProps = (dispatch, ownProps) => ({
    logout : () => dispatch(ACTIONS.USER_LOG_OUT(ownProps.history))
});

export default connect(null, mapDispatchToProps)(withRouter(Settings));