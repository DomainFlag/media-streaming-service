const mongoose = require('mongoose');
const {ObjectId} = mongoose.Schema.Types;
const {ReplySchema} = require('./reply');

const {ArtistSchema} = require("./artist");
const {AlbumSchema} = require("./album");
const {TrackSchema} = require("./track");

let PostSchema = new mongoose.Schema({
    author : {
        type : ObjectId,
        ref : 'User'
    },
    store : {
        tracks : [TrackSchema],
        albums : [AlbumSchema],
        artists : [ArtistSchema]
    },
    content : {
        type : String,
        default : ""
    },
    likes : {
        type : Map,
        of : String,
        default : {}
    },
    replies: [ReplySchema]
});

let Post = mongoose.model('Post', PostSchema);

module.exports = {Post, PostSchema};