import React from "react"
import {Component} from "react"
import {connect} from "react-redux"

import "./style.sass"
import star_empty from "../../../resources/icons/star-empty.svg";
import star from "../../../resources/icons/star.svg";
import CONSTANTS from "../../../utils/Constants";
import {ACTIONS} from "../../../reducers/account"

export class Widget extends Component {
    constructor(props) {
        super(props);

        this.state = {
            favourite : this.props.content.favourite || false
        }
    }

    componentWillReceiveProps = (nextProps) => {
        if(this.props.content.favourite !== nextProps.content.favourite) {
            this.setState({
                favourite : nextProps.content.favourite
            })
        }
    };

    onFavouriteContent = () => {
        this.setState((prevState) => {
            if(!prevState.favourite) {
                this.props.starContent(this.props.content);
            } else {
                this.props.unStarContent(this.props.content);
            }

            return {
                favourite : !prevState.favourite
            }
        });
    };

    render = () => (
        <div className={"main-widget-container main-widget-container-" + this.props.type}>
            <div className="main-widget-save" onClick={this.onFavouriteContent}>
                <img className="main-widget-star" alt="star" src={this.state.favourite ? star : star_empty}/>
            </div>
            <iframe
                title={this.props.content.uri}
                className={"main-content-widget main-widget-" + this.props.type}
                src={"https://open.spotify.com/embed?uri=" + this.props.content.uri}
                frameBorder="0"
                allowtransparency="true"
                allow="encrypted-media"/>
        </div>
    )
}

const mapDispatchToProps = (dispatch) => ({
    starContent : (body) => dispatch(ACTIONS.USER_COLLECTION(CONSTANTS.POST, body)),
    unStarContent : (body) =>  dispatch(ACTIONS.USER_COLLECTION(CONSTANTS.DELETE, body)),
});

export default connect(null, mapDispatchToProps)(Widget);