import React from "react"
import {Component} from "react"
import {connect} from "react-redux"
import {withRouter} from "react-router"

import Form from "../Form/Form";

import "./style.sass"
import AmusingAuth from "../AmusingAuth/PlayfullAuth";
import {ACTIONS} from "../../../reducers/auth";
import ReactDOM from "react-dom";

class Auth extends Component {
    constructor(props) {
        super(props);

        this.type = props.location.pathname.replace(/(.*)\/(\w+)$/, "$2");
    }

    componentDidMount = () => {
        ReactDOM.findDOMNode(this).parentNode.className = "non-extendable";
    };

    render = () => (
        <div className="authentication">
            <div className="authentication-container">
                <AmusingAuth />
            </div>
            <div className="authentication-container">
                <Form auth={this.props.auth} type={this.type}/>
            </div>
        </div>
    )
}


const mapDispatchToProps = (dispatch, ownProps) => ({
    auth : (type, body) => {
        dispatch(ACTIONS.USER_AUTH(ownProps.history, type, body));

        ownProps.history.push("/main");
    }
});

export default connect(null, mapDispatchToProps)(withRouter(Auth));