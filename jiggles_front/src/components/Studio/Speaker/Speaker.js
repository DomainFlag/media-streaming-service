import React from "react"
import {Component} from "react"

import "./style.sass"

import Slider from "./../../Components/Slider/Slider"
import { Constants } from "./../../Components/Slider/Slider";

import shareFile from "./../../../resources/icons/share-file.svg"
import spinner from "../../../resources/icons/spinner-circle.svg"

let req = require.context("../../../resources/icons/media/", false, /.*\.svg$/);
let playbackIcons = {};
req.keys().forEach(function(key){
    playbackIcons[key.slice(2, key.length-4)] = req(key);
});

let volumes = [];
req = require.context("../../../resources/icons/media/volume", false, /.*\.svg$/);
req.keys().forEach(function(key){
    volumes.push(req(key));
});

class MusicLoader extends Component {
    constructor(props) {
        super(props);

        this.state = {
            dragOver : null
        }
    }

    dropHandler = (e) => {
        e.preventDefault();

        let files = e.dataTransfer.files;

        if(files.length > 0) {
            let fileReader = new FileReader();

            if(files[0].type === "audio/mp3") {
                this.props.changeLoadingState(true);

                this.props.setTitle(files[0].name.replace(/\..+$/, ""));

                fileReader.readAsArrayBuffer(files[0]);
                fileReader.addEventListener("progress", (e) => {
                    // Do something latter
                });

                fileReader.addEventListener("loadend", () => {
                    if(fileReader.readyState === fileReader.DONE) {
                        this.props.audioPlayback.initialize(this.props.toggleInteraction, fileReader.result, (resolution) => {
                            this.props.changeLoadingState(!resolution);
                        });
                    } else {
                        this.props.changeLoadingState(false);
                    }
                });
            }
        }
    };

    dragOverHandler = (e) => {
        e.preventDefault();

        this.setState({
           dragOver : "speaker-interaction-zone-dropping"
        });

        // Set the dropEffect to move
        e.dataTransfer.dropEffect = "move"
    };

    dragLeaveHandler = (e) => {
        e.preventDefault();

        this.setState({
            dragOver : null
        });
    };

    render = () => (
        <div className="speaker-interaction">
            <div className={"speaker-interaction-zone " + this.state.dragOver} onDrop={this.dropHandler} onDragLeave={this.dragLeaveHandler} onDragOver={this.dragOverHandler}>
                <img className="speaker-upload" src={shareFile} />

                <p className="speaker-upload-text">Upload any Mp3 file</p>
            </div>
        </div>
    );
}

class MusicPlayer extends Component {
    constructor(props) {
        super(props);

        this.volume = {
            MIN_BOUNDARY : 0,
            MAX_BOUNDARY : 101
        };

        this.state = {
            volume: 100,
            state: "pause-button"
        };
    }

    componentDidMount = () => {
        this.props.audioPlayback.setVolumeLevel(this.state.volume / 100);
        this.ctx = this.canvas.getContext("2d");

        this.props.audioPlayback.visualizer.initialize(this.canvas, this.ctx);
        requestAnimationFrame(this.props.audioPlayback.visualizer.visualize);
    };

    onToggleState = () => {
        this.setState((prevState) => {
            let state = {};
            if(prevState.state === "pause-button") {
                this.props.audioPlayback.pauseTrack();

                state["state"] = "play-button";
            } else {
                this.props.audioPlayback.resumeTrack();

                state["state"] = "pause-button";
            }

            return state;
        });
    };

    changeVolume = (volume) => {
        this.setState(({
            volume
        }));
    };

    getVolumeDifference = () => this.volume.MAX_BOUNDARY - this.volume.MIN_BOUNDARY;

    getVolumeIcon = () => {
        if(this.state.volume === 0)
            return 0;

        let volumeIconRange = this.state.volume / this.getVolumeDifference();
        return Math.floor(volumeIconRange * (volumes.length - 1)) + 1;
    };

    render = () => (
        <div className="music-player">

            <div className="music-player-header">
                <div className="music-player-volume">
                    <img className="music-player-icon" src={volumes[this.getVolumeIcon()]} />
                    <Slider for="volume"
                            orientation={Constants.ORIENTATION_HORIZONTAL}
                            value={this.state.volume}
                            trackMinBoundary={0}
                            trackMaxBoundary={101}
                            changeVolumeLevel={this.props.audioPlayback.setVolumeLevel}
                            changeVolume={this.changeVolume}/>
                </div>
            </div>

            <div className="music-player-body">
                <p className="music-player-title">
                    { this.props.title }
                </p>
            </div>

            <div className="music-player-stuff">
                <div className="music-player-stuff-container">
                    <div className="music-player-container">
                        <img className="music-thumbnail" src="https://soundblabcontent.s3.amazonaws.com/2292a6e11e75e53d368e2de938308800d5f3fe5e.jpg?1452043904.jpg"/>
                        <canvas className="music-visualizer" width={200} height={125} ref={(canvas) => this.canvas = canvas}/>
                    </div>

                    <div className="music-player-playback">
                        <div className="music-player-playback-interaction">
                            <Slider for="tracker"
                                    orientation={Constants.ORIENTATION_HORIZONTAL}
                                    trackMinBoundary={0}
                                    trackMaxBoundary={this.props.audioPlayback.getTotalTime()}
                                    getState={this.props.audioPlayback.getAudioTick}
                                    jumpTrack={this.props.audioPlayback.jumpTrack}/>
                        </div>
                        <div className="music-player-playback-interaction">
                            <img className="music-player-icon" src={playbackIcons["shuffle-mode"]} />

                            <img className="music-player-icon" src={playbackIcons["play-previous-button"]} />
                            <img className="music-player-icon" src={playbackIcons[this.state.state]} onClick={this.onToggleState}/>
                            <img className="music-player-icon" src={playbackIcons["play-next-button"]} />

                            <img className="music-player-icon" src={playbackIcons["refresh-button"]} />
                        </div>
                    </div>
                </div>
            </div>

        </div>
    )
}

export default class Speaker extends Component {
    constructor(props) {
        super(props);

        this.state = {
            interaction : false,
            spinner : false,
            title : null
        }
    }

    changeLoadingState = (state) => {
        this.setState(({
            spinner : state
        }))
    };

    toggleInteraction = () => {
        this.setState((prevState) => ({
            interaction : !prevState.interaction
        }));
    };

    setTitle = (title) => {
        this.setState({
            title
        })
    };

    render = () => (
        <div className="speaker">
            {
                (!this.state.interaction && !this.state.spinner) ? (
                    <div className="speaker-container">
                        <MusicLoader ref={(MusicLoader => this.MusicLoader = MusicLoader )}
                                     toggleInteraction={this.toggleInteraction}
                                     audioPlayback={this.props.audioPlayback}
                                     changeLoadingState={this.changeLoadingState}
                                     setTitle={this.setTitle}/>
                    </div>
                ) : (this.state.spinner) ? (
                    <img className="speaker-spinner" src={spinner}/>
                ) : (this.state.interaction) && (
                    <div className="speaker-container">
                        <MusicPlayer title={this.state.title}
                                     toggleInteraction={this.toggleInteraction}
                                     audioPlayback={this.props.audioPlayback}/>
                    </div>
                )
            }
        </div>
    );
}

