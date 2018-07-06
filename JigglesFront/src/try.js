const querystring = require('querystring');
const https = require('https');

const AUTHORITY = "https://accounts.spotify.com/api/token";
const grant_type = "client_credentials";

const ClientID = "YOUR&CLIENT&ID";
const ClientServer = "YOUR&SERVER&ID";
const Authorization = Buffer.from(ClientID + ":" + ClientServer).toString('base64');

const postData = querystring.stringify({
    'grant_type' : 'client_credentials'
});

const options = {
    hostname: 'accounts.spotify.com',
    path: "/api/token",
    port: 443,
    method: 'POST',
    headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        'Content-Length': postData.length,
        'Authorization' : 'Basic ' + Authorization,
        'Allow-Control-Allow-Origin' : '*'
    }
};

const req = https.request(options, (res) => {
    console.log('statusCode:', res.statusCode);
    console.log('headers:', res.headers);

    res.on('data', (d) => {
        process.stdout.write(d);
    });
});

req.on('error', (e) => {
    console.error(e);
});

req.write(postData);
req.end();