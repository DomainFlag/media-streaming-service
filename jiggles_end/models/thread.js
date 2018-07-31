const mongoose = require('mongoose');
const ObjectId = mongoose.Types.ObjectId;
const {CommentSchema} = require("./comment");

let ThreadSchema = new mongoose.Schema({
    author : mongoose.Schema.Types.ObjectId,
    caption : {
        type: String,
        unique: true
    },
    content : {
        type: String,
        require: true
    },
    votes : {
        type: Number,
        default: 0,
        min: 0
    },
    comments: [CommentSchema]
});

let Thread = mongoose.model('Thread', ThreadSchema);

// Thread.insertMany([
//     {
//         author : new ObjectId('4edd40c86762e0fb12000910'),
//         caption : "resources/forum/threads/000.jpg",
//         content : "Interpol concert on 21 July, book your tickets here.",
//         votes : 5,
//         comments: [{
//             author : new ObjectId('4edd40c86762e0fb12000900'),
//             parent : null,
//             depth : 0,
//             likes : 6,
//             content : {
//                 text : "I suppose the concert will be bonkers",
//                 caption : "resources/forum/comments/000.svg"
//             }
//         }, {
//             author : new ObjectId('4edd40c86762e0fb12000901'),
//             parent : null,
//             depth : 0,
//             likes : 5,
//             content : {
//                 text : "No way, I won't miss this gig, yeah!",
//                 caption : "resources/forum/comments/001.svg"
//             }
//         }, {
//             author : new ObjectId('4edd40c86762e0fb12000902'),
//             parent : new ObjectId('4edd40c86762e0fb12000931'),
//             depth : 1,
//             likes : 0,
//             content : {
//                 text : "What is the price for the concert ticket??",
//                 caption : "resources/forum/comments/000.svg"
//             }
//         }]
//     }, {
//         author : new ObjectId('4edd40c86762e0fb12000911'),
//         caption: "resources/forum/threads/001.jpg",
//         content : "Bloc Party album reissue on 31 July, pre-order right now, go for it right now.",
//         votes : 13,
//         comments: [{
//             author : new ObjectId('4edd40c86762e0fb12000900'),
//             parent : null,
//             depth : 0,
//             likes : 13,
//             content : {
//                 text : "I missed once, not gonna miss it twice",
//                 caption : "resources/forum/comments/000.svg"
//             }
//         }, {
//             author : new ObjectId('4edd40c86762e0fb12000901'),
//             parent : new ObjectId('4edd40c86762e0fb12000933'),
//             depth : 1,
//             likes : 2,
//             content : {
//                 text : "Yeah me too, I agree.",
//                 caption : "resources/forum/comments/001.svg"
//             }
//         }, {
//             author : new ObjectId('4edd40c86762e0fb12000902'),
//             parent : new ObjectId('4edd40c86762e0fb12000934'),
//             depth : 2,
//             likes : 2,
//             content : {
//                 text : "Let's go together, don't you want?.",
//                 caption : "resources/forum/comments/001.svg"
//             }
//         }]
//     }, {
//         author : new ObjectId('4edd40c86762e0fb12000912'),
//         caption: null,
//         content : "Test",
//         votes : 5,
//         comments: [{
//             author : new ObjectId('4edd40c86762e0fb12000900'),
//             parent : null,
//             depth : 0,
//             likes : 3,
//             content : {
//                 text : "Comment testing?",
//                 caption : "resources/forum/comments/001.svg"
//             }
//         }]
//     }
// ]);

module.exports = {Thread, ThreadSchema};