export const data = {
    posts: [
        {
            id: 1,
            title: 'Post 1',
            text: 'Codeforces Round',
            comments: [
                {
                    id: 1,
                    author: 'User1',
                    text: 'Great post!'
                },
                {
                    id: 2,
                    author: 'User2',
                    text: 'I agree!'
                }
            ]
        },
        {
            id: 2,
            title: 'Post 2',
            text: '2099 year',
            comments: [
                {
                    id: 1,
                    author: 'User3',
                    text: 'Interesting!'
                }
            ]
        },
        {
            id: 3,
            title: 'Post 3',
            text: 'ITMO CT',
            comments: []
        },
        {
            id: 4,
            title: 'Post 4',
            text: 'abracadabra',
            comments: []
        },
    ],
    users: [
        {
            id: 1,
            login: 'pavladog',
            name: 'Vlad Rozhko'
        },
        {
            id: 2,
            login: 'slovechkin',
            name: 'Slava Rybin'
        },
        {
            id: 3,
            login: 'ghstfc',
            name: 'Max Merkulov'
        },
    ],

    comments: {
        1: {id: 1, userId: 1, postId: 4, text: "Thanks, tourist"},
        2: {id: 2, userId: 5, postId: 4, text: "You can read more on Wikipedia"},
        3: {id: 3, userId: 1, postId: 4, text: "Great!"},
        4: {id: 4, userId: 1, postId: 5, text: "Welcome to the round."},
        5: {id: 5, userId: 7, postId: 5, text: "Can Div.1 take part unofficially?"},
        6: {id: 6, userId: 5, postId: 5, text: "I hope"},
        8: {id: 8, userId: 11, postId: 8, text: "Please, write about TopCoder SRM 737"},
        9: {id: 9, userId: 7, postId: 8, text: "I'll do"}
    }
}