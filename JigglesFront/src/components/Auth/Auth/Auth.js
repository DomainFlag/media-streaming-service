import React from "react"
import {Component} from "react"
import {connect} from "react-redux"

import Form from "../Form/Form";

import "./style.sass"
import AmusingAuth from "../AmusingAuth/PlayfullAuth";
import {ACTIONS} from "../../../reducers/auth";

class Auth extends Component {
    constructor(props) {
        super(props);
    }

    render = () => (
        <div className="authentication">
            <div className="authentication-container">
                <AmusingAuth />
            </div>
            <div className="authentication-container">
                <Form login={this.props.login}/>
            </div>
        </div>
    )
}


const mapDispatchToProps = (dispatch, ownProps) => ({
    login : (body) => {
        dispatch(ACTIONS.USER_AUTH(ownProps.history, body));
    }
});

export default connect(null, mapDispatchToProps)(Auth);