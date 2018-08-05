import React from "react"
import {Component} from "react";

import "./style.sass"
import {connect} from "react-redux";
import {withRouter} from "react-router";
import back from "./../../../resources/icons/back-arrow.svg"
import ReactDOM from "react-dom";
import {ACTIONS} from "../../../reducers/settings";

export class Account extends Component {
    constructor(props) {
        super(props);

        this.props.getDefaultImages();

        this.stats = [
            "Name",
            "Email",
            "Type"
        ];

        this.account = [
            {
                option : "Profile",
            }
        ];

        this.state = {
            feature : 0
        }
    }

    componentDidMount = () => {
        ReactDOM.findDOMNode(this).parentNode.className = "non-extendable";
    };

    render = () => (
        <div className="account">
            <div className="account-header">
                <img className="account-header-icon" alt="back" src={back} onClick={this.props.history.goBack}/>
                <p className="account-header-text">Account</p>
            </div>
            <div className="account-options">
                {
                    this.account.map((feature, index) => (
                        <p className={"account-option " + ((this.state.feature === index) && "account-option-fired")}>{feature.option}</p>
                    ))
                }
            </div>
            <div className="account-feature">
                {
                    (this.props.default && this.props.user.hasOwnProperty("caption")) && (
                        <div className="account-feature-info">
                            <p className="account-feature-label">
                                Account icons:
                            </p>
                            <div className="account-feature-captions">
                                {
                                    this.props.default.map((img) => (
                                        <img className={"account-feature-img " + (!img.match(this.props.user.caption) && "account-feature-active")} alt="icon" src={img}/>
                                    ))
                                }
                            </div>
                        </div>
                    )
                }
                {
                    this.stats.map((stat) => (
                        <div className="account-feature-info">
                            <p className="account-feature-label">
                                {stat + ":"}
                            </p>
                            <p className="account-feature-value">
                                {this.props.user.hasOwnProperty(stat.toLowerCase()) && this.props.user[stat.toLowerCase()]}
                            </p>
                        </div>
                    ))
                }
            </div>
        </div>
    )
}

const mapDispatchToProps = (dispatch) => ({
    getDefaultImages : () => dispatch(ACTIONS.DEFAULT_IMAGES())
});

const mapStateToProps = (state) => ({
    user : state.account.user,
    default : state.settings.default
});

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(Account));