import React from "react"
import {Component} from "react"

import "./style.sass"

import cancel from "./../../resources/icons/cancel.svg"
import cancelFocused from "./../../resources/icons/cancel_focused.svg"
import UriBuilder from "../../utils/UriBuilder";

const SEARCH_TYPES = [
    "artist",
    "album",
    "track"
];

const SCHEME = "http";
const AUTHORITY = "localhost:8080";

export default class Search extends Component {
    constructor(props) {
        super(props);

        this.state = {
            value : null,
            size : {
                maxSize : null,
                characterSize: null,
                currentSize : null
            },
            cancel: cancel
        }
    }

    componentDidMount = () => {
        this.setState(({
            size: {
                maxSize: this.input.getBoundingClientRect().width,
                characterSize: this.test.clientWidth+1,
                currentSize: null
            }
        }));

        this.input.focus();
    };

    onHover = (e) => {
        this.setState({
            cancel: cancelFocused
        })
    };

    onHoverOut = (e) => {
        this.setState({
            cancel: cancel
        })
    };

    onSubmit = (e) => {
        if(e.which === 13) {
            SEARCH_TYPES.forEach((type) => {
                let query = {};
                query[type] = this.state.value;

                let uri = new UriBuilder()
                    .setScheme(SCHEME)
                    .setAuthority(AUTHORITY)
                    .appendPath(type)
                    .appendQueryParameter(query)
                    .build();

                let headers = new Headers();
                headers.append("Method", "GET");
                headers.append("Content-Type", "application/json");

                fetch(uri, { headers }).then((data) => data.json())
                    .then(console.log)
                    .catch(console.error);
            });
        }
    };

    onChange = (e) => {
        this.setState({
            value : e.target.value
        });

        let percentage = e.target.value.length * this.state.size.characterSize / this.state.size.maxSize;
        console.log(this.state.size.maxSize);
        this.border.style.width = Math.max(this.state.size.maxSize*percentage, this.state.size.characterSize) + "px";
    };

    render = () => (
        <div className="search" onKeyDown={this.onSubmit}>
            <img className="search-cancel" src={this.state.cancel} onMouseOver={this.onHover} onMouseOut={this.onHoverOut} onClick={this.props.onToggleSearch}/>
            <div className="search-container">
                <div className="search-container-input" >
                    <div ref={(test) => { this.test = test; }} className="search-test">T</div>
                    <input ref={(input) => { this.input = input; }} className="search-input" placeholder="Song, album, artist..." onChange={this.onChange}/>
                    <div ref={(border) => { this.border = border; }} className="search-input-border" tabIndex="-1"/>
                </div>
            </div>
        </div>
    )
}