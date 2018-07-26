const mongoose = require('mongoose');

let NewsSchema = new mongoose.Schema({
    author: mongoose.Schema.Types.ObjectId,
    header: {
        type: String,
        require: true
    },
    caption: {
        type: String
    }
});

let News = mongoose.model('News', NewsSchema);

module.exports = {News, NewsSchema};