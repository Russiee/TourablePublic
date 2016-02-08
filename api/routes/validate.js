var validate = {

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
	}
}

module.exports = validate;
