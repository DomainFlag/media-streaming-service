let RequestifyCollector = require("./utils/requestify-collector");
let express = require("express");
let request = require("request");
let app = express();

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

app.get("/bad", function(req, res) {
    res.send("Error 404: File not Found :(");
});

app.use(function(req, res, next) {
    if(req.method === "OPTIONS") {
        res.setHeader("Access-Control-Allow-Headers", "Method, Origin, X-Requested-With," +
            " Content-Type," +
            " Accept");
        res.setHeader("Content-Type", "application/json");
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT");

        res.status(200).end();
    } else {
        //Do latter
        res.send("Ops, bad.");
    }
});

app.listen(process.env.PORT || 8080);


