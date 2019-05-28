import React        from "react"
import {Component}  from "react"
import {ACTIONS}    from "../../reducers/main"
import {connect}    from "react-redux"

import cancel from "./../../resources/icons/cancel.svg"
import cancelFocused from "./../../resources/icons/cancel-focused.svg"

import "./style.sass"

export class Search extends Component {
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
        // Enter key
        if(e.which === 13) {
            this.props.fetchQuery(this.state.value);
            this.props.onToggleSearch();
            // Esc key
        } else if(e.which === 27) this.props.onToggleSearch();
    };

    onChange = (e) => {
        this.setState({
            value : e.target.value
        });

        let percentage = e.target.value.length * this.state.size.characterSize / this.state.size.maxSize;
        this.border.style.width = Math.max(this.state.size.maxSize*percentage, this.state.size.characterSize) + "px";
    };

    render = () => (
        <div className="search" onKeyDown={this.onSubmit}>
            <img className="search-cancel" alt="cancel" src={this.state.cancel} onMouseOver={this.onHover} onMouseOut={this.onHoverOut} onClick={this.props.onToggleSearch}/>
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


const mapDispatchToProps = (dispatch) => ({
    fetchQuery : (queryString) => dispatch(ACTIONS.ENTERTAINER_QUERY(queryString))
});

export default connect(null, mapDispatchToProps)(Search);