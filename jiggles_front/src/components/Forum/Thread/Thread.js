import React from "react"
import {Component} from "react"
import CONSTANTS from "./../../../utils/Constants";

import constrantizer from "../../../utils/TextImageContrastizer"
import queryTree from "../../../utils/CommentParser"
import like from "./../../../resources/icons/like.svg"
import like_blue from "./../../../resources/icons/like-blue.svg"
import like_dark from "./../../../resources/icons/like-dark.svg"

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

        this.expression = /[-a-zA-Z0-9@:%_\+.~#?&//=]{2,256}\.[a-z]{2,4}\b(\/[-a-zA-Z0-9@:%_\+.~#?&//=]*)?/gi;
        this.regex = new RegExp(this.expression);

        this.caption = (props.thread.caption) ?
            (this.expression.test(props.thread.caption) ?
                new UriBuilder()
                    .setScheme(CONSTANTS.SCHEME)
                    .setAuthority(CONSTANTS.AUTHORITY)
                    .appendPath(props.thread.caption)
                    .build() : props.thread.caption)
            : null;

        let depth = 1;

        this.state = {
            depth: depth,
            color: "#000000",
            comments: this.saveTree(depth)
        };
    }

    componentWillReceiveProps = (nextProps) => {
        if(this.caption !== nextProps.thread.caption)
            this.caption = (nextProps.thread.caption) ?
                (this.expression.test(nextProps.thread.caption) ?
                    new UriBuilder()
                        .setScheme(CONSTANTS.SCHEME)
                        .setAuthority(CONSTANTS.AUTHORITY)
                        .appendPath(nextProps.thread.caption)
                        .build() : nextProps.thread.caption)
                : null;
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
            color: constrantizer(this.img, this.container)
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

    render = () => (
        <div className="thread">
            {
                this.caption && (
                    <div className="thread-caption">
                        <img className="thread-caption-content" ref={(img) => this.img = img}
                             crossOrigin="Anonymous" src={this.caption} onLoad={this.onLoadImage}/>

                        <div className="thread-utility" ref={(container) => this.container = container}>
                            <div className="thread-votes" style={{ color: this.state.color }}>
                                <img src={(
                                    this.state.color === "#FFFFFF" ? like : like_dark
                                )} className="thread-like"/>
                                <p className="thread-votes-content">{this.props.thread.votes}</p>
                            </div>
                            <div className="thread-created_by" style={{ color: this.state.color }}>
                                <p>{this.props.thread.created_by || "User"}</p>
                            </div>
                            <div className="thread-date_when" style={{ color: this.state.color }}>
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

export default Thread;