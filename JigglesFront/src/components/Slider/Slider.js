import React from "react"
import {Component} from "react";

import "./style.sass"

const ORIENTATION_VERTICAL = "vertical";
const ORIENTATION_HORIZONTAL = "horizontal";

const SLIDER_PLACEHOLDER = 1;
const SLIDER_THUMB = 0;

const styleMaker = (type, orientation, value) => {
    if(orientation === ORIENTATION_VERTICAL) {
        if(type === SLIDER_PLACEHOLDER) return { height: (100-value) + "%" };
        else return { top: value + "%" };
    } else {
        if(type === SLIDER_PLACEHOLDER) return { width: value + "%"};
        else return { left: value + "%" };
    }
};

export const Constants = {
    ORIENTATION_VERTICAL,
    ORIENTATION_HORIZONTAL,

    SLIDER_PLACEHOLDER,
    SLIDER_THUMB
};

export default class Slider extends Component {
    constructor(props) {
        super(props);

        this.state = this.changeOnEvent(this.props.value || 0);
    }

    componentWillReceiveProps(nextProps) {
        if(nextProps.for === "equalizer")
            this.setState(this.changeOnEvent(nextProps.value || 0));
    }

    changeOnEvent = (currentValue) => {
        let percentage = (currentValue - this.props.trackMinBoundary) / (this.props.trackMaxBoundary - this.props.trackMinBoundary) * 100;
        let value = Math.round(this.props.trackMinBoundary + (this.props.trackMaxBoundary - this.props.trackMinBoundary) * percentage / 100);

        return {
            value: value,
            percentage: (this.props.orientation === ORIENTATION_VERTICAL) ? 100-percentage : percentage
        };
    };

    setAudioUpdater = () => {
        if(this.props.getState === undefined)
            return;

        this.audioTick = setInterval(() => {
            let audioTicker = this.props.getState();
            this.setState(
                this.changeOnEvent(audioTicker)
            );
        }, 1000);
    };

    componentDidMount = () => {
        if(this.props.orientation === ORIENTATION_VERTICAL) {
            this.sliderDimen = this.slider.clientHeight;
            this.sliderOffset = this.slider.offsetTop;
        } else {
            this.sliderDimen = this.slider.clientWidth;
            this.sliderOffset = this.slider.offsetLeft;
        }

        if(this.props.for === "tracker")
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
        let pageDimenOrient, percentage;

        if(this.props.orientation === ORIENTATION_VERTICAL)
            pageDimenOrient = event.pageY;
        else pageDimenOrient = event.pageX;

        if(pageDimenOrient >= this.sliderOffset && pageDimenOrient <= this.sliderDimen + this.sliderOffset) {
            percentage = (pageDimenOrient - this.sliderOffset) / this.sliderDimen * 100;
        } else if(pageDimenOrient < this.sliderOffset) {
            percentage = 0;
        } else {
            percentage = 100;
        }

        percentage = (this.props.orientation === ORIENTATION_VERTICAL) ? 100-percentage : percentage;
        let value = Math.round(this.props.trackMinBoundary + (this.props.trackMaxBoundary - this.props.trackMinBoundary) * percentage / 100);

        this.setState({
            value : value,
            percentage : (this.props.orientation === ORIENTATION_VERTICAL) ? 100-percentage : percentage
        });

        if(this.props.for === "volume") {
            this.props.changeVolume(percentage);
            this.props.changeVolumeLevel(percentage / 100);
        } else if(this.props.for === "equalizer")
            this.props.changeValue(value);
        else if(this.props.for === "equalizer")
            this.props.onChangeParams(value);

        event.stopPropagation();
        event.preventDefault();

        return value;
    };

    mouseUp = () => {
        document.removeEventListener("mousemove", this.mouseMove);
        document.removeEventListener("mouseup", this.mouseUp);
        this.thumb.addEventListener("mousedown", this.mouseDown);

        if(this.props.for === "tracker") {
            this.props.jumpTrack(this.state.value);
            this.setAudioUpdater();
        }
    };

    mousePlaceholderDown = (e) => {
        if(this.props.for === "tracker") {
            clearInterval(this.audioTick);

            this.props.jumpTrack(this.mouseMove(e));
        }
    };

    mousePlaceholderUp = (e) => {
        if(this.props.for === "tracker") {
            this.setAudioUpdater();
        }
    };

    getCurrentTrackTime = () => {
        let seconds = this.state.value % 60;
        let minutes = (this.state.value - seconds) / 60;

        return minutes + ":" +  ("" + seconds).padStart(2, '0');
    };

    render = () => (
        <div className={"slider slider-" + this.props.orientation}>
            {
                this.props.for === "tracker" ? (
                    <span className="slider-value">
                        {this.getCurrentTrackTime()}
                    </span>
                ) : (this.props.for === "equalizer") ? (
                    <span className="slider-value">
                        {this.state.value}
                    </span>
                ) : null
            }
            <div className={"slider-container slider-container-" + this.props.orientation}
                 ref={(slider) => this.slider = slider} onMouseDown={this.mousePlaceholderDown} onMouseUp={this.mousePlaceholderUp}>

                <div className={"slider-placeholder slider-placeholder-" + this.props.orientation}
                     style={styleMaker(SLIDER_PLACEHOLDER, this.props.orientation, this.state.percentage)}/>

                <div className={"thumb thumb-" + this.props.orientation}
                     style={styleMaker(SLIDER_THUMB, this.props.orientation, this.state.percentage)}
                     ref={(thumb) => this.thumb = thumb} onMouseDown={this.mouseDown}/>

            </div>
            {
                this.props.label && (
                    <div className="slider-label">
                        <p className="slider-label-value">
                            {this.props.label}
                        </p>
                    </div>
                )
            }
        </div>
    )
}