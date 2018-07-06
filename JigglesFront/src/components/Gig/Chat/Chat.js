import React from "react"
import {Component} from "react"
import {connect} from "react-redux"

import "./style.sass"
import chat_icon from "./../../../resources/icons/send-button.svg"
import chat_slider from "./../../../resources/icons/chat-slider.svg"
import social_profile from "./../../../resources/icons/account.svg"
import search_icon from "./../../../resources/icons/lens.svg"

const CONNECTION = {
    0: "connected",
    1: "away",
    2: "occupied"
};

class Profile extends Component {
    constructor(props) {
        super(props);
    }

    render = () => (
        <div className="social-profile">
            <img className="social-profile-acc" src={social_profile} />
            <div className="social-profile-details">
                <p className="social-profile-name">{this.props.name}</p>
                <div className="social-profile-connection">
                    <div className={"social-profile-connection-icon " + "connection_" + this.props.connection }/>
                    <div className="social-profile-connection-value">
                        {CONNECTION[this.props.connection]}
                    </div>
                </div>
            </div>
        </div>
    );
}

class Message extends Component {
    constructor(props) {
        super(props);
    }

    render = () => (
        <div className={"chat-message " + this.props.who}>
            <p className="chat-message-body">{this.props.body}</p>
        </div>
    );
}

export default class Chat extends Component {
    constructor(props) {
        super(props);

        this.state = {
            messages: this.props.messages,
            toggleContainer: {
                socialContainer: null,
                socialSlider: null
            }
        }
    }

    componentDidMount = () => {};

    onToggleContainer = () => {
        this.setState((prevState) => {
            if(prevState.toggleContainer.socialSlider)
                return {
                    toggleContainer: {
                        socialContainer: null,
                        socialSlider: null
                    }
                };
            else {
                return {
                    toggleContainer: {
                        socialContainer: "talk-container-toggle",
                        socialSlider: "talk-slider-action_slided"
                    }
                };
            }
        });
    };

    onAddMessage = (e) => {
        let keyPressed = e.which || e.keyCode;
        if(keyPressed === 13) {
            this.sendMessage();
        }
    };

    sendMessage = () => {
        this.setState(({
            messages: this.state.messages.concat({
                id: this.state.messages[this.state.messages.length-1].id+1,
                name: "John Dow",
                body: this.input.innerText
            })
        }));

        this.input.innerHTML = this.props.placeholder;
        this.input.blur();
    };

    componentDidUpdate = () => {
        this.chat.scrollTop = this.chat.scrollHeight;
    };

    onChange = () => {
        this.input.style.height = this.input.scrollHeight + "px";
        this.chat.scrollTop = this.chat.scrollHeight;
    };

    onFocus = () => {
        if(this.input.innerText === this.props.placeholder)
            this.input.innerText = "";
    };

    onBlur = () => {
        if(this.input.innerText.length === 0)
            this.input.innerText = this.props.placeholder;
    };

    render = () => (
        <div className="talk">
            <div className="talk-chat">
                <div className="chat-header">
                    <div className="chat-recipient">
                        <p className="chat-recipient-name">{this.props.profile.profile.name}</p>
                    </div>
                </div>
                <div className="chat-body">
                    <div className="chat-body-container" ref={(chat) => this.chat = chat}>
                        {
                            this.state.messages.map((message) => {
                                return message.name === this.props.profile.name ? (
                                    <Message key={message.id} who="sender" body={message.body} />
                                ) : (
                                    <Message key={message.id} who="recipient" body={message.body} />
                                )
                            })
                        }
                    </div>
                </div>
                <div className="chat-divider-container">
                    <div className="chat-divider"/>
                </div>
                <div className="chat-action">
                    <div className="chat-input" ref={(input) => this.input = input} onFocus={this.onFocus} onBlur={this.onBlur} onChange={this.onChange} onKeyDown={this.onAddMessage} contentEditable="true">
                        {this.props.placeholder}
                    </div>
                    <img className="chat-send-icon" src={chat_icon} onClick={this.sendMessage}/>
                </div>
            </div>
            <div className="talk-social">
                <img src={chat_slider} className={"talk-slider-action " + this.state.toggleContainer.socialSlider } onClick={this.onToggleContainer}/>
                <div className={"talk-social-container " + this.state.toggleContainer.socialContainer }>
                    {
                        <Profile {...this.props.profile.profile}/>
                    }
                    <div className="social-list">
                        {
                            this.props.profile.friends.map((friend) => (
                                <Profile {...friend}/>
                            ))
                        }
                    </div>
                    <div className="social-search">
                        <input type="text" className="social-search-input" placeholder="search friend..."/>
                        <img src={search_icon} className="social-search-icon"/>
                    </div>
                </div>
            </div>
        </div>
    )
}