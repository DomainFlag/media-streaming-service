const CONSTANTS = require("./utils/Constants");

const _ = require('lodash');
const fs = require('fs');
const url = require('url');
const path = require('path');
const express = require('express');
const bodyParser = require('body-parser');

require('./config/config');
require('./db/mongoose');

let { SERVER } = require("./config/app_config");

let RequestifyCollector = require("./utils/RequestifyCollector");
let resourcesCollector = require("./utils/ResourcesCollector");
let {authenticate} = require('./middleware/authenticate');

let {User} = require('./models/user');
let {Album} = require("./models/album");
let {Track} = require("./models/track");
let {Artist} = require("./models/artist");
let {Thread} = require("./models/thread");
let {Social} = require("./models/social");

const ENTERTAINMENT_TYPES = {
    track : ['id', 'name', 'uri', 'track_number', 'type', 'images', 'artists'],
    album : ['id', 'name', 'release_date', 'uri', 'type', 'images', 'artists'],
    artist : ['name', 'type', 'uri', 'id', 'genres']
};

const CONTENT_TYPES = {
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

let simpleResponseQuery = (res, status, message, type) => {
    let response = (type === "application/json") ? JSON.stringify(message) : message;

    res.set({
        "Content-Type" : type || "text/html"
    });
    
    res.status(status).send(response);
};

app.use(bodyParser.json({ limit : "10mb", extended : true }));

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
        res.set({
            "Access-Control-Allow-Origin" : "*",
            "Access-Control-Allow-Headers" : "X-Requested-With"
        });

        next();
    }
});

app.use('/resources', express.static(__dirname + '/resources'));

/* Public & Non-protected Routes */

app.post('/users', (req, res) => {
    let body = _.pick(req.body, ['email', 'name', 'password']);
    let user = new User(body);

    user.save().then(() => {
        return user.generateAuthToken();
    }).then((token) => {
        res.set({
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

    if(CONTENT_TYPES.hasOwnProperty(contentType)) {
        CONTENT_TYPES[contentType].resource.find({}, CONTENT_TYPES[contentType].projection)
            .then((data) => {
                res.set({
                    'Content-Type': 'application/json'
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

app.get(/^\/forum\/thread/, function(req, res) {
    Thread.find({})
        .populate('author')
        .populate('comments.author')
        .then((documents) => {
            simpleResponseQuery(res, 200, documents, "application/json");
        })
        .catch(() => {
            simpleResponseQuery(res, 401, "invalid request");
        });
});

app.get(/^\/account\/images/, function(req, res) {
    fs.readdir('./resources/users/default', null, (err, files) => {
        if(err) simpleResponseQuery(res, 401, "invalid request");
        else simpleResponseQuery(res, 200, files.map((file) => {
            return url.format({
                protocol: req.protocol,
                host: req.get('host'),
                pathname: 'resources/users/default/' + file,
            });
        }), "application/json");
    });
});

app.use(authenticate);

/* Private & Protected Routes */
function returnQueryPromise(req, searchBy, searchType) {
    return RequestifyCollector.querySearch(searchBy, searchType)
        .then((data) => {
            Object.keys(data).forEach((type) => {
                data[type].items.forEach((item) => {
                    item['favourite'] = false;

                    req.user.content[type].forEach((itemColl) => {
                        if(itemColl.id === item.id)
                            item['favourite'] = true;
                    })
                });
            });

            return data;
        })
}

app.get(/^\/query\/(artist|track|album|all)$/, function(req, res) {
    let searchBy = req.params[0];

    if(req.query.hasOwnProperty(searchBy)) {
        if(searchBy === CONSTANTS.ALL) {
            let promises = [];

            Object.keys(CONSTANTS.CONTENT_TYPES).forEach((key) => {
                let searchType = CONSTANTS.CONTENT_TYPES[key];

                promises.push(returnQueryPromise(req, req.query[searchBy], searchType));
            });

            Promise.all(promises).then((data) => {
                return data.reduce((container, content) => {
                    let contentType = Object.keys(content)[0];
                    container[contentType] = content[contentType];

                    return container;
                }, {});
            }).then((data) => {
                simpleResponseQuery(res, 200, data, 'application/json');
            })
        } else {
            returnQueryPromise(req, req.query[searchBy], searchBy)
                .then((data) => {
                    simpleResponseQuery(res, 200, data, 'application/json');
                });
        }
    } else res.send(null);
});

app.get(/^\/query\/(artists|tracks|albums)\/([0-9a-zA-Z]+)$/, function(req, res) {
    let searchBy = req.params[0];
    let searchId = req.params[1];

    RequestifyCollector.queryFetch(searchBy, searchId, "top-tracks")
        .then((data) => {
            simpleResponseQuery(res, 200, data, 'application/json');
        });
});

/* Thread Comments(POST, PUT, DELETE) */
app.post(/^\/forum\/thread\/comment/, function(req, res) {
    let thread_id = _.pick(req.body, ['_id']);

    let body = _.pick(req.body, ['parent', 'depth', 'content']);
    body['author'] = req.user._id;

    Thread.findOneAndUpdate({ _id : thread_id._id }, {
        $push : {
            comments : body
        }
    }, { upsert : true, new: true })
    .populate('author')
    .populate('comments.author')
    .exec((err, raw) => {
        if(err) simpleResponseQuery(res, 401, "couldn't create new message" + err.toString());
        else simpleResponseQuery(res, 200, raw, "application/json");
    });
});

app.put(/^\/forum\/thread\/comment/, function(req, res) {
    let thread_id = _.pick(req.body, ['_id']);

    let body = _.pick(req.body, ['parent', 'depth', 'content']);

    Thread.update({ _id : thread_id, "comments.author" : req.user._id },
        body.reduce((acc, value) => {
            let obj = {};
            obj["comments.$." + value] = value;

            return {...acc, ...obj };
        }, {}), (err, raw) => {
            if(err) simpleResponseQuery(res, 401, "couldn't update message");
            else simpleResponseQuery(res, 200, raw, "application/json");
        });
});


app.delete(/^\/forum\/thread\/comment/, function(req, res) {
    let body = _.pick(req.body, ['threadID, commentID']);

    Thread.update({ _id : body.threadID, "comments.author" : req.user._id }, {
        $pull : {
            "comments.$._id" : body.commentID
        }
    }, (err) => {
        if(err) simpleResponseQuery(res, 401, "couldn't delete message");
        else simpleResponseQuery(res, 200, raw, "application/json");
    });
});

/* Thread (GET (public), POST, PUT, DELETE) */
app.post(/^\/forum\/thread/, function(req, res) {
    let body = _.pick(req.body, ['caption', 'content']);

    let type = "png";
    let base64Data = body.caption.replace(/^data:image\/(png|jpeg|svg\+xml|jpg);base64,/, function(val) {
        type = arguments[1].replace(/(\+\w+)/, "");
        return "";
    });

    let uri = resourcesCollector.reservePath() + `.${type}`;

    fs.writeFile(__dirname + `/${uri}`, base64Data, 'base64', (err) => {
        if(err) simpleResponseQuery(res, 401, "couldn't create thread " + err.toString());
        else {
            Thread.create({ caption : uri, content : body.content, author : req.user._id}, (err, raw) => {
                raw.author = req.user;

                if(err) simpleResponseQuery(res, 401, "couldn't delete message");
                else simpleResponseQuery(res, 200, raw, "application/json");
            });
        }
    });
});

app.put(/^\/forum\/thread/, function(req, res) {
    let body = _.pick(req.body, [ '_id', 'caption', 'content']);

    let type, base64Data;
    new Promise((resolve, reject) => {
        if(body.caption) {
            type = "png";
            base64Data = body.caption.replace(/^data:image\/(png|jpeg|svg\+xml|jpg);base64,/, function(val) {
                type = arguments[1].replace(/(\+\w+)/, "");
                return "";
            });

            Thread.findOne({ _id : body._id, author : req.user._id}).then((thread) => {
                if(body.caption.match(thread.caption))
                    reject(thread);
                else resolve(thread);
            });
        }
    }).then((thread) => {
        fs.unlink(`./${thread.caption}`, (err) => {
            if(err) simpleResponseQuery(res, 401, "couldn't remove your old caption");
            else {
                resourcesCollector.freePath(thread.caption);
                let uri = `${resourcesCollector.reservePath()}.${type}`;

                fs.writeFile(`/${uri}`, base64Data, 'base64', (err) => {
                    if(err) simpleResponseQuery(res, 401, "couldn't create thread " + err.toString());
                    else {
                        resourcesCollector.freePath(thread.caption);
                        thread.set({ caption : uri, content : body.content});
                        thread.save().then((raw) => {
                            simpleResponseQuery(res, 200, thread, "application/json");
                        }).catch((err) => {
                            simpleResponseQuery(res, 401, "couldn't update the thread");
                        });
                    }
                });
            }
        })
    }).catch((thread) => {
        thread.set({content : body.content});
        thread.save().then((raw) => {
            simpleResponseQuery(res, 200, thread, "application/json");
        }).catch((err) => {
            simpleResponseQuery(res, 401, "couldn't update the thread");
        });
    });
});

app.delete(/^\/forum\/thread/, function(req, res) {
    let body = _.pick(req.body, [ '_id']);

    Thread.findOne({ _id : body._id, author : req.user._id  }).then((thread) => {
        fs.unlink(`./${thread.caption}`, (err) => {
            if(err) simpleResponseQuery(res, 401, "couldn't remove your old caption");
            else {
                resourcesCollector.freePath(thread.caption);

                Thread.remove({ _id : body._id, author : req.user._id }, (err, doc) => {
                    if(err) simpleResponseQuery(res, 401, "couldn't delete message");
                    else simpleResponseQuery(res, 200, body, "application/json");
                });
            }
        })
    });
});

/* Collection content(GET, POST, DELETE) */
app.get(/\/users\/collection/, (req, res) => {
    let contentType = req.params[0];

    simpleResponseQuery(res, 200, req.user.content[contentType], "application/json");
});

app.post(/\/users\/collection\/(track|album|artist)/, (req, res) => {
    let contentType = req.params[0];
    let body = _.pick(req.body, ENTERTAINMENT_TYPES[contentType]);

    req.user.content[contentType+'s'].push(body);
    req.user.save().then((result) => {
        simpleResponseQuery(res, 200, { user : req.user }, "application/json");
    }).catch((error) => {
        console.log(error);
        simpleResponseQuery(res, 401, "couldn't save content");
    });
});

app.delete(/\/users\/collection\/(track|album|artist)/, (req, res) => {
    let contentType = req.params[0];
    let body = _.pick(req.body, ENTERTAINMENT_TYPES[contentType]);

    req.user.content[contentType + 's'] = req.user.content[contentType + 's'].filter((content) => {
        return content.id !== body.id;
    });

    req.user.save().then(() => {
        simpleResponseQuery(res, 200, "successfully deleted");
    }).catch(() => {
        simpleResponseQuery(res, 401, "couldn't delete content");
    });
});

/* News/Releases content(GET (public), POST, PUT, DELETE) */
app.post(/^\/main\/(news|releases)$/, function(req, res) {
    let contentType = req.params[0];
    let body = _.pick(req.body, CONTENT_TYPES[contentType].projection.split(" "));

    if((req.user.type === "critic" && contentType === "releases") ||
        (req.user.type === "author" && contentType === "news")) {
        CONTENT_TYPES[contentType].resource.create(body, (err, raw) => {
            if(err) simpleResponseQuery(res, 401, `couldn't compose ${contentType}`);
            else simpleResponseQuery(res, 200, raw, "application/json");
        })
    } else {
        simpleResponseQuery(res, 200, "Not authorized");
    }
});

app.put(/^\/main\/(news|releases)$/, function(req, res) {
    let contentType = req.params[0];
    let body = _.pick(req.body, CONTENT_TYPES[contentType].projection.split(" "));

    if(((req.user.type === "critic" && contentType === "releases") ||
        (req.user.type === "author" && contentType === "news"))) {
        CONTENT_TYPES[contentType].update({
            _id : body._id,
            author: req.user._id
        }, body, (err, raw) => {
            if(err) simpleResponseQuery(res, 401, `couldn't edit ${contentType}`);
            else simpleResponseQuery(res, 200, raw, "application/json");
        })
    } else {
        simpleResponseQuery(res, 200, "Not authorized");
    }
});

app.delete(/^\/main\/(news|releases)$/, function(req, res) {
    let contentType = req.params[0];
    let body = _.pick(req.body, CONTENT_TYPES[contentType].projection.split(" "));

    if(((req.user.type === "critic" && contentType === "releases") ||
        (req.user.type === "author" && contentType === "news"))) {
        CONTENT_TYPES[contentType].remove({
            author: req.user._id,
            _id : body._id
        }, (err) => {
            if(err) simpleResponseQuery(res, 401, `couldn't delete ${contentType}`);
            else simpleResponseQuery(res, 200, `successfully deleted ${contentType}`);
        })
    } else {
        simpleResponseQuery(res, 200, "Not authorized");
    }
});

/* Social chat(GET, POST, PUT, DELETE) */
app.get('/users/me/social', (req, res) => {
    Social.find({
        $or : [{
            creator : req.user._id
        }, {
            group : {
                $elemMatch : {
                    _id : req.user._id
                }
            }
        }]
    }).then((documents) => {
        let social = [];

        documents.forEach((document) => {
            social.push(new Promise((resolve, reject) => {
                let users = [];

                document.group.forEach((user) => {
                    users.push(
                        User.find({
                            _id : user._id
                        }, "name").then((userName) => ({
                            ...user, userName
                        }))
                    );
                });

                Promise.all(users).then((users) => {
                    resolve(users);
                }).catch((err) => {
                    reject(err);
                })
            }));
        });

        Promise.all(social).then((data) => {
            simpleResponseQuery(res, 200, data, "application/json");
        }).catch(() => {
            simpleResponseQuery(res, 401, "couldn't get the social");
        });
    });
    res.send(req.user);
});

app.get('/users/me', (req, res) => {
    res.send(req.user);
});

app.delete('/users/me/token', (req, res) => {
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

app.listen(process.env.PORT || SERVER.PORT, () => {
    console.log(`Started up at port ${process.env.PORT || SERVER.PORT}`);
}).on('error', function(err) {
    console.log(err);
});