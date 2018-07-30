const mongoose = require('mongoose');
const {ObjectId} = mongoose.Schema.Types;

let CommentSchema = new mongoose.Schema({
    author: ObjectId,
    parent: ObjectId,
    depth: {
        type: Number,
        default: 0,
        min: 0
    },
    likes: {
        type: Number,
        default: 0,
        min: 0
    },
    content: {
        text: {
            type: String,
            require: true
        },
        caption: {
            type: String
        }
    }
});

let Comment = mongoose.model('Comment', CommentSchema);

module.exports = {Comment, CommentSchema};