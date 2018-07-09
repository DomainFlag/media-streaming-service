import React from "react"
import {Component} from "react"

import "./style.sass"
import social_profile from "./../../../resources/icons/account.svg"
import cancel from "./../../../resources/icons/cancel.svg"
import chat_menu from "./../../../resources/icons/chat-menu.svg"

class Profile extends Component {
    constructor(props) {
        super(props);
    }

    render = () => (
        <div className="social-profile" onClick={this.props.onClick}>
            <img className="social-profile-acc" src={social_profile} />
            <div className="social-profile-details">
                <p className="social-profile-name">{this.props.name}</p>
                <p className="social-profile-active">{this.props.date}</p>
            </div>
        </div>
    );
}

class Chat extends Component {
    constructor(props) {
        super(props);

        this.state = {
            messages: this.props.profile.messages
        }
    }

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
                name: this.props.profile.name,
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
        <div className="talk-container">
            <div className="chat-header">
                <img className="chat-menu" src={chat_menu} onClick={this.props.onToggleContainer}/>
                <div className="chat-recipient">
                    <p className="chat-recipient-name">{this.props.profile.name}</p>
                </div>
                <img className="chat-close" src={cancel} />
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
            <div className="chat-action">
                <div className="chat-input" ref={(input) => this.input = input} onFocus={this.onFocus} onBlur={this.onBlur} onChange={this.onChange} onKeyDown={this.onAddMessage} contentEditable="true">
                    {this.props.placeholder}
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

export default class Social extends Component {
    constructor(props) {
        super(props);

        this.state = {
            toggleContainer: null
        }
    }

    onToggleContainer = (friend) => {
        this.setState((prevState) => {
            if(prevState.toggleContainer)
                return {
                    toggleContainer: null
                };
            else {
                return {
                    toggleContainer: friend
                };
            }
        });
    };

    render = () => (
        <div className="talk">
            {
                this.state.toggleContainer ? (
                    <Chat onToggleContainer={this.onToggleContainer} profile={this.state.toggleContainer} placeholder="Message here..."/>
                ) : (
                    <div className="talk-container">
                        <div className="chat-header">
                            <div className="chat-recipient">
                                <p className="chat-recipient-name">All contacts</p>
                            </div>
                            <img className="chat-close" src={cancel} />
                        </div>
                        <div className="chat-body">
                            {
                                this.props.friends.map((friend) => (
                                    <Profile {...friend} onClick={this.onToggleContainer.bind(this, friend)}/>
                                ))
                            }
                        </div>
                        <div className="chat-action">
                            <input type="text" className="social-search-input" placeholder="Search friend..."/>
                        </div>
                    </div>
                )
            }
        </div>
    )
}