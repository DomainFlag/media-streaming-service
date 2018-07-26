const mongoose = require('mongoose');
const {ReplySchema} = require('./reply');

let ChatSchema = new mongoose.Schema({
    creator: mongoose.Schema.Types.ObjectId,
    group: [mongoose.Schema.Types.ObjectId],
    history: [ReplySchema]
});

let Chat = mongoose.model('Chat', ChatSchema);

module.exports = {Chat, ChatSchema};