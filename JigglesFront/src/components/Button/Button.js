import React from "react"
import {Component} from "react"

import "./style.sass"
import ACTIONS from "../../reducers/main";

export default class Button extends Component {
    constructor(props) {
        super(props);

        this.state = {
            value: props.value.toUpperCase(),
            selectable: props.selectable
        };
    }

    render = () => (
        <button className={"button " + this.state.selectable} onClick={this.props.onClick}>
            {this.state.value}
        </button>
    )
}