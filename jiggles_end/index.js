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
let {Post} = require("./models/post");
let {Social} = require("./models/social");
let {Release} = require("./models/release");

const STORE_PROJECTION = {
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
        projection : "id title artist url uri score reviews"
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
app.post('/user', (req, res) => {
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

app.post('/user/login', (req, res) => {
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

app.get(/^\/feed\/thread/, function(req, res) {
    Thread.find({})
        .populate('author')
        .populate('replies.author')
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

                    req.user.store[type].forEach((itemStore) => {
                        if(itemStore.id === item.id)
                            item["favourite"] = true;
                    })
                });

                data[type] = data[type].items;
            });

            return data;
        })
}

app.get(/^\/query\/(artist|track|album|all)$/, function(req, res) {
    let searchBy = req.params[0];

    if(req.query.hasOwnProperty(searchBy)) {
        if(searchBy === CONSTANTS.ALL) {
            let promises = [];

            Object.keys(CONSTANTS.STORE_ITEMS).forEach((key) => {
                let searchType = CONSTANTS.STORE_ITEMS[key];

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

/****************************************************************/
/**
 * Like a thread's reply
 */
app.post(/^\/feed\/thread\/reply\/like/, function(req, res) {
    let body = _.pick(req.body, ["thread_id", "reply_id"]);

    Thread.findOneAndUpdate({ "_id" : body.thread_id, "replies._id" : body.reply_id }, {
        $set : {
            ["replies.$.likes.".concat(req.user._id.toString())] : true
        }
    }, { new: true })
        .populate("author")
        .populate("replies.author")
        .exec((err, raw) => {
            if(err) simpleResponseQuery(res, 401, "couldn't like thread's reply" + err.toString());
            else simpleResponseQuery(res, 200, raw, "application/json");
        });
});

/**
 * Unlike a thread's reply
 */
app.delete(/^\/feed\/thread\/reply\/like/, function(req, res) {
    let body = _.pick(req.body, ["thread_id", "reply_id"]);

    Thread.findOneAndUpdate({ "_id" : body.thread_id, "replies._id" : body.reply_id }, {
        $unset : {
            ["replies.$.likes.".concat(req.user._id.toString())] : true
        }
    }, { new: true })
        .populate("author")
        .populate("replies.author")
        .exec((err, raw) => {
            if(err) simpleResponseQuery(res, 401, "couldn't unlike thread's reply" + err.toString());
            else simpleResponseQuery(res, 200, raw, "application/json");
        });
});

/**
 * Reply to a thread
 */
app.post(/^\/feed\/thread\/reply/, function(req, res) {
    let thread = _.pick(req.body, ["thread_id"]);

    let body = _.pick(req.body, ["parent", "depth", "content"]);
    body['author'] = req.user._id;

    Thread.findOneAndUpdate({ "_id" : thread.thread_id }, {
        $push : {
            replies : body
        }
    }, { upsert : true, new: true })
    .populate('author')
    .populate('replies.author')
    .exec((err, raw) => {
        if(err) simpleResponseQuery(res, 401, "couldn't create new message" + err.toString());
        else simpleResponseQuery(res, 200, raw, "application/json");
    });
});

/**
 * Update a thread's reply
 */
app.put(/^\/feed\/thread\/reply/, function(req, res) {
    let thread = _.pick(req.body, ["thread_id"]);

    let body = _.pick(req.body, ["parent", "depth", "content"]);

    Thread.update({ "_id" : thread.thread_id, "replies.author" : req.user._id },
        body.reduce((acc, value) => {
            let obj = {};
            obj["replies.$." + value] = value;

            return {...acc, ...obj };
        }, {}), (err, raw) => {
            if(err) simpleResponseQuery(res, 401, "couldn't update message");
            else simpleResponseQuery(res, 200, raw, "application/json");
        });
});

/**
 * Delete a thread's reply
 */
app.delete(/^\/feed\/thread\/reply/, function(req, res) {
    let body = _.pick(req.body, ["thread_id", "reply_id"]);

    Thread.update({ "_id" : body.thread_id, "replies.author" : req.user._id }, {
        $pull : {
            "replies.$._id" : body.reply_id
        }
    }, (err) => {
        if(err) simpleResponseQuery(res, 401, "couldn't delete message");
        else simpleResponseQuery(res, 200, raw, "application/json");
    });
});

/**
 * Like a thread
 */
app.post(/^\/feed\/thread\/like/, function(req, res) {
    let body = _.pick(req.body, ['thread_id']);

    Thread.findOneAndUpdate({ "_id" : body.thread_id }, {
        $set : {
            ["likes.".concat(req.user._id.toString())] : true
        }
    }, { new: true })
        .populate("author")
        .populate("replies.author")
        .exec((err, raw) => {
            if(err) simpleResponseQuery(res, 401, "couldn't create new message" + err.toString());
            else simpleResponseQuery(res, 200, raw, "application/json");
        });
});

/**
 * Unlike a thread
 */
app.delete(/^\/feed\/thread\/like/, function(req, res) {
    let body = _.pick(req.body, ['thread_id']);

    Thread.findOneAndUpdate({ "_id" : body.thread_id }, {
        $unset : {
            ["likes.".concat(req.user._id.toString())] : true
        }
    }, { new: true })
        .populate("author")
        .populate("replies.author")
        .exec((err, raw) => {
            if(err) simpleResponseQuery(res, 401, "couldn't create new message" + err.toString());
            else simpleResponseQuery(res, 200, raw, "application/json");
        });
});

/****************************************************************/
/**
 * Create a thread
 */

app.post(/^\/feed\/thread/, (() => {
    let createThreadOpt = (req, res, uri, body) => {
        Thread.create({ caption : uri, content : body.content, author : req.user._id}, (err, raw) => {
            raw.author = req.user;

            if(err) simpleResponseQuery(res, 401, "couldn't delete message");
            else simpleResponseQuery(res, 200, raw, "application/json");
        });
    };

    return (req, res) => {
        let body = _.pick(req.body, ['caption', 'content']);

        if(body.caption) {
            let type = "png";
            let base64Data = body.caption.replace(/^data:image\/(png|jpeg|svg\+xml|jpg);base64,/, function(val) {
                type = arguments[1].replace(/(\+\w+)/, "");
                return "";
            });

            let uri = resourcesCollector.reservePath() + `.${type}`;

            fs.writeFile(__dirname + `/${uri}`, base64Data, 'base64', (err) => {
                if(err) simpleResponseQuery(res, 401, "couldn't create thread " + err.toString());
                else createThreadOpt(req, res, uri, body);
            });
        } else {
            createThreadOpt(req, res, "", body);
        }
    }
})());

/**
 * Update a thread
 */
app.put(/^\/feed\/thread/, function(req, res) {
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

/**
 * Delete a thread
 */
app.delete(/^\/feed\/thread/, function(req, res) {
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

/****************************************************************/
/**
 * Get post
 */
app.get(/^\/feed\/post/, function(req, res) {
    Post.find({})
        .populate('author')
        .populate('replies.author')
        .then((documents) => {
            simpleResponseQuery(res, 200, documents, "application/json");
        })
        .catch(() => {
            simpleResponseQuery(res, 401, "invalid request");
        });
});

/****************************************************************/
/**
 * Get user's store
 */
app.get(/^\/user\/store/, (req, res) => {
    let contentType = req.params[0];

    simpleResponseQuery(res, 200, req.user.store[contentType], "application/json");
});

/**
 * Save an item to the user's store
 */
app.post(/^\/user\/store/, (req, res) => {
    let body = _.pick(req.body, ["tracks", "albums", "artists"]);

    Object.keys(body).forEach((key) => {
        if(req.user.store.hasOwnProperty(key)) {
            body[key].forEach((item) => {
                req.user.store[key].push(item);
            });
        }
    });

    Post.create({ store : body, author : req.user._id}, (err, raw) => {
        if(err) console.log(err);
    });

    req.user.save().then((result) => {
        simpleResponseQuery(res, 200, result, "application/json");
    }).catch((error) => {
        simpleResponseQuery(res, 401, "couldn't update the store");
    });
});

/**
 * Delete an item from the user's store
 */
app.delete(/^\/user\/store/, (req, res) => {
    let body = _.pick(req.body, STORE_PROJECTION[type]);

    Object.keys(body.store).forEach((key) => {
       if(req.user.store.hasOwnProperty(key)) {
           req.user.store[key] = req.user.store[key].filter(item => {
               for(let i = 0; i < body.store[key].length; i++) {
                   if(item._id === body.store[key][i])
                       return false;
               }

               return true;
           });
       }
    });

    req.user.save().then(() => {
        simpleResponseQuery(res, 200, "successfully deleted item from the store");
    }).catch(() => {
        simpleResponseQuery(res, 401, "couldn't delete item from the store");
    });
});

/****************************************************************/
/**
 * Get fresh release
 */
app.get(/^\/main\/fresh$/, function(req, res) {
    Release.find({}, null, { limit : 1 })
        .then((data) => {
            res.set({
                'Content-Type': 'application/json'
            });

            res.send(JSON.stringify(data));
        })
        .catch(() => {
            res.status(401).send();
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

/****************************************************************/
app.post('/user/me/token', (req, res) => {
    let body = _.pick(req.body, ["token"]);

    RequestifyCollector.queryProfile(body.token)
        .then((data) => {
            simpleResponseQuery(res, 200, data, "application/json");
        })
        .catch((error) => {
            simpleResponseQuery(res, 200, "Not valid token: " + error.toString());
        });
});

app.get('/user/me', (req, res) => {
    res.send(req.user);
});

app.delete('/user/me/token', (req, res) => {
    req.user.removeToken(req.token).then(() => {
        res.status(200).send();
    }, () => {
        console.log("This operation can't be done, try again");
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