import {combineReducers} from "redux"
import auth from "./auth"
import settings from "./settings"
import entertainer from "./entertainer"
import { routerReducer } from 'react-router-redux'

export default combineReducers({
    auth,
    settings,
    entertainer,
    routing: routerReducer
})