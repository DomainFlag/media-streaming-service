const _ = require('lodash');
const express = require('express');
const bodyParser = require('body-parser');

require('./config/config');
require('./db/mongoose');

let RequestifyCollector = require("./utils/RequestifyCollector");
let {authenticate} = require('./middleware/authenticate');

let {User} = require('./models/user');
let {Album} = require("./models/album");
let {Track} = require("./models/track");
let {Artist} = require("./models/artist");
let {Fireplace} = require("./models/fireplace");

const CONTENT_TYPE = {
    news : {
        resource : require("./models/news").News,
        projection : "id author header caption"
    },
    releases  : {
        resource : require("./models/release").Release,
        projection : "id title artist url score reviews"
    }
};

let app = express();

let validatedSavedResponse = (res) => {
    res.set({
        "Content-Type" : "text/html"
    });
    res.status(200).send("Saved successfully");
};

app.use(bodyParser.json());

app.use(function(req, res, next) {
    if(req.method === "OPTIONS") {
        res.set({
            "Access-Control-Allow-Origin" : "*",
            "Access-Control-Allow-Headers" : "Method, X-Auth," +
            " Content-Type, Access-Control-Expose-Headers",
            "Access-Control-Allow-Methods" : "GET, POST, PUT, DELETE"
        });

        res.status(200).end();
    } else {
        next();
    }
});

/* Public & Non-protected Routes */

app.post('/users', (req, res) => {
    let body = _.pick(req.body, ['email', 'password']);
    let user = new User(body);

    user.save().then(() => {
        return user.generateAuthToken();
    }).then((token) => {
        res.set({
            "Access-Control-Allow-Origin" : "*",
            'Content-Type' : 'application/json',
            'Access-Control-Expose-Headers' : 'X-Auth',
            'X-Auth' : token
        });

        res.send(JSON.stringify(user));
    }).catch((e) => {
        res.status(400).send(e);
    })
});

app.post('/users/login', (req, res) => {
    let body = _.pick(req.body, ['email', 'password']);

    User.findByCredentials(body.email, body.password).then((user) => {
        return user.generateAuthToken().then((token) => {
            res.set({
                "Access-Control-Allow-Origin" : "*",
                'Content-Type' : 'application/json',
                'Access-Control-Expose-Headers' : 'X-Auth',
                'X-Auth' : token
            });

            res.send(JSON.stringify(user));
        });
    }).catch((e) => {
        res.status(400).send();
    });
});

app.get(/^\/main\/(news|releases)$/, function(req, res) {
    let contentType = req.params[0];

    if(CONTENT_TYPE.hasOwnProperty(contentType)) {
        CONTENT_TYPE[contentType].resource.find({}, CONTENT_TYPE[contentType].projection)
            .then((data) => {
                res.set({
                    'Content-Type': 'application/json',
                    'Access-Control-Allow-Origin': '*'
                });

                res.send(JSON.stringify(data));
            })
            .catch(() => {
                res.status(401).send();
            });
    } else {
        res.status(401).send("Invalid request");
    }
});


app.use(authenticate);

/* Private & Protected Routes */

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

app.post(/\/user\/save\/(track|album|artist)/, (req, res) => {
    let savedType = req.params[1];

    switch(savedType) {
        case "track" : {
            let body = _.pick(req.body, ['id', 'name', 'releaseDate', 'uri', 'trackNumber', 'type', 'images', 'artists']);
            let track = new Track(body);

            track.save().then(validatedSavedResponse.bind(this, res)).catch(() => {
                res.status(401).send();
            });

            break;
        }
        case "album" : {
            let body = _.pick(req.body, ['id', 'name', 'releaseDate', 'uri', 'type', 'images', 'artists']);
            let album = new Album(body);

            album.save().then(validatedSavedResponse.bind(this, res)).catch(() => {
                res.status(401).send();
            });

            break;
        }
        case "artist" : {
            let body = _.pick(req.body, ['name', 'type', 'uri', 'id', 'genres']);
            let artist = new Artist(body);

            artist.save().then(validatedSavedResponse.bind(this, res)).catch(() => {
                res.status(401).send();
            });
            break;
        }
        default: {
            res.status(401).send();
        }
    }
});

app.post(/\/user\/compose\/(review|news)/, (req, res) => {
    if(req.user.type !== "author")
        res.send(401).send("Not authorized");

    let typeComposition = req.params[2];

    let body, composition;
    body['author'] = req.user.id;

    if(typeComposition === "review") {
        body = _.pick(req.body, ['content', 'score', 'urlReference']);
        composition = new Review(body);
    } else {
        body = _.pick(req.body, ['header', 'caption']);
        composition = new News(body);
    }

    composition.save().then(validatedSavedResponse.bind(this, res)).catch(() => {
        res.status(401).send();
    });
});

app.post(/\/user\/create\/fireplace/, (req, res) => {
    let body = _.pick(req.body, ['caption', 'content']);
    body['author'] = req.user.id;

    let fireplace = new Fireplace(body);

    fireplace.save().then(validatedSavedResponse.bind(this, res)).catch(() => {
        res.status(401).send();
    });
});

app.get('/users/me', (req, res) => {
    res.send(req.user);
});

app.delete('/users/me/token', (req, res) => {
    res.setHeader("Access-Control-Allow-Origin", "*");
    req.user.removeToken(req.token).then(() => {
        res.status(200).send();
    }, () => {
        console.log("nope");
        res.status(401).send();
    });
});

app.use(function(req, res) {
    res.status(401).send("No resource found");
});

app.listen(process.env.PORT || 8000, () => {
    console.log(`Started up at port ${process.env.PORT}`);
}).on('error', function(err) {
    console.log(err);
});

module.exports = {app};
