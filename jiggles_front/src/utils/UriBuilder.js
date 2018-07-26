class UriBuilder {
    constructor() {
        this.uri = null;
        this.scheme = null;
        this.authority = null;
        this.path = [];
        this.queryParameters = [];

    }

    clear = () => {
        this.path.length = 0;
        this.queryParameters.length = 0;
    };

    setAuthority = (authority) => {
        this.authority = authority.trim();

        return this;
    };

    setScheme = (scheme) => {
        this.scheme = scheme.trim();

        return this;
    };

    appendPath = (newSegment) => {
        if(newSegment !== null)
            this.path.push(newSegment.trim());

        return this;
    };

    appendQueryParameter = (obj) => {
        if(obj !== null)
            this.queryParameters.push(obj);

        return this;
    };

    constructQueryParameters = function() {
        if(this.queryParameters.length !== 0)
            return "?" + this.queryParameters.map((obj) => Object.keys(obj)
                .map((key) => key.toLowerCase() + "=" + obj[key]))
                .join("&");

        return "";
    };

    build = () => {
        this.uri = this.scheme + "://" +
            this.authority +
            this.path.reduce((acc, currPath) => acc + "/" + currPath , "") +
            this.constructQueryParameters();

        return this.uri;
    };
}

export default UriBuilder;