import React from "react"
import {Component} from "react"

import "./style.sass"

class QueryContent extends Component {
    constructor(props) {
        super(props);
    }

    capitalizeFirstLetter = (string) => {
        return string.charAt(0).toUpperCase() + string.slice(1);
    };

    render = () => (
        <div className="main-query-container">
            <div className="main-search-header">
                <p className="main-search-category">{this.capitalizeFirstLetter(this.props.type)}</p>
            </div>

            <div
                className="main-search-contents"
                style={{ flexDirection : this.props.direction}}>
                {
                    this.props.contents.map((content) => (
                        <iframe
                            className={"main-search-widget " + "main-widget-" + this.props.type}
                            src={"https://open.spotify.com/embed?uri=" + content.uri}
                            frameBorder="0"
                            width="225" height="275"
                            allowTransparency="true"
                            allow="encrypted-media"/>
                    ))
                }
            </div>
        </div>
    );
}

export default class Query extends Component {
    constructor(props) {
        super(props);

        this.state = {
            artists : {
                max : 1,
                extendable : false
            },
            albums : {
                max : 3,
                extendable : false
            },
            tracks : {
                max : 8,
                extendable : false
            }
        }
    }

    render = () => (
        <div className="main-query">
            <div className="main-query-row">
                {
                    ["artists", "albums"].map((queryType) => (
                        <QueryContent
                            contents={this.props.search[queryType].items.slice(0, this.state[queryType].max)}
                            type={queryType}
                            direction="row"/>
                    ))
                }
            </div>
            <div className="main-query-row">
                {
                    ["tracks"].map((queryType) => (
                        <QueryContent
                            contents={this.props.search[queryType].items.slice(0, this.state[queryType].max)}
                            type={queryType}
                            direction="row"/>
                    ))
                }
            </div>
        </div>
    )
}