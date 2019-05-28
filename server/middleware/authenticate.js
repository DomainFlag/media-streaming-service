let {User} = require('./../models/user');

let authenticate = (req, res, next) => {
    let token = req.header('x-auth');

    User.findByToken(token).then((user) => {
        if(!user) {
            return Promise.reject("error");
        }

        req.user = user;
        req.token = token;
        next();
    }).catch((e) => {
        res.status(401).send("You aren't signed in, sign up/in please");
    });
};

module.exports = {authenticate};
