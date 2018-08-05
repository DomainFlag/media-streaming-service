const mongoose = require('mongoose');

let ArtistSchema = new mongoose.Schema({
    id : {
        type: String,
        required: true
    },
    name: {
        type: String,
        required: true
    },
    type: {
        type: String,
        default: "artist"
    },
    uri : {
        type: String,
        required: true
    },
    favourite : {
        type : Boolean,
        default : true
    },
    genres: [String]
});

let Artist = mongoose.model('Artist', ArtistSchema);

module.exports = {Artist, ArtistSchema};