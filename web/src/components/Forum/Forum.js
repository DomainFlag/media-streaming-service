import React from "react"
import {Component} from "react"
import {connect} from "react-redux"

import ReactDOM from "react-dom";
import CONSTANTS from "../../utils/Constants";
import {ACTIONS} from "../../reducers/forum";

import ThreadCreator from "./ThreadCreator/ThreadCreator";
import Settings from "../Settings/Settings";
import create from "./../../resources/icons/create.svg"
import menu_icon from "./../../resources/icons/social-menu.svg"
import logo from "./../../resources/assets/logo-white.svg"
import Thread from "./Thread/Thread";

import "./style.sass"

export class Forum extends Component {
    constructor(props) {
        super(props);

        this.state = {
            threadCreatorToggle : false,
            thread : {
                mode : CONSTANTS.NONE
            }
        };

        props.fetchThreads();
    }

    componentDidMount = () => {
        ReactDOM.findDOMNode(this).parentNode.className = "extendable";
    };

    onToggleThreadCreator = (mode, thread) => {
        this.setState((prevState) => {
            if(mode === null || mode === CONSTANTS.NONE || !prevState.threadCreatorToggle) {
                return {
                    threadCreatorToggle: !prevState.threadCreatorToggle,
                    thread: {
                        mode,
                        ...thread
                    }
                }
            } else {
                return {
                    thread: {
                        mode,
                        ...thread
                    }
                }
            }
        });
    };

    render = () => (
        <div className="forum">
            <div className="forum-header">
                <div className="forum-container">
                    <img className="forum-menu" alt="menu icon" src={menu_icon} onClick={this.props.history.goBack}/>
                </div>

                <div className="forum-container-extended">
                    <p className="forum-label">Compartments</p>
                    {
                        ["Community", "Discover", "Create"].map((compartment, index) => (
                            <p className="forum-link" key={index}>{compartment}</p>
                        ))
                    }
                </div>

                <div className="forum-container-extended">
                    <p className="forum-label">Filter By</p>
                    {
                        ["Song", "Album", "Artist"].map((filterBy, index) => (
                            <p className="forum-link" key={index}>{filterBy}</p>
                        ))
                    }
                </div>

                <div className="forum-container">
                    <img className="forum-main-logo-content" alt="logo" src={logo} />
                </div>
            </div>
            <div className="forum-header">
            </div>
            <div className="forum-main">
                <div className="forum-main-header">
                    <input className="forum-finder" type="text" placeholder="Search..."/>

                    <div className="forum-tools">
                        <div className="forum-tools-items">
                            <img className="forum-tools-item" alt="create thread" src={create} onClick={this.onToggleThreadCreator.bind(this, CONSTANTS.CREATE, null)}/>
                        </div>

                        <Settings/>
                    </div>
                </div>
                {
                    this.state.threadCreatorToggle && (
                        <ThreadCreator thread={this.state.thread} onToggleThreadCreator={this.onToggleThreadCreator.bind(this, CONSTANTS.NONE, null)}/>
                    )
                }
                <div className="forum-main-container">
                    <div className="forum-main-content">
                        {
                            this.props.forum.threads.map((thread) => (
                                <Thread type={CONSTANTS.THREAD_VIEW} key={thread._id} thread={thread} onToggleThreadCreator={this.onToggleThreadCreator}/>
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