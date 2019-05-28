module.exports = (function() {
    return UriBuilder;
})();

function UriBuilder() {
    this.uri = null;
    this.scheme = null;
    this.authority = null;
    this.path = [];
    this.queryParameters = [];
}

UriBuilder.prototype.clear = function() {
    this.path.length = 0;
    this.queryParameters.length = 0;
};

UriBuilder.prototype.setAuthority = function(authority) {
    this.authority = authority;

    return this;
};

UriBuilder.prototype.setScheme = function(scheme) {
    this.scheme = scheme;

    return this;
};

UriBuilder.prototype.appendPath = function(newSegment) {
    if(newSegment !== null)
        this.path.push(newSegment);

    return this;
};

UriBuilder.prototype.appendQueryParameter = function(obj) {
    if(obj !== null)
        this.queryParameters.push(obj);

    return this;
};

UriBuilder.prototype.constructQueryParameters = function() {
    if(this.queryParameters.length !== 0)
        return "?" + this.queryParameters.map((obj) => Object.keys(obj)
            .map((key) => key.toLowerCase() + "=" + obj[key]))
            .join("&");

    return "";
};

UriBuilder.prototype.build = function() {
    this.uri = this.scheme + "://" +
        this.authority +
        this.path.reduce((acc, currPath) => acc + "/" + currPath , "") +
        this.constructQueryParameters();

    return this.uri;
};