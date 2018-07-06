class UriBuilder {
    constructor() {
        this.uri = null;
        this.scheme = null;
        this.authority = null;
        this.path = [];
        this.queryParameters = [];

    }

    setAuthority = (authority) => {
        this.authority = authority;

        return this;
    };

    setScheme = (scheme) => {
        this.scheme = scheme;

        return this;
    };

    appendPath = (newSegment) => {
        this.path.push(newSegment);

        return this;
    };

    appendQueryParameter = (obj) => {
        this.queryParameters.push(obj);

        return this;
    };

    build = () => {
        this.uri = this.scheme + "://" +
            this.authority + "/" +
            this.path.reduce((acc, currPath) => acc + currPath + "/", "") + "?" +
            this.queryParameters.map((obj) => Object.keys(obj)
                .map((key) => key.toLowerCase() + "=" + obj[key]))
                .join("&");

        return this.uri;
    };
}

export default UriBuilder;