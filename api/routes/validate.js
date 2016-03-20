var validate = {
    //checks stuff is there and of correct type
	validateInput: function (input, expectedInput) {
        var validInput = true;
        for (var prop in expectedInput) {
            expectedInput[prop];
            if (input[prop] === undefined || input[prop] === null) {
                console.log("Error: missing property: " + prop);
                validInput = false;
            } else if (typeof input[prop] !== typeof expectedInput[prop]) {
                console.log("Error: Incorrect property type: " + prop);
                validInput = false;
            }
        }
        if (validInput)
            return true;
        else
            return false;
	},   
    //removes properties we dont expect
    parseData: function (input, expectedInput) {
        var data = input;
        for (var prop in data) {
            if (!expectedInput.hasOwnProperty(prop))
                delete data[prop];
        }
        return data;
    }
}

module.exports = validate;
