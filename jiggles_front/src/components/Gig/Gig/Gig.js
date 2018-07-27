import React from "react"
import {Component} from "react"

import "./style.sass"

import Settings from "../../Settings/Settings"
import menu_icon from "./../../../resources/icons/social-menu.svg"
import logo from "./../../../resources/assets/logo-white.svg"
import Fireplace from "../FirePlace/FirePlace";
import ReactDOM from "react-dom";

import fireplaces from "../../../dummy/fireplaces"

export default class Gig extends Component {
    constructor(props) {
        super(props);
    }

    componentDidMount = () => {
        ReactDOM.findDOMNode(this).parentNode.className = "extendable";
    };

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
                            fireplaces.map((fireplace) => (
                                <Fireplace fireplace={fireplace}/>
                            ))
                        }
                    </div>
                </div>
            </div>
        </div>
    )
}