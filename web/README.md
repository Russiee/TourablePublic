# Web

[![Build Status](https://travis-ci.com/philefstat/touring.svg?token=xRmb9YdhmsBgxRxc5wxz&branch=master)](https://travis-ci.com/philefstat/touring)

# API

Dependencies required to run this API:

* "async": "^1.5.2",
* "body-parser": "^1.14.2",
* "express": "^4.13.4",
* "morgan": "^1.7.0",
* "node-cache": "^3.1.0",
* "parse": "^1.7.0"

* "gulp": "^3.9.1",
* "gulp-debug": "^2.1.2",
* "gulp-inject": "^4.0.0",
* "gulp-istanbul": "^0.10.3",
* "gulp-jshint": "^2.0.0",
* "gulp-mocha": "^2.2.0",
* "gulp-nodemon": "^2.0.6",
* "gulp-strip-debug": "^1.1.0",
* "jshint": "^2.9.1",
* "jshint-stylish": "^2.1.0",
* "main-bower-files": "^2.11.1",
* "should": "^8.3.0",
* "supertest": "^1.2.0"

* "angular": "^1.5.0",
* "angular-ui-router": "^0.2.18",
* "bootstrap": "^3.3.6",
* "jquery": "^2.2.0",
* "parse": "^1.7.0",
* "pouchdb": "^5.3.1",
* "ng-file-upload": "^12.0.4",
* "moment": "^2.12.0"


To run this site:

```npm install```

(you might have to use sudo depending on your config)

```gulp```

```open http://localhost:3000/```

To run the tests:

```gulp test```

To push to heroku:

```git remote add heroku-ws https://git.heroku.com/touring-ws.git```

from the root directory:

```git subtree push --prefix web heroku-ws master```

and if force is required:

```git push heroku-ws `git subtree split --prefix web master`:master --force```

