import React from "react"
import {Component} from "react"

import "./style.sass"

import CheckboxIcon from "./../../resources/icons/checked.svg"

export default class Checkbox extends Component {
    constructor(props) {
        super(props);

        this.state = {
            checkboxToggle: null
        }
    }

    onSubmit = () => {};

    onCheckboxToggle = () => {
        this.setState((prevState) => {
            if(prevState.checkboxToggle) {
                return {
                    checkboxToggle: null
                };
            } else return {
                checkboxToggle: "checkbox-toggled"
            };
        });
    };

    render = () => (
        <div className="form-checkbox">
            <label className="form-checkbox-label">
                {this.props.label}
            </label>
            <div className={"form-checkbox-container " + this.state.checkboxToggle} onClick={this.onCheckboxToggle}>
                {
                    this.state.checkboxToggle ? (
                        <img src={CheckboxIcon} className="form-update-checkbox-done"/>
                    ) : null
                }
            </div>
        </div>
    );
}
