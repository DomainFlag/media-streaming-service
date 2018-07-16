import React from "react"
import {Component} from "react"

import "./style.sass"

import Slider from "./../Slider/Slider"

import shareFile from "./../../resources/icons/share-file.svg"
import audioPlayback from "../../utils/audio_playback";

let req = require.context("../../resources/icons/media/", false, /.*\.svg$/);
let playbackIcons = {};
req.keys().forEach(function(key){
    playbackIcons[key.slice(2, key.length-4)] = req(key);
});

let volumes = [];
req = require.context("../../resources/icons/media/volume", false, /.*\.svg$/);
req.keys().forEach(function(key){
    volumes.push(req(key));
});

class MusicPlayer extends Component {
    constructor(props) {
        super(props);

        this.volume = {
            MIN_BOUNDARY : 0,
            MAX_BOUNDARY : 101
        };

        this.state = {
            volume: 51
        };
    }

    getVolumeDifference = () => this.volume.MAX_BOUNDARY - this.volume.MIN_BOUNDARY;

    getVolumeIcon = () => {
        if(this.state.volume === 0)
            return 0;


        let volumeIconRange = this.state.volume / this.getVolumeDifference();

        return Math.floor(volumeIconRange * (volumes.length - 1)) + 1;
    };

    toggleVolumeSlider = () => {

    };

    render = () => (
        <div className="music-player">
            <div className="music-player-container">
                <img className="music-thumbnail" src="https://soundblabcontent.s3.amazonaws.com/2292a6e11e75e53d368e2de938308800d5f3fe5e.jpg?1452043904.jpg"/>
            </div>

            <div className="music-player-playback">
                <div className="music-player-playback-interaction">
                    <Slider orientation="horizontal" trackMinBoundary={0} trackMaxBoundary={244}/>
                </div>
                <div className="music-player-playback-interaction">
                    <img className="music-player-icon" src={playbackIcons["shuffle-mode"]} />

                    <img className="music-player-icon" src={playbackIcons["play-previous-button"]} />
                    <img className="music-player-icon" src={playbackIcons["play-rounded-button"]} />
                    <img className="music-player-icon" src={playbackIcons["play-next-button"]} />

                    <div className="music-player-volume">
                        <Slider orientation="vertical" trackMinBoundary={0} trackMaxBoundary={100}/>
                        <img className="music-player-icon" src={volumes[this.getVolumeIcon()]} onClick={this.toggleVolumeSlider} />
                    </div>
                </div>
            </div>
        </div>
    )
}

export default class Speaker extends Component {
    constructor(props) {
        super(props);
    }

    noisePlayback = (result) => {
        audioPlayback(result);
    };

    dropHandler = (e) => {
        e.preventDefault();

        let files = e.dataTransfer.files;
        if(files.length > 0) {
            let fileReader = new FileReader();
            if(files[0].type === "audio/mp3") {
                fileReader.readAsArrayBuffer(files[0]);
                fileReader.addEventListener("loadend", () => {
                    if(fileReader.readyState === fileReader.DONE) {
                        this.noisePlayback(fileReader.result);
                    }
                });
            }
        }
    };

    dragOverHandler = (e) => {
        e.preventDefault();
        // Set the dropEffect to move
        e.dataTransfer.dropEffect = "move"
    };

    render = () => (
        <div className="speaker">
            {/*<div className="speaker-container" onDrop={this.dropHandler} onDragOver={this.dragOverHandler}>*/}

                {/*<img className="speaker-upload" src={shareFile} />*/}

                {/*/!*<Button value="Play" onClick={this.noisePlayback}/>*!/*/}
            {/*</div>*/}
            <div className="speaker-container">
                <MusicPlayer/>
            </div>
        </div>
    )
}

