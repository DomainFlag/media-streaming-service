let { database } = require('./app_config');

Object.keys(database).forEach((key) => {
    process.env[key] = database[key];
});