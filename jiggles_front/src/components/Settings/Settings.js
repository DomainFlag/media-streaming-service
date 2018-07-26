import React from "react"
import {Component} from "react"

import "./style.sass"

import Equalizer from "../Studio/Toys/Equalizer/Equalizer";

import backArrow from "../../resources/icons/back-arrow.svg"

export default class Settings extends Component {
    constructor(props) {
        super(props);

        this.components = [
            {
                type : "Equalizer",
                component : <Equalizer setup={this.props.audioPlayback.setup}/>
            }, {
                type : "Matcher",
                component : null
            }, {
                type : "Beater",
                component : null
            }, {
                type : "Tweaking",
                component : null
            }
        ];

        this.state = {
            activeState : 0
        }
    }

    onToggleComponent = (index) => {
        this.setState(({
            activeState : index
        }))
    };

    render = () => (
        <div className="settings">
            <div className="settings-container">

                <div className="settings-header">

                    <div className="settings-header-container">
                        <img className="settings-back-icon" src={backArrow} onClick={this.props.onToggleSettings}/>

                        <p className="settings-header-title">
                            Settings
                        </p>
                    </div>

                    <div className="settings-header-container">
                    </div>

                </div>

                <div className="settings-body">
                    <div className="settings-utils">
                        {
                            this.components.map((settingsComponent, index) => (
                                <p className={"settings-util " + ((this.state.activeState === index) && "util-active") }
                                   onClick={this.onToggleComponent.bind(this, index)} key={index}>
                                    { settingsComponent.type }
                                </p>
                            ))
                        }
                    </div>

                    <div className="settings-divider"/>

                    <div className="settings-interaction">
                        { this.components[this.state.activeState].component }
                    </div>
                </div>

            </div>
        </div>
    )
}