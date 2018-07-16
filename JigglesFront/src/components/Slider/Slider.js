import React from "react"
import {Component} from "react";

import "./style.sass"

export default class Slider extends Component {
    constructor(props) {
        super(props);

        this.styleOrientation = {
            "position" : {
                "vertical" : "top",
                "horizontal" : "left"
            },
            "length" : {
                "vertical" : "height",
                "horizontal" : "width"
            }
        };

        this.state = {
            sliderValue : 0,
            sliderPlaceholder : null,
            thumbValue : null
        }
    }

    componentDidMount = () => {
        if(this.props.orientation === "horizontal") {
            this.sliderDimen = this.slider.clientWidth;
            this.sliderOffset = this.slider.offsetLeft;
        } else {
            this.sliderDimen = this.slider.clientHeight;
            this.sliderOffset = this.slider.offsetTop;
        }
    };

    componentWillUnmount = () => {
        document.removeEventListener("mousemove", this.mouseMove);
        document.removeEventListener("mouseup", this.mouseUp);
    };

    mouseDown = (event) => {
        event.target.removeEventListener("mousedown", this.mouseDown);
        document.addEventListener("mousemove", this.mouseMove);
        document.addEventListener("mouseup", this.mouseUp);
        event.stopPropagation();
        event.preventDefault();
    };

    mouseMove = (event) => {
        let pageDimenOrient, valuePercentage;
        if(this.props.orientation === "horizontal")
            pageDimenOrient = event.pageX;
        else pageDimenOrient = event.pageY;

        if(pageDimenOrient >= this.sliderOffset && pageDimenOrient <= this.sliderDimen + this.sliderOffset) {
            valuePercentage = (pageDimenOrient - this.sliderOffset) / this.sliderDimen * 100;
        } else if(pageDimenOrient < this.sliderOffset) {
            valuePercentage = 0;
        } else {
            valuePercentage = 100;
        }

        let value = Math.round(this.props.trackMinBoundary + (this.props.trackMaxBoundary - this.props.trackMinBoundary) * valuePercentage / 100);

        this.setState({
            sliderValue : value,
            sliderPlaceholder : this.props.orientation === "horizontal" ? this.thumb.style.left : this.thumb.style.top,
            thumbValue: this.props.orientation === "horizontal" ? valuePercentage + "%" : valuePercentage + "%"
        });

        event.stopPropagation();
        event.preventDefault();
    };

    mouseUp = () => {
        document.removeEventListener("mousemove", this.mouseMove);
        this.thumb.addEventListener("mousedown", this.mouseDown);
    };


    getCurrentTrackTime = () => {
        let seconds = this.state.sliderValue % 60;
        let minutes = (this.state.sliderValue - seconds) / 60;

        return minutes + ":" +  ("" + seconds).padStart(2, '0');
    };

    orientationStyle = (type, value) => {
        let style = {};
        style[this.styleOrientation[type][this.props.orientation]] = value;

        return style;
    };

    render = () => (
        <div className={"slider slider-" + this.props.orientation}>
            {
                this.orientation === "horizontal" && (
                    <span className="slider-value">
                        { this.getCurrentTrackTime() }
                    </span>
                )
            }
            <div className={"slider-container-" + this.props.orientation + " slider-container"} ref={(slider) => this.slider = slider}>
                <div className={"slider-placeholder slider-placeholder-" + this.props.orientation} style={this.orientationStyle("length", this.state.sliderPlaceholder)} />
                <div className={"thumb thumb-" + this.props.orientation} style={this.orientationStyle("position", this.state.thumbValue)} ref={(thumb) => this.thumb = thumb} onMouseDown={this.mouseDown}/>
            </div>
        </div>
    )
}