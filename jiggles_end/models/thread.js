const mongoose = require('mongoose');
const {ObjectId} = mongoose.Schema.Types;
const {ReplySchema} = require('./reply');

let ThreadSchema = new mongoose.Schema({
    author : {
        type : ObjectId,
        ref : 'User'
    },
    caption : {
        type :  String,
        default : ""
    },
    content : {
        type : String,
        require : true
    },
    likes : {
        type : Map,
        of : String,
        default : {}
    },
    replies: [ReplySchema]
});

let Thread = mongoose.model('Thread', ThreadSchema);

module.exports = {Thread, ThreadSchema};