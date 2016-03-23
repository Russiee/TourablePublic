var config = {
    api_url: function() {
        if (process.env.NODE_ENV === 'production')
            return 'https://touring-api.herokuapp.com/api/';
        else
            return 'localhost:3000/api/';
    },
    database: function() {
        if (process.env.NODE_ENV === 'production') {
            return {
                appID: "touring",
                masterKey: "yF85llv84OI0NV41ieaHU7PM0oyRCMLT",
                serverURL: "https://touring-db.herokuapp.com/parse"
            }
        } else if (process.env.NODE_ENV === 'staging') {
            return {
                appID: "touring-testing",
                masterKey: "B9pOdZStXFqj48739yOO0B64MTtbv9Tf",
                serverURL: "https://touring-db-testing.herokuapp.com/parse"
            };
        } else {
            return {
                appID: "touring-testing",
                masterKey: "B9pOdZStXFqj48739yOO0B64MTtbv9Tf",
                serverURL: "https://touring-db-testing.herokuapp.com/parse"
//                serverURL: "http://localhost:1337/parse"
            };
        }
    }
};

module.exports = config;

