const mongoose = require('mongoose');

let ArtistSchema = new mongoose.Schema({
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
    id : {
        type: String,
        required: true
    },
    genres: [String]
});

let Artist = mongoose.model('Artist', ArtistSchema);

module.exports = {Artist, ArtistSchema};