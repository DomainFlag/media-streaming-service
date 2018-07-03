import React from "react"
import {Component} from "react"

import "./style.sass"

export default class Button extends Component {
    constructor(props) {
        super(props);

        this.state = {
            value: props.value.toUpperCase(),
            selectable: props.selectable
        };
    }

    render = () => (
        <button className={"button " + this.state.selectable}>
            {this.state.value}
        </button>
    )
}