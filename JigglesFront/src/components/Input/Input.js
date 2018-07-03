import React from "react"
import {Component} from "react"

import "./style.sass"

export default class Input extends Component {
    constructor(props) {
        super(props);

        this.state = {
            label: props.label,
            value: props.value || props.label.toLowerCase()
        }
    }

    onInputChange = (e) => {
        this.setState((prevState) => ({
            value: prevState.value + e.target.value
        }));
    };

    render = () => (
        <div className="form-input">
            <label className="form-input-label">{this.state.label}</label>
            <input className="form-input-action" placeholder={this.state.value} onChange={this.onInputChange}/>
        </div>
    );
}
