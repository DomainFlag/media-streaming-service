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
        this.isThere = (nextProps.validation !== null && nextProps.validation !== undefined);
        this.warningContent = this.isThere && nextProps.validation ? ({
            color : "#29A369",
            text : "Matching"
        }) : ({
            color : "#DB2E3B",
            text : "Not Matching"
        });

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
            <div className="form-input-validation">
                {
                    this.isThere && (
                        <div className="form-input-info">
                            <p className="form-input-warning" style={{ color: this.warningContent.color }}>
                                {
                                    this.warningContent.text
                                }
                            </p>
                        </div>
                    )
                }
                <input className="form-input-action" placeholder={this.props.placeholder} value={this.state.value} type={this.props.type} onChange={this.onInputChange}/>
            </div>
        </div>
    );
}
