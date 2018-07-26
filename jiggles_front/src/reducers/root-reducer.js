import {combineReducers} from "redux"
import auth from "./auth"
import entertainer from "./entertainer"
import { routerReducer } from 'react-router-redux'

export default combineReducers({
    auth,
    entertainer,
    routing: routerReducer
})