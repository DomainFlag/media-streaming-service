import React from "react"
import {Component} from "react"

import impact from "./../../../resources/icons/impact.svg"

import "./style.sass"

export default class Release extends Component {
    measureScore = (score) => {
        if(score > 80)
            return "#29A369";
        else if(score > 60)
            return "#F2E827";
        else return "#DB2E3B";
    };

    sortByScore = (review_1, review_2) => {
        if(review_1.score < review_2.score)
            return 1;
        else if(review_1.score > review_2.score)
            return -1;
        else return 0;
    };

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
                    this.props.content.map((snippet) => (
                        <div className="highlight" key={snippet._id}>
                            <div className="highlight-container">

                                <div className="highlight-spotlight">
                                    <img className="highlight-poster" alt="poster" src={snippet.url} />

                                    <div className="highlight-content">
                                        <h1 className="highlight-content-artist">{snippet.artist}</h1>
                                        <h1 className="highlight-content-title">{snippet.title}</h1>
                                    </div>
                                </div>

                                <div className="highlight-score">
                                    {
                                        (snippet.score > 80) && (
                                            <img className="highlight-impact" alt="critical acclaim" src={impact} />
                                        )
                                    }
                                    <p className="highlight-score-value" style={{ color: snippet.score > 80 ? "#22895D" : "#15101C"}}>
                                        {
                                            snippet.score / 10.0
                                        }
                                    </p>
                                </div>
                            </div>
                            <div className="highlight-container">
                                <div className="highlights-reviews">
                                    {
                                        snippet.reviews
                                            .sort(this.sortByScore)
                                            .map((review, index) => (
                                            <div className="highlight-review" key={index}>
                                                <div className="highlight-review-container">
                                                    <p className="highlight-review-score" style={{backgroundColor: this.measureScore(review.score)}}>{review.score}</p>
                                                </div>
                                                <div className="highlight-review-container">
                                                    <p className="highlight-review-critic">{review.author}</p>
                                                    <p className="highlight-review-content">{review.content}</p>
                                                </div>
                                            </div>
                                        ))
                                    }
                                </div>
                            </div>
                        </div>
                    ))
                }
            </div>

        </div>
    );
}