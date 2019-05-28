import React from "react"
import {Component} from "react"

import "./style.sass"

export default class Textarea extends Component {
    constructor(props) {
        super(props);

        this.state = {
            value: this.props.value || null
        };
    }

    componentDidMount = () => {
        document.addEventListener("keydown", this.onSubmitComment);
    };

    componentWillUnmount = () => {
        document.removeEventListener("keydown", this.onSubmitComment);
    };

    componentWillReceiveProps(nextProps) {
        if(this.props.value !== nextProps.value)
            this.setState({
                value : nextProps.value || ""
            });
    }

    onSubmitComment = (e) => {
        // Enter key
        if(e.which === 13 && this.props.onSubmitComment) {
            this.props.onSubmitComment();
            e.preventDefault();
        }
    };

    onTextareaChange = (e) => {
        this.setState({
            value : e.target.value
        });

        this.props.onParentChange(e.target.value);
    };

    render = () => (
        <div className="form-textarea">
            {
                this.props.caption && (
                    <div className="form-textarea-div" style={{ marginRight: "8px" }}>
                        <img className="form-textarea-caption" alt="caption" src={this.props.caption}/>
                    </div>
                )
            }
            <div className="form-textarea-div" style={{ marginLeft : this.props.caption && "8px" }}>
                {
                    this.props.label && (
                        <label className="form-textarea-label">{this.props.label}</label>
                    )
                }
                <textarea className="form-textarea-action"
                          placeholder={this.props.placeholder}
                          value={this.state.value}
                          onChange={this.onTextareaChange}/>
            </div>
        </div>
    );
}
