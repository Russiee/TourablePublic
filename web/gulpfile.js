var gulp = require('gulp');
var jshint = require('gulp-jshint');
var stylish = require('jshint-stylish');
var nodemon = require('gulp-nodemon');
var inject = require('gulp-inject');
var bowerFiles = require('main-bower-files');
var debug = require('gulp-debug');


gulp.task('default', ['inject', 'lint'], function() {
    nodemon({
        script: 'server.js',
        ext: 'js html css',
        env: { 'NODE_ENV': 'development' }
    });
});

gulp.task('lint', function() {
  return gulp.src('./client/app/**/*.js')
    .pipe(jshint())
    .pipe(jshint.reporter('jshint-stylish'));
});

gulp.task('inject', function () {
    var target = gulp.src('./client/index.html');
    var sources = gulp.src(bowerFiles({
        paths: {
            bowerDirectory: './client/assets/bower_components',
            bowerrc: './bowerrc',
            bowerJson: './bower.json'
        }
    }).concat(['./client/app/**/*.js', './client/app/**/*.css']), {read: false});

    return target.pipe(inject(sources))
        .pipe(gulp.dest('./client'));
});

var mocha = require('gulp-mocha');
var util = require('util');
var istanbul = require('gulp-istanbul');
var stripDebug = require('gulp-strip-debug');


//gulp.task('server-test', function () {
//    return gulp.src(['tests/route-tests.js'], { read: false })
//        .pipe(mocha({ reporter: 'spec' }))
//        .on('error', util.log);
//});
//
//gulp.task('validate-tests', function () {
//    return gulp.src(['tests/validate-tests.js'], { read: false })
//        .pipe(mocha({ reporter: 'spec' }))
//        .on('error', util.log);
//});
//
gulp.task('pre-test', function () {
  return gulp.src(['server.js','aws/aws.js'])
    .pipe(stripDebug())
    // Covering files
    .pipe(istanbul())
    // Force `require` to return covered files
    .pipe(istanbul.hookRequire());
});

gulp.task('test', ['pre-test'], function () {
  return gulp.src(['mocha/*.js'])
    .pipe(mocha())
    // Creating the reports after tests ran
    .pipe(istanbul.writeReports())
});
