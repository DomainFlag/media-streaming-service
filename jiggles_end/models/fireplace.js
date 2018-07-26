const mongoose = require('mongoose');
const {CommentSchema} = require("./comment");

let FireplaceSchema = new mongoose.Schema({
    user: mongoose.Schema.Types.ObjectId,
    caption: {
        type: String,
        unique: true
    },
    content: {
        type: String,
        require: true
    },
    votes: {
        type: Number,
        default: 0,
        min: 0
    },
    comments: [CommentSchema]
});

let Fireplace = mongoose.model('Fireplace', FireplaceSchema);

module.exports = {Fireplace};