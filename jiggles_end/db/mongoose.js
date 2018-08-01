const _ = require("lodash");
const mongoose = require('mongoose');

mongoose.Promise = global.Promise;
mongoose.connection.openUri(process.env.MONGODB_URI);

["News", "Release", "Thread"].forEach((modelName) => {
    let modelSeedName = modelName + "Seed";

    let dbObj = _.pick(require("./../models/" + modelName.toLowerCase()), [ modelName, modelSeedName]);

    dbObj[modelName].find({}).then((documents) => {
        if(documents.length === 0)
            dbObj[modelSeedName]();
    })
});

module.exports = {mongoose};
