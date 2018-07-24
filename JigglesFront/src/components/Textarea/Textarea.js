import React from "react"
import {Component} from "react"

import "./style.sass"

export default class Textarea extends Component {
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

    onTextareaChange = (e) => {
        this.setState({
            value : e.target.value
        });

        this.props.onParentChange(e.target.value);
    };

    render = () => (
        <div className="form-textarea">
            <label className="form-textarea-label">{this.state.label}</label>
            <textarea className="form-textarea-action"
                      placeholder={this.props.placeholder}
                      value={this.state.value}
                      onChange={this.onTextareaChange}/>
        </div>
    );
}
