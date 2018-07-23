require('./config/config');

const _ = require('lodash');
const express = require('express');
const bodyParser = require('body-parser');

let {mongoose} = require('./db/mongoose');
let RequestifyCollector = require("./utils/requestify-collector");
let {User} = require('./models/user');
let {authenticate} = require('./middleware/authenticate');

let app = express();

app.use(bodyParser.json());

app.use(function(req, res, next) {
    if(req.method === "OPTIONS") {
        res.setHeader("Access-Control-Allow-Headers", "Method, Origin, X-Auth, X-Requested-With," +
            " Content-Type, Accept, Access-Control-Expose-Headers");
        res.setHeader("Content-Type", "application/json, text/html");
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");

        res.status(200).end();
    } else {
        next();
    }
});

app.post('/users', (req, res) => {
    let body = _.pick(req.body, ['email', 'password']);
    let user = new User(body);

    user.save().then(() => {
        return user.generateAuthToken();
    }).then((token) => {
        res.set({
            'Access-Control-Allow-Origin' : "*",
            'Content-Type' : 'application/json',
            'Access-Control-Expose-Headers' : 'X-Auth',
            'X-Auth' : token
        });
        res.send(user);
    }).catch((e) => {
        res.status(400).send(e);
    })
});

app.get("/bad", function(req, res) {
    res.send("Error 404: File not Found :(");
});

app.use(authenticate);

app.get(/^\/(artist|track|album)$/, function(req, res) {
    let searchBy = req.params[0];

    if(req.query.hasOwnProperty(searchBy))
        RequestifyCollector.querySearch(req.query[searchBy], searchBy)
            .then((data) => {
                res.set({
                    'Content-Type': 'application/json',
                    'Access-Control-Allow-Origin': '*'
                });

                res.send(JSON.stringify(data));
            });
    else res.send(null);
});

app.get(/^\/(artists|tracks|albums)\/([0-9a-zA-Z]+)$/, function(req, res) {
    let searchBy = req.params[0];
    let searchId = req.params[1];

    RequestifyCollector.queryFetch(searchBy, searchId, "top-tracks")
        .then((data) => {
            res.set({
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': '*'
            });

            res.send(JSON.stringify(data));
        });
});

app.post('/users/login', (req, res) => {
    let body = _.pick(req.body, ['email', 'password']);

    User.findByCredentials(body.email, body.password).then((user) => {
        return user.generateAuthToken().then((token) => {
            res.header('x-auth', token).send(user);
        });
    }).catch((e) => {
        res.status(400).send();
    });
});

app.get('/users/me', (req, res) => {
  res.send(req.user);
});

app.delete('/users/me/token', (req, res) => {
  req.user.removeToken(req.token).then(() => {
    res.status(200).send();
  }, () => {
    res.status(400).send();
  });
});

app.listen(process.env.PORT || 3000, () => {
  console.log(`Started up at port ${process.env.PORT}`);
});

module.exports = {app};
