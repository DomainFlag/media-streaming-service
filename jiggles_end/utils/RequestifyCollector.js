const request = require("request");
const requestPromise = require("request-promise");
const querystring = require('querystring');

const CONSTANTS = require("./Constants");
const UriBuilder = require("./UriBuilder");

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
            .setScheme(CONSTANTS.SCHEME)
            .setAuthority(CONSTANTS.AUTHORITY)
            .appendPath(CONSTANTS.API_SECURITY)
            .appendPath(CONSTANTS.TOKEN)
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
            .setScheme(CONSTANTS.SCHEME)
            .setAuthority(CONSTANTS.AUTHORITY_DEV)
            .appendPath(CONSTANTS.API_ACCESS)
            .appendPath(CONSTANTS.SEARCH)
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
            .setScheme(CONSTANTS.SCHEME)
            .setAuthority(CONSTANTS.AUTHORITY_DEV)
            .appendPath(CONSTANTS.API_ACCESS)
            .appendPath(type)
            .appendPath(id)
            .appendPath(cake)
            .appendQueryParameter({"country" : CONSTANTS.COUNTRY})
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

module.exports = (() => new RequestifyCollector())();