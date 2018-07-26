const mongoose = require('mongoose');

let ReviewSchema = new mongoose.Schema({
    author: mongoose.Schema.Types.ObjectId,
    content: {
        type: String
    },
    score: {
        type: Number,
        default: 0,
        min: 0,
        max: 100
    },
    urlReference: {
        type: String,
        require: true
    }
});

let Review = mongoose.model('Review', ReviewSchema);

module.exports = {Review, ReviewSchema};