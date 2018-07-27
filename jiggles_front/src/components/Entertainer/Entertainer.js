import React from "react"
import {Component} from "react"
import ReactDOM from "react-dom"
import {connect} from "react-redux"
import {Link} from "react-router-dom"

import Settings from "../Settings/Settings"
import Search from "../Search/Search";
import releases from "../../dummy/releases"
import news from "../../dummy/news";

import logo from "./../../resources/assets/logo.svg"
import impact from "./../../resources/icons/impact.svg"
import Button from "../Components/Button/Button";

import "./style.sass"

class Release extends Component {
    constructor(props) {
        super(props);
    }

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
                    this.props.content.map((snippet, index) => (
                        <div className="highlight" key={index}>
                            <div className="highlight-container">

                                <div className="highlight-spotlight">
                                    <img className="highlight-poster" src={snippet.url} />

                                    <div className="highlight-content">
                                        <h1 className="highlight-content-artist">{snippet.artist}</h1>
                                        <h1 className="highlight-content-title">{snippet.title}</h1>
                                    </div>
                                </div>

                                <div className="highlight-score">
                                    {
                                        (snippet.score > 80) && (
                                            <img className="highlight-impact" src={impact} />
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
                                                    <p className="highlight-review-critic">{review.critic}</p>
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

class Divider extends Component {
    constructor(props) {
        super(props);
    }

    render = () => (
        <div className="main-divider"/>
    )
}

class News extends Component {
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
                    this.props.content.map((content, index) => (
                        <div className="news" key={index}>
                            <img className="news-image" src={content.url} />

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

export class Entertainer extends Component {
    constructor(props) {
        super(props);

        this.state = {
            toggleSearch: {
                trigger: false,
                first_container: {
                    opacity: 1.0
                },
                second_container: {
                    display: "none"
                }
            }
        }
    }

    componentDidMount = () => {
        ReactDOM.findDOMNode(this).parentNode.className = "extendable";
    };

    onToggleSearch = () => {
        this.setState((prevState) =>
            (prevState.toggleSearch.trigger) ? ({
                toggleSearch: {
                    trigger: false,
                    first_container: {
                        opacity: 1.0
                    },
                    second_container: {
                        display: "none"
                    }
                }
            }) : ({
                toggleSearch: {
                    trigger: true,
                    first_container: {
                        opacity: 0.4
                    },
                    second_container: {
                        display: "flex"
                    }
                }
            })
        );
    };

    render = () => (
        <div className="main">
            <div className="main-container" style={{ display: this.state.toggleSearch.second_container.display }}>
                {
                    this.state.toggleSearch.trigger ? (
                            <Search onToggleSearch={this.onToggleSearch}/>
                    ) : null
                }
            </div>

            <div className="main-container" style={{opacity: this.state.toggleSearch.first_container.opacity}}>
                <div className="main-header">

                    <div className="main-header-container">
                        <img id="main-header-logo" src={logo}/>

                        <Button value="Search" capsLocked={false} iconBased={true} selectable={null} backgroundColor="#148491" onClick={this.onToggleSearch}/>
                    </div>

                    <div className="main-header-miscellaneous">
                        <div className="main-header-links">
                            <Link to="/forum">
                                <p className="main-header-link">Forum</p>
                            </Link>
                            <Link to="/studio">
                                <p className="main-header-link">Playcollection</p>
                            </Link>
                        </div>

                        <Settings/>
                    </div>
                </div>

                <div className="main-content">
                    <div className="main-content-header">
                        <p className="main-content-header-text">
                            Currently recommended tracks
                        </p>
                    </div>

                    <div className="main-content-body">
                        <iframe
                            className="main-content-widget" src="https://open.spotify.com/embed?uri=spotify:track:11dFghVXANMlKmJXsNCbNl"
                            width="240" height="240" frameBorder="0" allowtransparency="true"
                            allow="encrypted-media"/>

                        <iframe
                            className="main-content-widget" src="https://open.spotify.com/embed?uri=spotify:track:11dFghVXANMlKmJXsNCbNl"
                            width="240" height="240" frameBorder="0" allowtransparency="true"
                            allow="encrypted-media"/>

                        <iframe
                            className="main-content-widget" src="https://open.spotify.com/embed?uri=spotify:track:11dFghVXANMlKmJXsNCbNl"
                            width="240" height="240" frameBorder="0" allowtransparency="true"
                            allow="encrypted-media"/>
                    </div>
                </div>

                <div className="main-body">
                    <Release content={releases} label="Releases"/>
                    <Divider />
                    <News content={news} label="News"/>
                </div>
            </div>
        </div>
    )
}

export default connect(null, )(Entertainer);