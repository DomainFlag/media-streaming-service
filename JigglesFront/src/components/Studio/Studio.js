import React from "react"
import {Component} from "react"

import "./style.sass"

import AudioPlayback from "../../utils/AudioPlayback";
import Speaker from "../Speaker/Speaker";
import Settings from "../Settings/Settings";

import settingsMenu from "../../resources/icons/chat-menu.svg"

export default class Studio extends Component {
    constructor(props) {
        super(props);

        this.audioPlayback = new AudioPlayback();

        this.state = {
            settingsToggle : false
        }
    }

    onToggleSettings = () => {
        this.setState((prevState) => ({
            settingsToggle : !prevState.settingsToggle
        }))
    };

    render = () => (
        <div className="studio">
            <Speaker audioPlayback={this.audioPlayback}/>
            {
                this.state.settingsToggle ? (
                    <Settings audioPlayback={this.audioPlayback} onToggleSettings={this.onToggleSettings}/>
                ) : (
                    <img className="studio-settings-toggle-button" src={settingsMenu} onClick={this.onToggleSettings}/>
                )
            }
        </div>
    )
}

