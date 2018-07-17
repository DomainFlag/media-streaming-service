import React from "react"
import {Component} from "react";

import "./style.sass"

export default class Slider extends Component {
    constructor(props) {
        super(props);

        this.state = Object.assign({}, this.changeOnEvent(this.props.sliderValue || 0));
    }


    changeOnEvent = (currentValue) => {
        let valuePercentage = currentValue / (this.props.trackMaxBoundary - this.props.trackMinBoundary) * 100;
        let value = Math.round(this.props.trackMinBoundary + (this.props.trackMaxBoundary - this.props.trackMinBoundary) * valuePercentage / 100);
        
        return {
            sliderValue: value,
            sliderPercentage: valuePercentage + "%"
        };
    };

    setAudioUpdater = () => {
        if(this.props.getState === undefined)
            return;

        let audioTicker = this.props.getState();

        this.audioTick = setInterval(() => {
            this.setState(
                    this.changeOnEvent(audioTicker())
                );
        }, 1000);
    };

    componentDidMount = () => {
        this.sliderDimen = this.slider.clientWidth;
        this.sliderOffset = this.slider.offsetLeft;

        this.setAudioUpdater();
    };

    componentWillUnmount = () => {
        document.removeEventListener("mousemove", this.mouseMove);
        document.removeEventListener("mouseup", this.mouseUp);

        if(this.props.for === "tracker") {
            clearInterval(this.audioTick);
        }
    };

    mouseDown = (event) => {
        event.target.removeEventListener("mousedown", this.mouseDown);
        document.addEventListener("mousemove", this.mouseMove);
        document.addEventListener("mouseup", this.mouseUp);
        event.stopPropagation();
        event.preventDefault();

        if(this.props.for === "tracker")
            clearInterval(this.audioTick);
    };

    mouseMove = (event) => {
        let pageDimenOrient = event.pageX, valuePercentage;

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
            sliderPercentage : valuePercentage + "%"
        });

        if(this.props.for !== "tracker") {
            this.props.changeVolume(valuePercentage);
            this.props.changeVolumeLevel(valuePercentage / 100);
        }

        event.stopPropagation();
        event.preventDefault();

        return value;
    };

    mouseUp = () => {
        document.removeEventListener("mousemove", this.mouseMove);
        document.removeEventListener("mouseup", this.mouseUp);
        this.thumb.addEventListener("mousedown", this.mouseDown);

        if(this.props.for === "tracker") {
            this.props.jumpTrack(this.state.sliderValue);
            this.setAudioUpdater();
        }
    };

    mousePlaceholderDown = (e) => {
        if(this.props.for === "tracker")
            clearInterval(this.audioTick);

        if(this.props.for === "tracker") {
            this.props.jumpTrack(this.mouseMove(e));
            this.setAudioUpdater();
        }
    };

    getCurrentTrackTime = () => {
        let seconds = this.state.sliderValue % 60;
        let minutes = (this.state.sliderValue - seconds) / 60;

        return minutes + ":" +  ("" + seconds).padStart(2, '0');
    };

    render = () => (
        <div className="slider">
            {
                this.props.for === "tracker" && (
                    <span className="slider-value">
                        { this.getCurrentTrackTime() }
                    </span>
                )
            }
            <div className="slider-container" ref={(slider) => this.slider = slider} onClick={this.mousePlaceholderDown}>
                <div className="slider-placeholder" style={{ width : this.state.sliderPercentage}} />
                <div className="thumb" style={{ left: this.state.sliderPercentage}} ref={(thumb) => this.thumb = thumb} onMouseDown={this.mouseDown}/>
            </div>
        </div>
    )
}