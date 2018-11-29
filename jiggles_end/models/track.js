const mongoose = require('mongoose');
const {ArtistSchema} = require("./artist");
const {ImageSchema} = require("./image");

let TrackSchema = new mongoose.Schema({
    id: {
        type: String,
        required: true
    },
    name: {
        type: String,
        required: true
    },
    uri : {
        type: String,
        required: true,
        unique: true
    },
    trackNumber : {
        type: Number,
        default: 1
    },
    type: {
        type: String,
        default: "track"
    },
    favourite : {
        type: Boolean,
        default: true
    },
    images: [ImageSchema],
    artist: [ArtistSchema]
});

let Track = mongoose.model('Track', TrackSchema);

module.exports = {Track, TrackSchema};