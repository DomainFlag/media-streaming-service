const mongoose = require('mongoose');
const {ObjectId} = mongoose.Schema.Types;

let ReplySchema = new mongoose.Schema({
    author: ObjectId,
    body: {
        text: {
            type: String,
            minLength: 1,
        },
        caption: {
            type: String,
            trim: true
        }
    }
});

let Reply = mongoose.model('Reply', ReplySchema);

module.exports = {Reply, ReplySchema};