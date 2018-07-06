const getToken = (() => {
    const AUTHORITY = "https://accounts.spotify.com/api/token";
    const grant_type = "client_credentials";

    const ClientID = "YOUR&CLIENT&ID";
    const ClientServer = "YOUR&SERVER&ID";
    const Authorization = btoa(ClientID + ":" + ClientServer);

    let headers = new Headers();
    headers.append("Authorization", "Basic " + Authorization);
    headers.append("Content-Type", "application/x-www-form-urlencoded");
    headers.append("Allow-Control-Allow-Origin", "*");

    const data = {
        grant_type
    };

    return () => fetch(AUTHORITY, { method: "POST", headers, data, mode: "cors" });
})();


const queryArtist = (() => {
    const AUTHORITY = "https://api.spotify.com/v1/search?q=Interpol&type=artist";

    const Token = "YOUR&TOKEN";

    let headers = new Headers();
    headers.append("Authorization", "Bearer " + Token);
    headers.append("Content-Type", "application/json");

    return () => fetch(AUTHORITY, { method: "GET", headers });
})();

const queryFetch = (() => {
    const AUTHORITY = "https://api.spotify.com/v1/artists/3WaJSfKnzc65VDgmj2zU8B";

    const Token = "YOUR&TOKEN";

    let headers = new Headers();
    headers.append("Authorization", "Bearer " + Token);
    headers.append("Content-Type", "application/json");

    return () => fetch(AUTHORITY, { method: "GET", headers });
})();

export default queryFetch;