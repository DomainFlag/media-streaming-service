const mongoose = require('mongoose');
const ObjectId = mongoose.Schema.Types.ObjectId;

let QueueSchema = new mongoose.Schema({
    title: {
        type: String,
        required: false
    },
    leader: { type : ObjectId, ref : 'User' },
    friends: [ { type : ObjectId, ref : 'User' } ],
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