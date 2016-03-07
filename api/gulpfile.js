var gulp = require('gulp');
var nodemon = require('gulp-nodemon');
var mocha = require('gulp-mocha');

gulp.task('default', function() {
    nodemon({
        script: 'server.js',
        ext: 'js html', 
        env: { 'NODE_ENV': 'development' }
      })
});

gulp.task('tests', function () {
    return gulp.src(['tests/route-tests.js'], { read: false })
        .pipe(mocha({ reporter: 'spec' }))
        .on('error', util.log);
});