import React from "react"
import {Component} from "react"

import "./style.sass"

export default class Input extends Component {
    constructor(props) {
        super(props);

        this.state = {
            label: props.label,
            value: this.props.value || ""
        };

    }

    componentWillReceiveProps(nextProps) {
        if(nextProps.cleanState)
            this.setState({
                value : ""
            });
    }

    onInputChange = (e) => {
        this.setState({
            value:  e.target.value
        });

        this.props.onParentChange(e);
    };

    render = () => (
        <div className="form-input">
            <label className="form-input-label">{this.state.label}</label>
            <input className="form-input-action" placeholder={this.props.placeholder} value={this.state.value} type={this.props.type} onChange={this.onInputChange}/>
        </div>
    );
}
