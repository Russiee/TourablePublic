var gulp = require('gulp');
var nodemon = require('gulp-nodemon');
var mocha = require('gulp-mocha');
var util = require('util');


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
    return gulp.src(['tests/route-tests.js'], { read: false })
        .pipe(mocha({ reporter: 'spec' }))
        .on('error', util.log);
});