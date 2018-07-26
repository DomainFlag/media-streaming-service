const mongoose = require('mongoose');

let QueueSchema = new mongoose.Schema({
    title: {
        type: String,
        required: false
    },
    moderator: mongoose.Schema.Types.ObjectId,
    friends: [mongoose.Schema.Types.ObjectId],
    position: {
        type: Number,
        default: 1
    },
    tracks: [
        {
            trackIdentifier : Schema.Types.ObjectId,
            trackNumber : {
                user: Schema.Types.ObjectId,
                type: Number,
                required: true,
                default: 1,
                min: 1
            }
        }
    ]
});

let Queue = mongoose.model('Queue', QueueSchema);

module.exports = {Queue, QueueSchema};