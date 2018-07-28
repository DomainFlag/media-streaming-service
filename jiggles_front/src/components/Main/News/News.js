import React from "react"
import {Component} from "react"

import "./style.sass"

export default class News extends Component {
    constructor(props) {
        super(props);
    }

    render = () => (
        <div className="entertainment">
            <div className="entertainment-label">
                <p className="entertainment-label-value">
                    {
                        this.props.label
                    }
                </p>
            </div>
            <div className="entertainment-container">
                {
                    this.props.content.map((content) => (
                        <div className="news" key={content._id}>
                            <img className="news-image" src={content.caption} />

                            <div className="news-content">
                                <h1 className="news-content-title">{content.header}</h1>
                            </div>
                        </div>
                    ))
                }
            </div>
        </div>
    );
}