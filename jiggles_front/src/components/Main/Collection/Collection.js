import React from "react"
import {Component} from "react"
import {connect} from "react-redux"

import "./style.sass"
import recommendations from "../../../dummy/recommandations";
import Query from "../Query/Query";

export class Collection extends Component {
    constructor(props) {
        super(props);

        this.types = ["all", "albums", "artists", "tracks"];

        this.state = {
            components : [{
                type : "Recommendation",
                content : recommendations
            }, {
                type : "Collection",
                content : this.props.content
            }, {
                type : "Query",
                content : this.props.search
            }],
            activeComponent : 0,
            activeToolbar : this.types[0]
        }
    }

    componentWillReceiveProps = (nextProps) => {
        if(this.state.components[1].content !== nextProps.content) {
            this.setState((prevState) => ({
                components : [{
                    type : "Recommendation",
                    content : recommendations
                }, {
                    type : "Collection",
                    content : nextProps.content
                }, {
                    type : "Query",
                    content : prevState.components[2].content
                }]
            }));
        }

        if(this.state.components[2].content !== nextProps.search) {
            this.setState((prevState) => ({
                components : [{
                    type : "Recommendation",
                    content : recommendations
                }, {
                    type : "Collection",
                    content : prevState.components[1].content
                }, {
                    type : "Query",
                    content : nextProps.search
                }],
                activeComponent : 2,
                activeToolbar : this.types[0]
            }));
        }
    };


    capitalizeFirstLetter = (string) => {
        return string.charAt(0).toUpperCase() + string.slice(1);
    };

    onToggleToolbar = (type) => {
        this.setState({
            activeToolbar: type
        });
    };

    onToggleComponent = (index) => {
        if(index !== 2 || this.state.components[2].content !== null) {
            this.setState({
                activeComponent : index
            });
        }
    };

    render = () => {
        let component = this.state.components[this.state.activeComponent];

        return <div className="main-dynamic">
            <div className="main-dynamic-toolbar">
                <div className="toolbar-components">
                    {
                        this.state.components.map((component, index) => (
                            <p className={"toolbar-component " + ((index === this.state.activeComponent) ? "toolbar-component-active" :
                                (index === 2 && this.state.components[2].content === null) && "toolbar-component-closed")}
                               key={component.type}
                               onClick={this.onToggleComponent.bind(this, index)}>
                                {component.type}
                            </p>
                        ))
                    }
                </div>

                <div className="toolbar-divider"/>

                <div className="toolbar-actions" ref={(container) => this.container = container}>
                    {
                        this.types.map((type, index) => (
                            <p className={"main-dynamic-toolbar-action " + ((this.state.activeToolbar === type) && "toolbar-active")}
                               key={index}
                               onClick={this.onToggleToolbar.bind(this, type)}>
                                {this.capitalizeFirstLetter(type)}
                            </p>
                        ))
                    }
                </div>
            </div>

            <div className="main-dynamic-viewpager">
                <Query content={component.content} type={this.state.activeToolbar}/>
            </div>
        </div>
    }
}

const mapStateToProps = (state) => ({
    content : state.account.user !== undefined ? state.account.user.content : {}
});

export default connect(mapStateToProps, null)(Collection);