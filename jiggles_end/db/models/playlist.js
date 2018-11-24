const mongoose = require('mongoose');

let PlaylistSchema = new mongoose.Schema({
    name: {
        type: String,
        required: true
    },
    sharedWith: [ { type : ObjectId, ref : 'User' } ],
    tracks: [
        {
            trackNumber : {
                type: Number,
                required: true,
                default: 0
            },
            trackIdentifier : mongoose.Schema.Types.ObjectId
        }
    ],
    path: {
        type: String,
        required: true
    }
});

let Playlist = mongoose.model('Playlist', PlaylistSchema);

module.exports = {Playlist, PlaylistSchema};