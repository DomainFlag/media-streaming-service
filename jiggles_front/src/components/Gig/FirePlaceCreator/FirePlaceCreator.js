import React from "react"
import {Component} from "react"

import shareFile from "../../../resources/icons/picture.svg"

import "./style.sass"
import sass from "../../../resources.sass"
import Textarea from "../../Components/Textarea/Textarea";
import Fireplace from "../FirePlace/FirePlace";
import Button from "../../Components/Button/Button";

class ImageLoader extends Component {
    constructor(props) {
        super(props);

        this.state = {
            dragOver : null,
            title : "",
            loadingState : false
        };
    }

    dropHandler = (e) => {
        e.preventDefault();

        let files;
        if(e.target.hasOwnProperty("files") && e.target.files.length !== 0)
            files = e.target.files;
        else files = e.dataTransfer.files;

        if(files.length > 0) {
            let fileReader = new FileReader();

            if(/image\/.*/.test(files[0].type)) {
                this.setState((prevState) => ({
                    loadingState : !prevState.loadingState,
                    title : files[0].name.replace(/\..+$/, "")
                }));

                fileReader.readAsDataURL(files[0]);

                fileReader.addEventListener("load", () => {
                    if(fileReader.readyState === fileReader.DONE) {
                        this.liveImage.src = fileReader.result;

                        this.props.onFireplaceCaptionChange(fileReader.result);
                    } else {
                        this.setState({
                            loadingState : false
                        })
                    }
                });
            }
        }
    };

    dragOverHandler = (e) => {
        e.preventDefault();

        this.setState({
            dragOver : "creator-interaction-zone-dropping"
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
        <div className="creator-interaction">
            <div className={"creator-interaction-zone " + this.state.dragOver} onDrop={this.dropHandler} onDragLeave={this.dragLeaveHandler} onDragOver={this.dragOverHandler}>
                <input className="creator-input" type="file" onChange={this.dropHandler}/>
                <img className="creator-live-input" ref={(liveImage) => this.liveImage = liveImage}/>
                <img className="creator-upload" src={shareFile} />

                <p className="creator-upload-text">Upload any caption Image</p>
            </div>
        </div>
    );
}


class FirePlaceCreator extends Component {
    constructor(props) {
        super(props);

        this.state = {
            fireplace : {
                caption : null,
                content : null
            }
        }
    }

    onFireplaceCaptionChange = (value) => {
        this.setState({
            fireplace : {
                ...this.state.fireplace,
                caption : value
            }
        });
    };

    onFireplaceContentChange = (value) => {
        this.setState({
            fireplace : {
                ...this.state.fireplace,
                content : value
            }
        });
    };

    render = () => (
        <div className="creator">
            <div className="creator-header">
                <p className="creator-header-title">
                    Fireplace Creator
                </p>
            </div>
            <div className="creator-body">
                <div className="creator-container">
                    <Fireplace fireplace={this.state.fireplace}/>
                </div>
                <div className="creator-container">
                    <div className="creator-filling">
                        <ImageLoader onFireplaceCaptionChange={this.onFireplaceCaptionChange}/>
                        <div className="creator-content">
                            <Textarea {...{label : "Content", placeholder : "Content..."}} onParentChange={this.onFireplaceContentChange}/>
                        </div>
                        <div className="creator-interaction">
                            <div className="creator-interaction-post">
                                <Button value="Post" backgroundColor="#148491"/>
                            </div>
                            <div className="creator-interaction-cancel">
                                <Button value="Cancel" backgroundColor="#DB2E3B"/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default FirePlaceCreator;