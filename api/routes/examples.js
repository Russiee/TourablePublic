//require modules up here.

var examples = {
    //example POST route
    samplePOST: function(req, res) {

        var data = JSON.parse(JSON.stringify(req.body));

        if (data)
            res.send(200)
        else
            res.send(400)
    },

    sampleGET: function(req, res) {

        var data = {
            example: "data"
        }

        res.send(data);
    }
}

module.exports = examples;