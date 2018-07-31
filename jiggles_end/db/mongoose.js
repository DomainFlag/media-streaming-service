let {NewsSeed} = require("./../models/news");
let {ReleaseSeed} = require("./../models/release");

let mongoose = require('mongoose');

mongoose.Promise = global.Promise;
mongoose.connect(process.env.MONGODB_URI);

// NewsSeed();
// ReleaseSeed();

module.exports = {mongoose};
