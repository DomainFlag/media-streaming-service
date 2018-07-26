const mongoose = require('mongoose');
const {ReviewSchema} = require("./review");

let ReleaseSchema = new mongoose.Schema({
    author: mongoose.Schema.Types.ObjectId,
    title: {
        type: String,
        require: true
    },
    artist: {
        type: String,
        require: true
    },
    url: {
        type: String,
        require: true
    },
    score: {
        type: Number,
        require: true,
        min: 0,
        max: 100
    },
    reviews: [ReviewSchema]
});

let Release = mongoose.model('Release', ReleaseSchema);

module.exports = {Release, ReleaseSchema};