import React from "react"
import {Component} from "react"

import Widget from "../Widget/Widget";

import "./style.sass"
import recommendations from "../../../dummy/recommandations";

export default class Recommendation extends Component {
    constructor(props) {
        super(props);

        this.state = {
            activeToolbar : Object.keys(recommendations)[0]
        }
    }

    capitalizeFirstLetter = (string) => {
        return string.charAt(0).toUpperCase() + string.slice(1);
    };

    onToggleToolbar = (recommendationType) => {
        this.setState({
            activeToolbar: recommendationType
        })
    };

    render = () => (
        <div className="main-dynamic">
            <div className="main-dynamic-toolbar">
                <div className="toolbar-header">
                    Recommended
                </div>

                <div className="toolbar-divider"/>

                <div className="toolbar-actions">
                    {
                        Object.keys(this.props.recommendations).map((recommendationType, index) => (
                            <p className={"main-dynamic-toolbar-action " + ((this.state.activeToolbar === recommendationType) && "toolbar-active")}
                               key={index}
                               onClick={this.onToggleToolbar.bind(this, recommendationType)}>
                                {this.capitalizeFirstLetter(recommendationType)}
                            </p>
                        ))
                    }
                </div>
            </div>

            <div className="main-dynamic-viewpager">
                {
                    this.props.recommendations[this.state.activeToolbar].map((recommendation, index) => (
                        <Widget content={recommendation} type="albums" key={index}/>
                    ))
                }
            </div>
        </div>
    )
}