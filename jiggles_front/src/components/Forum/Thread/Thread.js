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


        this.path = new UriBuilder()
            .setScheme(CONSTANTS.SCHEME)
            .setAuthority(CONSTANTS.APP)
            .build();
    }

    render = () => {
        return <div className="spark">
            <div className="spark-section">
                <div className="spark-container">
                    <img className="spark-avatar"
                         src={this.path + this.props.comment.comment.content.caption}/>

                    <div className="spark-miscellaneous-container">
                        <p className="spark-author">{this.props.comment.comment.name}</p>

                        {/*<p className="spark-date">{this.props.comment.comment.header.date}</p>*/}
                    </div>
                </div>
                <div className="spark-container">
                    <div className="spark-content">
                        {this.props.comment.comment.content.text}
                    </div>
                </div>
                <div className="spark-container">
                    <div className="spark-like-container">
                        <img src={like_blue} className="spark-like"/>
                        <p className="spark-votes">{this.props.comment.comment.likes}</p>
                    </div>
                </div>
            </div>
            <div className="spark-inception">
                {
                    this.props.comment.children.map((comment) => (
                        <Spark key={comment.id} comment={comment}/>
                    ))
                }
            </div>
        </div>
    }
}

class Thread extends Component {
    constructor(props) {
        super(props);

        this.path = new UriBuilder()
            .setScheme(CONSTANTS.SCHEME)
            .setAuthority(CONSTANTS.APP)
            .build();

        let depth = 1;

        this.state = {
            depth: depth,
            color: "#000000",
            comments: this.saveTree(depth)
        };
    }

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
            <div className="thread-caption">
                <img className="thread-caption-content"
                     src={this.path + this.props.thread.caption} />
                
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
                        <p>{this.props.thread.created_when || (new Date()).toDateString()}</p>
                    </div>
                </div>
            </div>
            <div className="thread-content" ref={(thread_content) => this.thread_content = thread_content}>
                <div className="thread-content-container">
                    <p className="thread-content-text" >{this.props.thread.content}</p>
                    <div className="thread-social">
                        {
                            this.state.comments
                                .map((comment) => (
                                    <Spark key={comment.id} comment={comment}/>
                                ))
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