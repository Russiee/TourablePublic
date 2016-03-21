//validate module object 
//contains the two functions used to verify input format correctness
var validate = {
    
    //validateInput function
    //returns a Boolean
    //required param(s) in input: input to be added to database, required format of input for object to be added
    //optional param(s) in input: none
	validateInput: function (input, expectedInput) {
        //initially creates Boolean variable
        var validInput = true;
        
        //iterate over the properties in the expected input
        for (var prop in expectedInput) {
            //adds expected input properties to array of properties to look through
            expectedInput[prop];
            
            //checks if the array of properties in the input to be added matches the required properties of the expected input.
            //if input does not have the correct properties included
            if (input[prop] === undefined || input[prop] === null) {
                console.log("Error: missing property: " + prop);
                //sets the return Boolean to false
                validInput = false;
            } 
            //if input does not have the correct property types included
            else if (typeof input[prop] !== typeof expectedInput[prop]) {
                console.log("Error: Incorrect property type: " + prop);
                //sets the return Boolean to false
                validInput = false;
            }
        }
        //if the input has the correct properties and property types as denoted by the expected input
        if (validInput)
            //sends back true, informing user that the input is correctly formatted to te requirements set by the expected input
            return true;
        else
            //sends back false, informing the user the input is not correctly formatted
            return false;
	},   
    
    //parseData function
    //returns the data without unexpected properties
    //required param(s) in input: input to be formatted, required format of input for object to be added
    //optional param(s) in input: none
    parseData: function (input, expectedInput) {
        //takes data from params
        var data = input;
        //checks the properties in the input data
        for (var prop in data) {
            //checks if the input's properties match the expected input's properties 
            if (!expectedInput.hasOwnProperty(prop))
                //if there is a property that does not match the required input format, remove the property from the input
                delete data[prop];
        }
        //return the data with the unexpected properties removed
        return data;
    }
}

//export this module
module.exports = validate;
