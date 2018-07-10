import React from "react"
import {Component} from "react"

import "./style.sass"

import menu_icon from "./../../../resources/icons/social-menu.svg"
import logo from "./../../../resources/assets/logo.svg"
import account from "./../../../resources/icons/account.svg"
import Fireplace from "../FirePlace/FirePlace";

class Settings extends Component {
    constructor(props) {
        super(props);

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
                        <p className="settings-option">Account</p>
                        <p className="settings-option">Feedback</p>
                        <p className="settings-option">About</p>
                    </div>
                )
            }
        </div>
    )
}

export default class Gig extends Component {
    constructor(props) {
        super(props);
    }

    render = () => (
        <div className="gig">
            <div className="gig-header">
                <div className="gig-container">
                    <img className="gig-menu" src={menu_icon}/>
                </div>

                <div className="gig-container-extended">
                    <p className="gig-label">Compartments</p>
                    <p className="gig-link">Community</p>
                    <p className="gig-link">Discover</p>
                    <p className="gig-link">Create</p>
                </div>

                <div className="gig-container-extended">
                    <p className="gig-label">Filter By</p>
                    <p className="gig-link">Song</p>
                    <p className="gig-link">Album</p>
                    <p className="gig-link">Artist</p>
                </div>

                <div className="gig-container">
                    <img className="gig-main-logo-content" src={logo} />
                </div>
            </div>
            <div className="gig-header">
            </div>
            <div className="gig-main">
                <div className="gig-main-header">
                    <input className="gig-finder" type="text" placeholder="Search..."/>

                    {/*{Settings Component}*/}
                    <Settings/>
                </div>
                <div className="gig-main-container">
                    <div className="gig-main-content">
                        {
                            this.props.fireplaces.map((fireplace) => (
                                <Fireplace fireplace={fireplace}/>
                            ))
                        }
                    </div>
                </div>
            </div>
        </div>
    )
}