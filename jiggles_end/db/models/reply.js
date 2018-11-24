const mongoose = require('mongoose');
const {ObjectId} = mongoose.Schema.Types;
const {LikeSchema} = require("./like");

let ReplySchema = new mongoose.Schema({
    author: { type : ObjectId, ref : 'User' },
    parent: { type : ObjectId, ref : 'User' },
    depth: {
        type: Number,
        default: 0,
        min: 0
    },
    content: {
        type: String
    },
    likes : {
        type : Map,
        of : String
    }
});

let Reply = mongoose.model('Reply', ReplySchema);

module.exports = {Reply: Reply, ReplySchema: ReplySchema};