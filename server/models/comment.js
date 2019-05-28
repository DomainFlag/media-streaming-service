const mongoose = require('mongoose');
const {ObjectId} = mongoose.Schema.Types;

let CommentSchema = new mongoose.Schema({
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
    likes: {
        type: Number,
        default: 0,
        min: 0
    },
});

let Comment = mongoose.model('Comment', CommentSchema);

module.exports = {Comment, CommentSchema};