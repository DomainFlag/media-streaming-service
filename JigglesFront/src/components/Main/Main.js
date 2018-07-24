import React from "react"
import {Component} from "react"
import {connect} from "react-redux"

import logo from "../../resources/assets/logo.svg"

import "./style.sass"
import Button from "../Button/Button";

export class Main extends Component {
    constructor(props) {
        super(props);
    }

    render = () => (
        <div className="main">
            <div className="main-header">
                <div className="main-header-logo">
                    <img id="main-header-logo" src={logo}/>
                    <div className="main-header-authentication">
                        <p className="main-header-authentication-auth">Sign Up</p>
                        <p className="main-header-authentication-auth">Login</p>
                    </div>
                </div>
                <div className="divider"/>
                <div className="main-header-menu">
                    <div className="main-header-links">
                        <p className="main-header-links-item">Home</p>
                        <p className="main-header-links-item">About</p>
                        <p className="main-header-links-item">Contact</p>
                    </div>
                </div>
            </div>
            <div className="main-body">
                <Button {...{
                    value: "Fetch Token",
                    selectable: null,
                    onClick: this.props.fetch_token
                }}/>
                <iframe
                    src="https://open.spotify.com/embed?uri=spotify:track:11dFghVXANMlKmJXsNCbNl"
                    width="300" height="380" frameBorder="0" allowTransparency="true"
                    allow="encrypted-media"/>
            </div>
        </div>
    )
}