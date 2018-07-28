import React from "react"
import {Component} from "react"
import {connect} from "react-redux"
import {withRouter} from "react-router"

import Form from "../Form/Form";

import "./style.sass"
import AmusingAuth from "../AmusingAuth/PlayfullAuth";
import {ACTIONS} from "../../../reducers/auth";
import ReactDOM from "react-dom";
import CONSTANTS from "./../../../utils/Constants";

class Auth extends Component {
    constructor(props) {
        super(props);
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
                <Form auth={this.props.auth}/>
            </div>
        </div>
    )
}


const mapDispatchToProps = (dispatch) => ({
    auth : (type, body) => dispatch(ACTIONS.USER_AUTH_LOGGING(type, body))
});

export default withRouter(connect(null, mapDispatchToProps)(Auth));