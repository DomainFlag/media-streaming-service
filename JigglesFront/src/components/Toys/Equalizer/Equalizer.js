import React from "react"
import {Component} from "react"

import "./style.sass"
import Slider from "../../Slider/Slider";
import { Constants } from "../../Slider/Slider"

export default class Settings extends Component {
    constructor(props) {
        super(props);
    }

    render = () => (
        <div className="equalizer">

            <div className="equalizer-types">

                <p className="equalizer-type">Rock</p>
                <p className="equalizer-type equalizer-type-active">Indie</p>
                <p className="equalizer-type">Pop</p>
                <p className="equalizer-type">Classic</p>

            </div>

            <div className="equalizer-tweaking">
                {
                    Object.keys(this.props.setup.equalizer)
                        .map((key) => (
                            <div className="equalizer-tweaker">
                                <Slider
                                    for="equalizer"
                                    label={ key }
                                    orientation={ Constants.ORIENTATION_VERTICAL }
                                    value={ this.props.setup.equalizer[key] }
                                    trackMinBoundary={-8}
                                    trackMaxBoundary={8}
                                    changeValue={this.props.setup.onChangeValue(key)}/>
                            </div>
                        ))
                }
            </div>

        </div>
    )
}