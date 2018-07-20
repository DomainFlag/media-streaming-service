import React from "react"
import {Component} from "react"

import "./style.sass"

import Equalizer from "./../Toys/Equalizer/Equalizer";

import backArrow from "../../resources/icons/back-arrow.svg"

export default class Settings extends Component {
    constructor(props) {
        super(props);
    }

    render = () => (
        <div className="settings">
            <div className="settings-container">

                <div className="settings-header">

                    <div className="settings-header-container">
                        <img className="settings-back-icon" src={backArrow}/>

                        <p className="settings-header-title">
                            Settings
                        </p>
                    </div>

                    <div className="settings-header-container">
                    </div>

                </div>

                <div className="settings-body">
                    <div className="settings-utils">
                        <p className="settings-util util-active">Equalizer</p>
                        <p className="settings-util">Matcher</p>
                        <p className="settings-util">Beater</p>
                        <p className="settings-util">Tweaking</p>
                    </div>

                    <div className="settings-divider"/>

                    <div className="settings-interaction">
                        <Equalizer setup={this.props.audioPlayback.setup}/>
                    </div>
                </div>

            </div>
        </div>
    )
}