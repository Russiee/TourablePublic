# Web

[![Build Status](https://travis-ci.com/philefstat/touring.svg?token=xRmb9YdhmsBgxRxc5wxz&branch=master)](https://travis-ci.com/philefstat/touring)

To run this site:

```npm install```

```gulp```

```open http://localhost:3000/```

To push to heroku:

```git remote add heroku-ws https://git.heroku.com/touring-ws.git```

from the main directory:

```git subtree push --prefix web heroku-ws master```

and if force is required:

```git push heroku-ws `git subtree split --prefix web master`:master --force```

