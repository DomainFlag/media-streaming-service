import React from "react"
import {Component} from "react"
import {connect} from "react-redux"

import "./style.sass"

import {ACTIONS} from "../../reducers/forum";
import Settings from "../Settings/Settings";
import menu_icon from "./../../resources/icons/social-menu.svg"
import logo from "./../../resources/assets/logo-white.svg"
import Thread from "./Thread/Thread";
import ReactDOM from "react-dom";

import fireplaces from "../../dummy/threads";

export class Forum extends Component {
    constructor(props) {
        super(props);

        props.fetchThreads();
    }

    componentDidMount = () => {
        ReactDOM.findDOMNode(this).parentNode.className = "extendable";
    };

    render = () => (
        <div className="forum">
            <div className="forum-header">
                <div className="forum-container">
                    <img className="forum-menu" src={menu_icon}/>
                </div>

                <div className="forum-container-extended">
                    <p className="forum-label">Compartments</p>
                    <p className="forum-link">Community</p>
                    <p className="forum-link">Discover</p>
                    <p className="forum-link">Create</p>
                </div>

                <div className="forum-container-extended">
                    <p className="forum-label">Filter By</p>
                    <p className="forum-link">Song</p>
                    <p className="forum-link">Album</p>
                    <p className="forum-link">Artist</p>
                </div>

                <div className="forum-container">
                    <img className="forum-main-logo-content" src={logo} />
                </div>
            </div>
            <div className="forum-header">
            </div>
            <div className="forum-main">
                <div className="forum-main-header">
                    <input className="forum-finder" type="text" placeholder="Search..."/>

                    {/*{Settings Component}*/}
                    <Settings/>
                </div>
                <div className="forum-main-container">
                    <div className="forum-main-content">
                        {
                            this.props.forum.threads.map((thread) => (
                                <Thread thread={thread}/>
                            ))
                        }
                    </div>
                </div>
            </div>
        </div>
    )
}

const mapStateToProps = (state) => ({
    forum : state.forum
});

const mapDispatchToProps = (dispatch) => ({
    fetchThreads : () => dispatch(ACTIONS.HANDLE_THREAD())
});

export default connect(mapStateToProps, mapDispatchToProps)(Forum);