import React from "react"
import {Component} from "react"

import Widget from "../Widget/Widget";

import "./style.sass"

class QueryContent extends Component {
    capitalizeFirstLetter = (string) => {
        return string.charAt(0).toUpperCase() + string.slice(1);
    };

    render = () => (
        <div className="main-query-container">
            <div className="main-search-header">
                <p className="main-search-category">{this.capitalizeFirstLetter(this.props.type)}</p>
            </div>

            <div className="main-search-contents">
                {
                    this.props.contents.map((content) => (
                        <Widget content={content} key={content.id} type={this.props.type}/>
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
                    ["artists", "albums"].map((queryType) => this.props.search[queryType].items.length !== 0 && (
                        <QueryContent
                            key={queryType}
                            contents={this.props.search[queryType].items.slice(0, this.state[queryType].max)}
                            type={queryType}
                            direction="row"/>
                    ))
                }
            </div>
            <div className="main-query-row">
                {
                    ["tracks"].map((queryType) => this.props.search[queryType].items.length !== 0 && (
                        <QueryContent
                            key={queryType}
                            contents={this.props.search[queryType].items.slice(0, this.state[queryType].max)}
                            type={queryType}
                            direction="row"/>
                    ))
                }
            </div>
        </div>
    )
}