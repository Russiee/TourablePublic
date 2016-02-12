var config = {
	api_url: function() {
		if (process.env.NODE_ENV === 'production')
			return 'http://touring-api.herokuapp.com/api/'
		else
			return 'localhost:3000/api/'
	}
};

module.exports = config;
