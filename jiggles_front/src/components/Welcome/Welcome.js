import React from "react"
import {Component} from "react"
import {withRouter} from "react-router"
import {Link} from "react-router-dom"
import {connect} from "react-redux"

import logo from "../../resources/assets/logo-white.svg"
import background from "../../resources/background.jpg"
import Button from "../Components/Button/Button";
import ReactDOM from "react-dom";

export class Welcome extends Component {
    componentDidMount = () => {
        ReactDOM.findDOMNode(this).parentNode.className = "non-extendable";
    };

    onRedirectMain = () => {
        if(this.props.token !== null)
            this.props.history.push("/main");
        else this.props.history.push("/auth/signup");
    };

    render = () => (
        <div className="welcome">
            <img className="welcome-background" alt="" src={background}/>

            <div className="welcome-header">
                <div className="welcome-header-logo">
                    <Link to="\">
                        <img id="welcome-header-logo" alt="logo" src={logo}/>
                    </Link>
                </div>
                <div className="welcome-header-menu">
                    <div className="welcome-header-links">
                        <p className="welcome-header-links-item">Home</p>
                        <p className="welcome-header-links-item">About</p>
                        <p className="welcome-header-links-item">Contact</p>
                    </div>
                    <div className="welcome-header-authentication">
                        <Link to="/auth/signup">
                            <p className="welcome-header-authentication-auth">Sign up</p>
                        </Link>
                        <Link to="/auth/login">
                            <p className="welcome-header-authentication-auth">Login</p>
                        </Link>
                    </div>
                </div>
            </div>
            <div className="welcome-body">
                <div className="welcome-body-col">
                    <div className="welcome-body-interaction">
                        <p className="welcome-body-header">More then just a music collection.</p>
                        <p className="welcome-body-subheader">Listen.  Connect.  Jiggle.</p>
                        <Button value="Get started" onClick={this.onRedirectMain}/>
                    </div>
                </div>
                <div className="welcome-body-col">
                </div>
            </div>
        </div>
    )
}

const mapStateToProps = (state) => ({
   token : state.account.token
});

export default withRouter(connect(mapStateToProps, null)(Welcome));