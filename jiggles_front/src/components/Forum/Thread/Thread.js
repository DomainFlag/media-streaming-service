import React from "react"
import {Component} from "react"
import {connect} from "react-redux"
import {withRouter} from "react-router-dom"
import CONSTANTS from "./../../../utils/Constants";
import {ACTIONS} from "./../../../reducers/forum";


import constrantizer from "../../../utils/TextImageContrastizer"
import queryTree from "../../../utils/CommentParser"
import like from "./../../../resources/icons/like.svg"
import like_blue from "./../../../resources/icons/like-blue.svg"
import like_dark from "./../../../resources/icons/like-dark.svg"
import menu_white from "./../../../resources/icons/thread-menu-white.svg"
import menu_dark from "./../../../resources/icons/thread-menu-dark.svg"
import delete_white from "./../../../resources/icons/rubbish-bin.svg"
import delete_dark from "./../../../resources/icons/rubbish-bin-black.svg"
import edit_white from "./../../../resources/icons/create.svg"
import edit_dark from "./../../../resources/icons/create-black.svg"

import "./style.sass"
import UriBuilder from "../../../utils/UriBuilder";

class Spark extends Component {
    constructor(props) {
        super(props);

        this.caption = new UriBuilder()
            .setScheme(CONSTANTS.SCHEME)
            .setAuthority(CONSTANTS.AUTHORITY)
            .appendPath(props.comment.content.caption)
            .build();
    }

    dateParser = function (objectId) {
        return (new Date(parseInt(objectId.substring(0, 8), 16) * 1000)).toDateString();
    };

    render = () => (
        <div className="spark">
            <div className="spark-section">
                <div className="spark-container">
                    <img className="spark-avatar" src={this.caption}/>

                    <div className="spark-miscellaneous-container">
                        <p className="spark-author">{this.props.comment.name}</p>

                        <p className="spark-date">{ this.dateParser(this.props.comment._id) }</p>
                    </div>
                </div>
                <div className="spark-container">
                    <div className="spark-content">
                        {this.props.comment.content.text}
                    </div>
                </div>
                <div className="spark-container">
                    <div className="spark-like-container">
                        <img src={like_blue} className="spark-like"/>
                        <p className="spark-votes">{this.props.comment.likes}</p>
                    </div>
                </div>
            </div>
            <div className="spark-inception">
                {
                    this.props.children.map((comment) => (
                        <Spark key={comment.id} comment={comment}/>
                    ))
                }
            </div>
        </div>
    )
}

class Thread extends Component {
    constructor(props) {
        super(props);

        let depth = 1;

        this.state = {
            threadMenuToggle : false,
            caption : this.buildCaptionUri(props),
            depth : depth,
            utilityColor : "#000000",
            menuColor : "#000000",
            comments : this.saveTree(depth)
        };
    }

    buildCaptionUri = (props) => {
        if(props.type === CONSTANTS.THREAD_CREATOR)
            return props.thread.caption;
        else if(props.type === CONSTANTS.THREAD_VIEW) {
            if(props.thread.caption) {
                return new UriBuilder()
                    .setScheme(CONSTANTS.SCHEME)
                    .setAuthority(CONSTANTS.AUTHORITY)
                    .appendPath(props.thread.caption)
                    .build();
            } else return null;
        } else return null;
    };

    componentWillReceiveProps = (nextProps) => {
        if(this.props.thread.caption !== nextProps.thread.caption) {
            this.setState({
                caption : this.buildCaptionUri(nextProps)
            });
        }
    };

    componentWillUnmount = () => {
        document.removeEventListener("click", this.onDocumentThreadMenuToggle);
    };

    dateParser = function (objectId) {
        if(!objectId)
            return (new Date()).toDateString();

        return (new Date(parseInt(objectId.substring(0, 8), 16) * 1000)).toDateString();
    };

    saveTree = (depth) => {
        if(!this.props.thread.hasOwnProperty("comments"))
            return [];

        let commentsProv = this.props.thread.comments.slice();
        return queryTree(commentsProv, depth);
    };

    onLoadImage = (e) => {
        this.setState({
            utilityColor: constrantizer(this.img, this.container_utility),
            menuColor : constrantizer(this.img, this.container_menu, 10,  this.canvas_menu)
        });

        this.thread_content.style.width = (e.target.width + "px");
    };

    extendSocial = () => {
        this.setState((prevState) => {
            let depth;
            if(prevState.depth === 1)
                depth = -1;
            else depth = 1;

            return {
                depth,
                comments: this.saveTree(depth)
            };
        })
    };

    contrastBackground = () => {
        if(this.state.menuColor === "#FFFFFF")
            return "#000000";
        else return "#FFFFFF";
    };

    contrastImage = (image1, image2) => {
        if(this.state.menuColor === "#FFFFFF")
            return image2;
        else return image1;
    };

    onDocumentThreadMenuToggle = (e) => {
        document.removeEventListener("click", this.onDocumentThreadMenuToggle);

        this.setState((prevState) => {
            return {
                threadMenuToggle : !prevState.threadMenuToggle
            };
        });
    };

    onThreadMenuToggle = (e) => {
        if(!this.state.threadMenuToggle) {
            this.setState((prevState) => {
                document.addEventListener("click", this.onDocumentThreadMenuToggle);

                return {
                    threadMenuToggle : !prevState.threadMenuToggle
                };
            });
        }
    };

    onThreadMenuProp = (e) => {
        e.preventDefault();
        e.nativeEvent.stopImmediatePropagation();

    };

    onThreadDelete = (thread) => {
        this.props.onDispatchThreadDelete({...thread});
    };

    onThreadUpdate = (thread) => {
        this.props.onToggleThreadCreator(CONSTANTS.UPDATE, {
            ...thread,
            caption : this.img.src
        });
    };

    render = () => (
        <div className="thread">
            {
                this.state.caption && (
                    <div className="thread-caption">
                        <img className="thread-caption-content" ref={(img) => this.img = img}
                             crossorigin="Anonymous"
                             src={this.state.caption}
                             onLoad={this.onLoadImage}/>

                        <div className="thread-menu" ref={(container_menu) => this.container_menu = container_menu}>
                            <img className="thread-menu-icon"
                                 src={this.state.menuColor === "#FFFFFF" ? menu_white : menu_dark}
                                 onClick={this.onThreadMenuToggle}/>

                            {
                                this.state.threadMenuToggle && (
                                    <div className="thread-menu-container" style={{ backgroundColor : this.state.menuColor }} onClick={this.onThreadMenuProp}>
                                        <div className="thread-menu-option" style={{ color : this.contrastBackground() }} onClick={this.onThreadUpdate.bind(this, this.props.thread)}>
                                            <img className="thread-menu-option-icon" src={this.contrastImage(edit_white, edit_dark)}/>
                                            <p className="thread-menu-option-text">Edit thread</p>
                                        </div>
                                        <div className="thread-menu-option" style={{ color : this.contrastBackground() }} onClick={this.onThreadDelete.bind(this, this.props.thread)}>
                                            <img className="thread-menu-option-icon" src={this.contrastImage(delete_white, delete_dark)}/>
                                            <p className="thread-menu-option-text">Delete thread</p>
                                        </div>
                                    </div>
                                )
                            }
                        </div>

                        <div className="thread-utility" ref={(container_utility) => this.container_utility = container_utility}>
                            <div className="thread-votes" style={{ utilityColor: this.state.utilityColor }}>
                                <img src={(
                                    this.state.utilityColor === "#FFFFFF" ? like : like_dark
                                )} className="thread-like"/>
                                <p className="thread-votes-content">{this.props.thread.votes}</p>
                            </div>
                            <div className="thread-created_by" style={{ utilityColor: this.state.utilityColor }}>
                                <p>{ this.props.thread.author ? this.props.thread.author.name : "User"}</p>
                            </div>
                            <div className="thread-date_when" style={{ utilityColor: this.state.utilityColor }}>
                                <p>{ this.dateParser(this.props.thread._id) }</p>
                            </div>
                        </div>
                    </div>
                )
            }
            <div className="thread-content" ref={(thread_content) => this.thread_content = thread_content}>
                <div className="thread-content-container">
                    <p className="thread-content-text" >{this.props.thread.content}</p>
                    <div className="thread-social">
                        {
                            this.state.comments
                                .map((comment) => <Spark key={comment.id} {...comment}/>)
                        }
                    </div>
                    <div className="thread-extra">
                        <p className="thread-extra-more" onClick={this.extendSocial}>
                            {
                                this.state.comments.length > 0 &&
                                    (this.state.depth === 1 ? "More..." : "...Less")
                            }
                        </p>
                    </div>
                </div>
            </div>
        </div>
    )
}

const mapDispatchToProps = (dispatch) => ({
    onDispatchThreadDelete : (body) => dispatch(ACTIONS.HANDLE_THREAD(CONSTANTS.DELETE, body))
});

export default withRouter(connect(null, mapDispatchToProps)(Thread));