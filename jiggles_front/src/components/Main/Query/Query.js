import React from "react"
import {Component} from "react"
import CONSTANTS from "./../../../utils/Constants";

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
            content : this.parseQuery(props),
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

    componentWillReceiveProps = (nextProps) => {
        if(nextProps.content !== this.props.content ||
            nextProps.type !== this.props.type) {
            this.setState({
                content : this.parseQuery(nextProps)
            })
        }
    };

    parseQuery = (props) => Object.keys(props.content).reduce((acc, type) => {
        acc[type] = {};

        if(props.type === type || props.type === CONSTANTS.ALL) {
            if(props.content[type].hasOwnProperty("items"))
                acc[type]["items"] = props.content[type].items;
            else acc[type]["items"] = props.content[type];
        } else acc[type]["items"] = [];

        return acc;
    }, {});

    render = () => (
        <div className="main-query">
            <div className="main-query-row">
                {
                    ["artists", "albums"].map((queryType) => this.state.content[queryType].items.length !== 0 && (
                        <QueryContent
                            key={queryType}
                            contents={this.state.content[queryType].items.slice(0, this.state[queryType].max)}
                            type={queryType.slice(0, queryType.length-1)}
                            direction="row"/>
                    ))
                }
            </div>
            <div className="main-query-row">
                {
                    ["tracks"].map((queryType) => this.state.content[queryType].items.length !== 0 && (
                        <QueryContent
                            key={queryType}
                            contents={this.state.content[queryType].items.slice(0, this.state[queryType].max)}
                            type={queryType.slice(0, queryType.length-1)}
                            direction="row"/>
                    ))
                }
            </div>
        </div>
    )
}