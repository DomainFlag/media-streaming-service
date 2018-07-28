import React from "react"
import {Component} from "react"

import lens from "../../../resources/icons/lens.svg"

import "./style.sass"

export default class Button extends Component {
    constructor(props) {
        super(props);

        this.state = {
            value: !props.capsLocked ? (props.value || "Click") : (props.value || "Click").toUpperCase(),
            selectable: props.selectable
        };
    }

    componentWillReceiveProps(nextProps) {
        if(nextProps.value !== this.state.value)
            this.setState({
                value : nextProps.value
            });
    }

    render = () => {
        return this.props.iconBased ? (
            <div className={"button-icon " + this.state.selectable}
                    onClick={this.props.onClick}>
                <img className="button-icon-content" src={lens}/>
                <p className="button-icon-text">{this.state.value}</p>
            </div>
        ) : (
            <button className={"button " + this.state.selectable}
                    style={{backgroundColor : this.props.backgroundColor}}
                    onClick={this.props.onClick}>
                {this.state.value}
            </button>
        )
    }
}