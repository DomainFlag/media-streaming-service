import {combineReducers} from "redux"
import account from "./account"
import settings from "./settings"
import main from "./main"
import forum from "./forum"
import {routerReducer} from 'react-router-redux'

export default combineReducers({
    account,
    settings,
    main,
    forum,
    routing: routerReducer
})