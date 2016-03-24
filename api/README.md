# API
Dependencies required to run this API:

* node: https://github.com/nodejs/node
* parse: https://github.com/ParsePlatform/Parse-SDK-JS
* node-cache: https://github.com/tcs-de/nodecache
* morgan: github.com/expressjs/morgan
* express: github.com/expressjs/express
* body-parser: github.com/expressjs/body-parser
* async: github.com/caolan/async
* gulp: github.com/gulpjs/gulp
* gulp-istanbul: github.com/SBoudrias/gulp-istanbul
* gulp-nodemon: github.com/JacksonGariety/gulp-nodemon
* gulp-strip-debug: github.com/sindresorhus/gulp-strip-debug
* gulp-mocha: github.com/sindresorhus/gulp-mocha
* mocha: github.com/mochajs/mocha
* should: github.com/shouldjs/should.js
* supertest: github.com/visionmedia/supertest

See package.json for module version details.


To build this API:

```npm install```

To run this API:

```gulp```

```open http://localhost:3000/api```

To test this API:

```gulp test```

To push to heroku:

```git remote add heroku https://git.heroku.com/touring-api.git```

To push to the heroku testing server:

```git remote add heroku-api-testing https://git.heroku.com/touring-api-testing.git```

from the main directory:

```git subtree push --prefix api heroku master```

or:

```git subtree push --prefix api heroku-api-testing master```

and if force is required:

```git push heroku `git subtree split --prefix api master`:master --force```

or:

```git push heroku-api-testing `git subtree split --prefix api master`:master --force```

