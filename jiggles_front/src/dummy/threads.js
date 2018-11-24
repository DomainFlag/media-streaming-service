let replies = [
    {
        id : 1,
        parent : 0,
        depth : 0,
        likes: [],
        name: "Rick Malvin",
        content : {
            "text" : "I suppose the concert will be bonkers"
        },
        header : {
            "avatar" : "./tests/account.svg",
            "date" : (new Date((Date.now()-Math.random()*50000+25000))).toDateString()
        }
    }, {
        id : 2,
        parent : 0,
        depth : 0,
        likes: [],
        name: "Terry Luke",
        content : {
            "text" : "No shit, I won't miss this gig, yeah!"
        },
        header : {
            "avatar" : "./tests/account.svg",
            "date" : (new Date((Date.now()-Math.random()*50000+25000))).toDateString()
        }
    }, {
        id : 3,
        parent : 2,
        depth : 1,
        likes: [],
        name: "Jerry Man",
        content : {
            "text" : "What is the price for the concert ticket??"
        },
        header : {
            "avatar" : "./tests/account.svg",
            "date" : (new Date((Date.now()-Math.random()*50000+25000))).toDateString()
        }
    }, {
        id : 4,
        parent: 3,
        depth : 2,
        likes: [],
        name: "Joe Davin",
        content : {
            "text" : "I missed once, not gonna miss it twice"
        },
        header : {
            "avatar" : "./tests/account.svg",
            "date" : (new Date((Date.now()-Math.random()*50000+25000))).toDateString()
        }
    }, {
        id : 5,
        parent : 1,
        depth : 1,
        likes: [],
        name: "Rick Malvin",
        content : {
            "text" : "Yeahhhh"
        },
        header : {
            "avatar" : "./tests/account.svg",
            "date" : (new Date((Date.now()-Math.random()*50000+25000))).toDateString()
        }
    }, {
        id : 7,
        parent : 5,
        depth : 2,
        likes: [],
        name: "Luke Skywalker",
        content : {
            "text" : "Let's go yeah"
        },
        header : {
            "avatar" : "./tests/account.svg",
            "date" : (new Date((Date.now()-Math.random()*50000+25000))).toDateString()
        }
    }, {
        id : 6,
        parent : 1,
        depth : 1,
        likes: [],
        name: "Rick Malvin",
        content : {
            "text" : "Oh, Hi Mark!"
        },
        header : {
            "avatar" : "./tests/account.svg",
            "date" : (new Date((Date.now()-Math.random()*50000+25000))).toDateString()
        }
    }
];

const threads = [
    {
        id: 0,
        caption: "./tests/000.jpg",
        content: "Interpol concert on 21 July, book your tickets here.",
        created_by: "John Doe",
        votes: 25,
        replies: replies,
        created_when: "08/07/2018"
    }, {
        id: 1,
        caption: "./tests/bloc_party.jpg",
        content: "Bloc Party album reissue on 31 July, pre-order right now, go for it right now.",
        created_by: "Jane Doe",
        votes: 13,
        replies: [],
        created_when: "02/07/2017"
    }
];

export default threads;