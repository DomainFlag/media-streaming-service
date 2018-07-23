import {combineReducers} from "redux"
import main from "./main"
import { routerReducer } from 'react-router-redux'

export default combineReducers({
    main,
    routing: routerReducer
})