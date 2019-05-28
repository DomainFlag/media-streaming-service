import React from "react"
import {Component} from "react"
import {connect} from "react-redux"
import {withRouter} from "react-router"

import Form from "../Form/Form";

import "./style.sass"
import AmusingAuth from "../AmusingAuth/AmusingAuth";
import {ACTIONS} from "../../../reducers/account";
import ReactDOM from "react-dom";

class Auth extends Component {
    componentDidMount = () => {
        ReactDOM.findDOMNode(this).parentNode.className = "non-extendable";
    };

    render = () => (
        <div className="authentication">
            <div className="authentication-container">
                <AmusingAuth />
            </div>
            <div className="authentication-container">
                <Form auth={this.props.auth}/>
            </div>
        </div>
    )
}


const mapDispatchToProps = (dispatch) => ({
    auth : (type, body) => dispatch(ACTIONS.USER_AUTH_LOGGING(type, body))
});

export default withRouter(connect(null, mapDispatchToProps)(Auth));