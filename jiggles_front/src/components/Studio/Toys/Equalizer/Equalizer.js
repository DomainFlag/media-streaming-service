import React from "react"
import {Component} from "react"

import "./style.sass"
import Slider from "../../../Components/Slider/Slider";
import { Constants } from "../../../Components/Slider/Slider"

export default class Settings extends Component {
    constructor(props) {
        super(props);

        this.components = [
            {
                type : "Rock",
                values : { 32 : 0, 64 : 1, 128 : 2, 256 : 3, 512 : 0, 1024 : 0, 2048 : 3, 4096 : 3, 8192 : 2, 16384 : 3}
            }, {
                type : "Indie",
                values : { 32 : 1, 64 : 2, 128 : 3, 256 : 2, 512 : 1, 1024 : 0, 2048 : 1, 4096 : 2, 8192 : 1, 16384 : 3}
            }, {
                type : "Pop",
                values : { 32 : 0, 64 : 2, 128 : 3, 256 : 1, 512 : 0, 1024 : 0, 2048 : 0, 4096 : 1, 8192 : 4, 16384 : 5}
            }, {
                type : "Classic",
                values : { 32 : 0, 64 : 4, 128 : 4, 256 : 2, 512 : 0, 1024 : 0, 2048 : 0, 4096 : 0, 8192 : 1, 16384 : 2}
            }
        ];

        this.state = {
            activeComponent : 0,
            values : this.components[0].values,
            customValues : { 32 : 0, 64 : 0, 128 : 0, 256 : 0, 512 : 0, 1024 : 0, 2048 : 0, 4096 : 0, 8192 : 0, 16384 : 0}
        };

        this.props.setup.onChangeValues(this.components[this.state.activeComponent].values);
    }

    onToggleEqualizer = (index) => {
        this.setState({
            activeComponent : index,
            values : this.components[index].values
        }, () => {
            this.props.setup.onChangeValues(this.components[this.state.activeComponent].values);
        })
    };

    onChangeParams = (key) => (value) => {
        this.setState((prevState) => {
            let obj = {};
            obj[key] = value;

            let values =  Object.assign({}, {...prevState.values}, obj);

            return {
                activeComponent : -1,
                values,
                customValues : values,
            }
        });
    };

    render = () => (
        <div className="equalizer">

            <div className="equalizer-types">
                {
                    this.components.map((component, index) => (
                        <p className={"equalizer-type " + ((this.state.activeComponent === index) && "equalizer-type-active")}
                           onClick={this.onToggleEqualizer.bind(this, index)} key={index}>
                            { component.type }
                        </p>
                    ))
                }
            </div>

            <div className="equalizer-tweaking">
                {
                    Object.keys(this.state.values)
                        .map((key) => (
                            <div className="equalizer-tweaker">
                                <Slider
                                    for="equalizer"
                                    label={key}
                                    orientation={Constants.ORIENTATION_VERTICAL}
                                    value={this.state.values[key]}
                                    trackMinBoundary={-8}
                                    trackMaxBoundary={8}
                                    changeValue={this.props.setup.onChangeValue(key)}
                                    onChangeParams={this.onChangeParams(key)}/>
                            </div>
                        ))
                }
            </div>

        </div>
    )
}