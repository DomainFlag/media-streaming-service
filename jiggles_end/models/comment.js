const mongoose = require('mongoose');

let CommentSchema = new mongoose.Schema({
    user: mongoose.Schema.Types.ObjectId,
    parent: {
        type: Number,
        default: 0,
        min: 0
    },
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