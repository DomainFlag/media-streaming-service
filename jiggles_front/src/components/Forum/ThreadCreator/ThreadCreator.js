import React from "react"
import {Component} from "react"
import {connect} from "react-redux"
import {withRouter} from "react-router"

import shareFile from "../../../resources/icons/picture.svg"
import {ACTIONS} from "../../../reducers/forum"
import Textarea from "../../Components/Textarea/Textarea";
import Thread from "../Thread/Thread";
import Button from "../../Components/Button/Button";

import "./style.sass"

class ImageLoader extends Component {
    constructor(props) {
        super(props);

        this.state = {
            caption : null,
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
                        this.setState({
                            caption : fileReader.result,
                            dragOver : "creator-interaction-zone-dropped"
                        });

                        this.props.onThreadCaptionChange(fileReader.result);
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
            <div className={"creator-interaction-zone " + this.state.dragOver}
                 onDrop={this.dropHandler}
                 onDragLeave={this.dragLeaveHandler}
                 onDragOver={this.dragOverHandler}>
                <input className="creator-input" type="file" onChange={this.dropHandler} />
                <img className="creator-live-input" src={this.state.caption} style={{ visibility: this.state.caption ? "visible" : "hidden" }}/>
                <img className="creator-upload" src={shareFile} />

                <p className="creator-upload-text">Upload any caption Image</p>
            </div>
        </div>
    );
}


class ThreadCreator extends Component {
    constructor(props) {
        super(props);

        this.state = {
            caption : null,
            content : null
        }
    }

    onPostThread = () => {
        if(this.state.caption && this.state.content)
            this.props.createThread(Object.assign({}, this.state));
    };

    onThreadCaptionChange = (value) => {
        this.setState({
            caption : value
        });
    };

    onThreadContentChange = (value) => {
        this.setState({
            content : value
        });
    };

    render = () => (
        <div className="creator">
            <div className="creator-header">
                <p className="creator-header-title">
                    Thread Creator
                </p>
            </div>
            <div className="creator-body">
                <div className="creator-container">
                    <Thread thread={this.state}/>
                </div>
                <div className="creator-container">
                    <div className="creator-filling">
                        <ImageLoader onThreadCaptionChange={this.onThreadCaptionChange}/>

                        <div className="creator-content">
                            <Textarea {...{label : "Content", placeholder : "Content..."}} onParentChange={this.onThreadContentChange}/>
                        </div>
                        
                        <div className="creator-interaction">
                            <div className="creator-interaction-post">
                                <Button value="Post" backgroundColor="#148491" onClick={this.onPostThread}/>
                            </div>
                            <div className="creator-interaction-cancel">
                                <Button value="Cancel" backgroundColor="#DB2E3B" onClick={this.props.history.goBack}/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

const mapDispatchToProps = (dispatch) => ({
    createThread : (body) => dispatch(ACTIONS.HANDLE_THREAD("POST", body))
});

export default withRouter(connect(null, mapDispatchToProps)(ThreadCreator));