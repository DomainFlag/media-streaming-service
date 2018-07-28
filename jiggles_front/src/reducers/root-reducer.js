import {combineReducers} from "redux"
import auth from "./auth"
import main from "./main"
import { routerReducer } from 'react-router-redux'

export default combineReducers({
    auth,
    main,
    routing: routerReducer
})