import React from 'react';
import {render} from 'react-dom';
import Welcome from "./components/Welcome/Welcome"

import './style.sass';
import Authentication from "./components/Authentication/Welcome";

render(
    <Authentication/>,
    document.getElementById("root")
);
