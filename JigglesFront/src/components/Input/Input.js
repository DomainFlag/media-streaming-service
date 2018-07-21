import React from "react"
import {Component} from "react"

import "./style.sass"

export default class Input extends Component {
    constructor(props) {
        super(props);

        this.state = {
            label: props.label,
            value: null
        }
    }

    onInputChange = (e) => {
        this.setState({
            value:  e.target.value
        });
    };

    render = () => (
        <div className="form-input">
            <label className="form-input-label">{this.state.label}</label>
            <input className="form-input-action" placeholder={this.props.placeholder} onChange={this.onInputChange}/>
        </div>
    );
}
