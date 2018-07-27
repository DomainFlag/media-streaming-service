import React from "react"
import {Component} from "react"

import constrantizer from "../../../utils/TextImageContrastizer"
import queryTree from "../../../utils/CommentParser"
import like from "./../../../resources/icons/like.svg"
import like_blue from "./../../../resources/icons/like-blue.svg"
import like_dark from "./../../../resources/icons/like-dark.svg"

import "./style.sass"

class Spark extends Component {
    constructor(props) {
        super(props);
    }

    render = () => {
        return <div className="spark">
            <div className="spark-section">
                <div className="spark-container">
                    <img className="spark-avatar"
                         src={`${this.props.comment.comment.header.avatar}`}/>

                    <div className="spark-miscellaneous-container">
                        <p className="spark-author">
                            {this.props.comment.comment.name}
                        </p>

                        <p className="spark-date">
                            {this.props.comment.comment.header.date}
                            </p>
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

class Fireplace extends Component {
    constructor(props) {
        super(props);

        let depth = 1;

        this.state = {
            depth: depth,
            color: "#000000",
            comments: this.saveTree(depth)
        };
    }

    saveTree = (depth) => {
        if(!this.props.fireplace.hasOwnProperty("comments"))
            return [];

        let commentsProv = this.props.fireplace.comments.slice();
        return queryTree(commentsProv, depth);
    };

    onLoadImage = (e) => {
        this.setState({
            color: constrantizer(this.img, this.container)
        });

        this.fireplace_content.style.width = (e.target.width + "px");
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
        <div className="fireplace">
            <div className="fireplace-caption">
                <img className="fireplace-caption-content"
                     src={this.props.fireplace.caption}
                     ref={(img) => this.img = img }
                     onLoad={this.onLoadImage}/>
                <div className="fireplace-utility" ref={(container) => this.container = container}>
                    <div className="fireplace-votes" style={{ color: this.state.color }}>
                        <img src={(
                            this.state.color === "#FFFFFF" ? like : like_dark
                        )} className="fireplace-like"/>
                        <p className="fireplace-votes-content">{this.props.fireplace.votes}</p>
                    </div>
                    <div className="fireplace-created_by" style={{ color: this.state.color }}>
                        <p>{this.props.fireplace.created_by || "User"}</p>
                    </div>
                    <div className="fireplace-date_when" style={{ color: this.state.color }}>
                        <p>{this.props.fireplace.created_when || (new Date()).toDateString()}</p>
                    </div>
                </div>
            </div>
            <div className="fireplace-content" ref={(fireplace_content) => this.fireplace_content = fireplace_content}>
                <div className="fireplace-content-container">
                    <p className="fireplace-content-text" >{this.props.fireplace.content}</p>
                    <div className="fireplace-social">
                        {
                            this.state.comments
                                .map((comment) => (
                                    <Spark key={comment.id} comment={comment}/>
                                ))
                        }
                    </div>
                    <div className="fireplace-extra">
                        <p className="fireplace-extra-more" onClick={this.extendSocial}>
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

export default Fireplace;