# API

To run this api:

```npm install```

```gulp```

```open http://localhost:3000/api```

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

