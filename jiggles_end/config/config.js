let { DATABASE } = require('./app_config');

Object.keys(DATABASE).forEach((key) => {
    process.env[key] = DATABASE[key];
});