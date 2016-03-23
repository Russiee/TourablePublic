var gulp = require('gulp');
var nodemon = require('gulp-nodemon');
var mocha = require('gulp-mocha');
var util = require('util');
var istanbul = require('gulp-istanbul');
var stripDebug = require('gulp-strip-debug');


gulp.task('default', function() {
    nodemon({
        script: 'server.js',
        ext: 'js html',
        env: { 'NODE_ENV': 'development' }
    })
});

gulp.task('route-tests', function () {
    return gulp.src(['tests/route-tests.js'], { read: false })
        .pipe(mocha({ reporter: 'spec' }))
        .on('error', util.log);
});

gulp.task('validate-tests', function () {
    return gulp.src(['tests/validate-tests.js'], { read: false })
        .pipe(mocha({ reporter: 'spec' }))
        .on('error', util.log);
});

gulp.task('pre-test', function () {
  return gulp.src(['server.js', 'routes/*.js'])
    .pipe(stripDebug())
    // Covering files
    .pipe(istanbul())
    // Force `require` to return covered files
    .pipe(istanbul.hookRequire());
});

gulp.task('test', ['pre-test'], function () {
  return gulp.src(['tests/*.js'])
    .pipe(mocha())
    // Creating the reports after tests ran
    .pipe(istanbul.writeReports())
    // Enforce a coverage of at least 90%
    .pipe(istanbul.enforceThresholds({ thresholds: { global: 60 } }));
});
