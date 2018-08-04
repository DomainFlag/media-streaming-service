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
        required: true
    },
    trackNumber : {
        type: Number,
        required: true,
        default: 1
    },
    type: {
        type: String,
        default: "track"
    },
    images: [ImageSchema],
    artist: [ArtistSchema]
});

let Track = mongoose.model('Track', TrackSchema);

module.exports = {Track, TrackSchema};