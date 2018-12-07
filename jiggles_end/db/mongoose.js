const _ = require("lodash");
const mongoose = require('mongoose');

mongoose.Promise = global.Promise;
mongoose.connect(process.env.MONGODB_URI)
    .then(() => {
        console.log("Successfully connected to the database");
    })
    .catch((err) => {
        console.log("Error: " + err.toString());
    });

["News", "Release"].forEach((modelName) => {
    let modelSeedName = modelName + "Seed";

    let dbObj = _.pick(require("./../models/" + modelName.toLowerCase()), [ modelName, modelSeedName]);

    dbObj[modelName].find({}).then((documents) => {
        console.log(documents.length);
        if(documents.length === 0)
            dbObj[modelSeedName]();
    })
});

module.exports = {mongoose};
