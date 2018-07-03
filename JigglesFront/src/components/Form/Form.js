import React from "react"
import {Component} from "react"

import "./style.sass"
import logo from "./../../resources/assets/logo.svg"
import Button from "../Button/Button";
import Input from "../Input/Input";
import Checkbox from "../Checkbox/Checkbox";

export default class Form extends Component {
    constructor(props) {
        super(props);
    }

    render = () => (
        <div className="form">
            <div className="form-header">
                <img src={logo} className="form-header-welcoming"/>
            </div>
            <div className="form-content">
                <div className="form-content-social-auth">

                </div>
                <div className="form-content-container">
                    <Input {...{label : "E-mail", value : "E-mail..."}}/>
                    <Input {...{label : "Password", value : "Password..."}}/>
                    <Input {...{label : "Repeat Password", value : "Repeat Password..."}}/>
                </div>
            </div>
            <div className="form-action">
                <Checkbox {...{label : "Remember Me"}}/>
                <Button value="submit" selectable={null}/>
            </div>
        </div>
    )
}