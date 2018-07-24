import React from "react"
import {Component} from "react"

import shareFile from "../../../resources/icons/picture.svg"

import "./style.sass"
import Textarea from "../../Textarea/Textarea";
import Fireplace from "../FirePlace/FirePlace";

class ImagerLoader extends Component {
    constructor(props) {
        super(props);

        this.state = {
            fireplace : {
                caption : null,
                content : null,
            },
            dragOver : null,
            title : "",
            loadingState : false
        };
    }

    dropHandler = (e) => {
        e.preventDefault();

        let files;
        if(e.target.files.length === 0)
            files = e.dataTransfer.files;
        else files = e.target.files;

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

                        this.setState({
                            fireplace : {
                                caption : this.liveImage
                            }
                        });
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
            dragOver : "gig-creator-interaction-zone-dropping"
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
        <div className="gig-creator-interaction">
            <div className={"gig-creator-interaction-zone " + this.state.dragOver} onDrop={this.dropHandler} onDragLeave={this.dragLeaveHandler} onDragOver={this.dragOverHandler}>
                <input className="gig-creator-input" type="file" onChange={this.dropHandler}/>
                <img className="gig-creator-live-input" ref={(liveImage) => this.liveImage = liveImage}/>
                <img className="gig-creator-upload" src={shareFile} />

                <p className="gig-creator-upload-text">Upload any caption Image</p>
            </div>
        </div>
    );
}


class GigCreator extends Component {
    constructor(props) {
        super(props);
    }

    onFireplaceContentChange = (value) => {
        this.setState({
            fireplace : {
                content : value
            }
        });
    };

    render = () => (
        <div className="gig-creator">
            <div className="gig-creator-container gig-creator-live">
                <Fireplace fireplace={this.state.fireplace}/>
            </div>
            <div className="gig-creator-container gig-creator-filling">
                <ImagerLoader/>
                <Textarea {...{label : "Content", placeholder : "Content..."}}
                          onParentChange={this.onFireplaceContentChange}/>
            </div>
        </div>
    )
}

export default GigCreator;