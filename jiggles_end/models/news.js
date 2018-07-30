const mongoose = require('mongoose');
const {ObjectId} = mongoose.Schema.Types;

let NewsSchema = new mongoose.Schema({
    author: ObjectId,
    header: {
        type: String,
        require: true
    },
    caption: {
        type: String
    }
});

let NewsSeed = () => {
    News.insertMany([{
        author : null,
        header: "Vance Joy Performs Mini-Set, Including 'Riptide,' on 'Kimmel': Watch",
        caption: "https://after5detroit.com/wp-content/uploads/2018/03/32-29-768x512-5.jpg"
    }, {
        author : null,
        header: "Nine Inch Nails release “Metal” live video with Gary Numan: Watch",
        caption: "https://consequenceofsound.files.wordpress.com/2018/07/nine-inch-nails-gary-numan-metal-las-vegas.png?w=1614"
    }, {
        author : null,
        header: "Ben Stiller’s high school band, Capital Punishment, to reissue 1982 LP, share Muzak Anonymous”: Stream",
        caption: "https://consequenceofsound.files.wordpress.com/2018/07/capital-punishment-roadkill-reissue-ben-stiller-muzak-anonymous.png"
    }, {
        author : null,
        header: "Rage Against the Machine send cease and desist to Farage Against the Machine podcast",
        caption: "https://consequenceofsound.files.wordpress.com/2018/07/rage-against-the-machine-versus-farage-against-the-machine.png?w=1614"
    }], function(err) {
        console.error(err);
    });
};

let News = mongoose.model('News', NewsSchema);

module.exports = {News, NewsSchema, NewsSeed};