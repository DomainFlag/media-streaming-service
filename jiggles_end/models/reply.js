const mongoose = require('mongoose');

let ReplySchema = new mongoose.Schema({
    author: mongoose.Schema.Types.ObjectId,
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