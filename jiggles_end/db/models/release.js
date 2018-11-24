const mongoose = require('mongoose');
const {ObjectId} = mongoose.Schema.Types;
const {ReviewSchema} = require("./review");

let ReleaseSchema = new mongoose.Schema({
    author: ObjectId,
    title: {
        type: String,
        require: true
    },
    artist: {
        type: String,
        require: true
    },
    url: {
        type: String,
        require: true
    },
    score: {
        type: Number,
        require: true,
        min: 0,
        max: 100
    },
    reviews: [ReviewSchema]
});

let ReleaseSeed = () => {
    Release.insertMany([
        {
            title: "Melodrama",
            artist: "Lorde",
            url: "https://images-na.ssl-images-amazon.com/images/I/51T8dh9dDML._SY355_.jpg",
            score: 91,
            reviews: [{
                author: "Tiny Mix Tapes",
                content: "Melodrama overwhelms me. It reaches me at that weird and fragile center.",
                score: 100,
                url: "https://www.tinymixtapes.com/music-review/lorde-melodrama"
            }, {
                author: "Sputnikmusic",
                content: "A bold and colorful magnum opus that marks an almost unbeatable " +
                    "personal milestone for Lorde.",
                score: 90,
                url: "https://www.sputnikmusic.com/review/74058/Lorde-Melodrama/"
            }]
        }, {
            title: "Plastic Beach",
            artist: "Gorillaz",
            url: "https://upload.wikimedia.org/wikipedia/en/thumb/d/d1/Plasticbeach452.jpg/220px-Plasticbeach452.jpg",
            score: 82,
            reviews: [{
                author: "No Ripcord",
                content: "The whole thing works beautifully, more with each listen.",
                score: 100,
                url: "http://www.noripcord.com/reviews/music/gorillaz/plastic-beach"
            }, {
                author: "Prefix Magazine",
                content: "He never panders to them; instead, Plastic Beach's guest vocals are " +
                    "anchored by Albarn's own melodic flair. His falsettoed ennui shines through, and " +
                    "the songs are loaded with Albarn's pet sounds.",
                score: 85,
                url: "http://www.prefixmag.com/reviews/plastic-beach/36442/"
            }]
        }, {
            title: "In Colour",
            artist: "Jamie XX",
            url: "https://upload.wikimedia.org/wikipedia/commons/c/c2/Jamie_xx_-_In_Colour.png",
            score: 87,
            reviews: [
                {
                    author: "Consequence of Sound",
                    content: "After one listen or 10, In Colour reflects brightly, a phenomenally" +
                        " poised and universally approachable solo debut.",
                    score: 100,
                    url: "https://pitchfork.com/reviews/albums/florence-and-the-machine-high-as-hope/"
                }, {
                    author: "The Guardian",
                    content: "Although In Colour flirts with being overly tasteful, it usually" +
                        " manages to stay just the right side of strange--much like the xx themselves.",
                    score: 80,
                    url: "https://www.theguardian.com/music/2015/may/28/jamie-xx-in-colour-review"
                }
            ]
        }, {
            title: "High As Hope",
            artist: "Florence + the Machine",
            url: "https://upload.wikimedia.org/wikipedia/en/thumb/6/61/HighAsHope.png/220px-HighAsHope.png",
            score: 75,
            reviews: [{
                author: "Paste Magazine",
                content: "Mountain-moving sound, with Welch’s vocals the main source of power.",
                score: 83,
                url: "https://www.pastemagazine.com/articles/2018/07/florence-the-machine-high-as-hope-review.html"
            }, {
                author: "Pitchfork",
                content: "How small, how beige, how disappointing.",
                score: 57,
                url: "https://pitchfork.com/reviews/albums/florence-and-the-machine-high-as-hope/"
            }]
        }, {
            title: "Flower Boy",
            artist: "Tyler, the Creator",
            url: "https://upload.wikimedia.org/wikipedia/en/c/c3/Tyler%2C_the_Creator_-_Flower_Boy.png",
            score: 84,
            reviews: [
                {
                    author: "Pretty Much Amazing",
                    content: "Flower Boy has elevated Tyler closer to the line. An unexpected move" +
                        " to be sure, but no less impressive whatsoever.",
                    score: 83,
                    url: "https://prettymuchamazing.com/reviews/tyler-the-creatpr-flower-boy/"
                }, {
                    author: "Consequence of Sound",
                    content: "It’s more of the same. It seems to be needing something more. An extra spark of interest.",
                    score: 50,
                    url: "https://consequenceofsound.net/2017/07/album-review-tyler-the-creator-scum-fuck-flower-boy/"
                }
            ]
        }, {
            title: "I'm all ears",
            artist: "Let's Eat Grandma",
            url: "https://media.pitchfork.com/photos/5ae0a4228bec5b23c213a2f5/1:1/w_320/Let%E2%80%99s%20Eat%20Grandma:%20I'm%20All%20Ears.jpg",
            score: 86,
            reviews: [
                {
                    author: "The Guardian",
                    content: "I’m All Ears is about abandoning fear and leaping boldly towards desire. It is remarkable.",
                    score: 100,
                    url: "https://www.theguardian.com/music/2018/jun/29/lets-eat-grandma-review-bold-intense-pop-that-gets-under-the-skin"
                }, {
                    author: "Under The Radar",
                    content: "Those two [\"Cool & Collected\" and \"Donnie Darko\"] towers loom over" +
                        " an otherwise sterile pop album, which lacks the hidden barbs and hazy ambiguity that drew Let's Eat Grandma's cult following.",
                    score: 65,
                    url: "http://www.undertheradarmag.com/reviews/lets_eat_grandma_im_all_ears/"
                }
            ]
        }, {
            title: "Oil of Every Pearl's Un-Insides",
            artist: "SOPHIE",
            url: "http://cdn.albumoftheyear.org/album/113120-oil-of-every-pearls-un-insides-1.jpg",
            score: 86,
            reviews: [
                {
                    author: "Tiny Mix Tapes",
                    content: "Fractured, inconsistent, broken, torn, OIL OF EVERY PEARL’S UN-INSIDES" +
                        " aims toward the stylistic grandness of High Pop, and in that inconsistence, it achieves it. ... It’s incredible.",
                    score: 100,
                    url: "http://redirect.viglink.com/?format=go&jsonp=vglnk_153278880746914&key=52746e8db87abed1fd620f26f47292ce&libId=jk5it26w0101r4s7000DLnn94q92o&loc=http%3A%2F%2Fwww.metacritic.com%2Fmusic%2Foil-of-every-pearls-un-insides%2Fsophie&v=1&out=https%3A%2F%2Fwww.tinymixtapes.com%2Fmusic-review%2Fsophie-oil-every-pearls-un-insides&ref=http%3A%2F%2Fwww.metacritic.com%2Fmusic%2Fim-all-ears%2Flets-eat-grandma&title=Oil%20Of%20Every%20Pearl%27s%20Un-Insides%20by%20SOPHIE%20Reviews%20and%20Tracks%20-%20Metacritic&txt=Read%20full%20review"
                }, {
                    author: "Exclaim",
                    content: "This is the kind of music that, in 20 years, we may look back on as a" +
                        " pivotal point in changing the trajectory of the pop music sound.",
                    score: 90,
                    url: "https://exclaim.ca/music/article/sophie-oil_of_every_pearls_un-insides"
                }
            ]
        }], function (err) {
        if (err)
            console.error(err);
    });

};

let Release = mongoose.model('Release', ReleaseSchema);

module.exports = {Release, ReleaseSchema, ReleaseSeed};