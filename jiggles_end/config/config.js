let { DATABASE, SERVER } = require('./app_config');

Object.keys(DATABASE).forEach((key) => {
    process.env[key] = DATABASE[key];
});

Object.keys(SERVER).forEach((key) => {
    process.env[key] = SERVER[key];
});