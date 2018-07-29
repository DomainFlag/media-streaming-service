import React from "react"
import {Component} from "react"

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
                        Object.keys(this.props.recommendations).map((recommendationType) => (
                            <p className={"main-dynamic-toolbar-action " + ((this.state.activeToolbar === recommendationType) && "toolbar-active")}
                               onClick={this.onToggleToolbar.bind(this, recommendationType)}>
                                {this.capitalizeFirstLetter(recommendationType)}
                            </p>
                        ))
                    }
                </div>
            </div>

            <div className="main-dynamic-viewpager">
                {
                    this.props.recommendations[this.state.activeToolbar].map((recommendation) => (
                        <iframe
                            className="main-content-widget"
                            src={recommendation}
                            width="240" height="240" frameBorder="0"
                            allowTransparency="true"
                            allow="encrypted-media"/>
                    ))
                }
            </div>
        </div>
    )
}