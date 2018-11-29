const mongoose = require('mongoose');
const {ArtistSchema} = require("./artist");
const {ImageSchema} = require("./image");

let AlbumSchema = new mongoose.Schema({
    id : {
        type: String,
        required: true
    },
    name : {
        type: String,
        required: true
    },
    uri : {
        type: String,
        required: true,
        unique : true
    },
    type : {
        type: String,
        default: "album"
    },
    favourite : {
        type : Boolean,
        default : true
    },
    images: [ImageSchema],
    artists: [ArtistSchema]
});

let Album = mongoose.model('Album', AlbumSchema);

module.exports = {Album, AlbumSchema};