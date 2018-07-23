const request = require("request");
const requestPromise = require("request-promise");
const querystring = require('querystring');

const UriBuilder = require("./uri-builder");

const SCHEME = "https";

const AUTHORITY = "accounts.spotify.com";
const AUTHORITY_DEV = "api.spotify.com";

const API_SECURITY = "api";
const TOKEN = "token";
const API_ACCESS = "v1";
const SEARCH = "search";
const grant_type = "client_credentials";
const COUNTRY = "US";

const ARTIST = "artist";
const ALBUM = "album";
const TRACK = "track";

const ARTISTS = "artists";
const TRACKS = "tracks";
const ALBUMS = "albums";

class RequestifyCollector {
    constructor() {
        this.setCollectorAuth();
        this.getToken();
    }

    setCollectorAuth() {
        const ClientID = "YOUR&KEY";
        const ClientServer = "YOUR&KEY";

        this.Authorization = Buffer.from(ClientID + ":" + ClientServer).toString('base64');
    };

    getToken() {
        let url = new UriBuilder()
            .setScheme(SCHEME)
            .setAuthority(AUTHORITY)
            .appendPath(API_SECURITY)
            .appendPath(TOKEN)
            .build();

        let options = {
            url: url,
            headers: {
                "Authorization" : ("Basic " + this.Authorization),
                "Content-Type" : "application/x-www-form-urlencoded"
            },
            body: querystring.stringify({
                'grant_type' : 'client_credentials'
            })
        };

        request.post({...options}, (err, response, body) => {
            if(!err && response.statusCode === 200) {
                let info = JSON.parse(body);

                this.parseTokenResponse(info);
            }
        });
    };

    parseTokenResponse(response) {
        if(response.hasOwnProperty("token_type") && response.hasOwnProperty("access_token")) {
            if(response["token_type"] === "Bearer" && response["access_token"] !== "") {
                this.token = response["access_token"];

                setTimeout(() => {
                    this.getToken();
                }, response["expires_in"] * 1000);
            }
        }
    };

    querySearch(query, type) {
        let url = new UriBuilder()
            .setScheme(SCHEME)
            .setAuthority(AUTHORITY_DEV)
            .appendPath(API_ACCESS)
            .appendPath(SEARCH)
            .appendQueryParameter({"q" : query})
            .appendQueryParameter({"type" : type})
            .build();

        let options = {
            url: url,
            headers: {
                "Authorization" : "Bearer " + this.token,
                "Content-Type" : "application/json"
            },
            resolveWithFullResponse: true
        };

        console.log(this.token);
        return requestPromise.get({...options})
            .then((response) => {
                if(response.statusCode === 200) {
                    return JSON.parse(response.body);
                } else return Promise.reject("unsuccessful")
            })
            .catch(console.error);
    };

    queryFetch(type, id, cake = null) {
        let url = new UriBuilder()
            .setScheme(SCHEME)
            .setAuthority(AUTHORITY_DEV)
            .appendPath(API_ACCESS)
            .appendPath(type)
            .appendPath(id)
            .appendPath(cake)
            .appendQueryParameter({"country" : COUNTRY})
            .build();

        let options = {
            url: url,
            headers: {
                "Authorization" : "Bearer " + this.token,
                "Content-Type" : "application/json"
            },
            resolveWithFullResponse: true
        };

        return requestPromise.get({...options})
            .then((response) => {
                if(response.statusCode === 200) {
                    return JSON.parse(response.body);
                } else return Promise.reject("unsuccessful")
            })
            .catch(console.error);
    };
}

module.exports = (function() {
    return new RequestifyCollector();
})();