import React from "react"
import {Component} from "react"

import "./style.sass"

import AudioPlayback from "../../utils/AudioPlayback";
import Speaker from "../Speaker/Speaker";
import Settings from "../Settings/Settings";

export default class Studio extends Component {
    constructor(props) {
        super(props);

        this.audioPlayback = new AudioPlayback();
    }

    render = () => (
        <div className="studio">
            <Speaker audioPlayback={this.audioPlayback}/>
            <Settings audioPlayback={this.audioPlayback}/>
        </div>
    )
}

