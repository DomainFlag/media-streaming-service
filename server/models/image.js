const mongoose = require('mongoose');

let ImageSchema = new mongoose.Schema({
    url: {
        type: String,
        required: true
    },
    height: {
        type: Number,
        required: true
    },
    width: {
        type: Number,
        required: true
    }
});

let Image = mongoose.model('Image', ImageSchema);

module.exports = {Image, ImageSchema};