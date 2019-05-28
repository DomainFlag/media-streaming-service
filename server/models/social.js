const mongoose = require('mongoose');
const {ObjectId} = mongoose.Schema.Types;
const {ReplySchema} = require('./reply');

let ChatSchema = new mongoose.Schema({
    creator: {
        type : ObjectId,
        ref : 'User'
    },
    group: [ { type : ObjectId, ref : 'User' } ],
    history: [ReplySchema]
});

let Chat = mongoose.model('Chat', ChatSchema);

module.exports = {Chat, ChatSchema};