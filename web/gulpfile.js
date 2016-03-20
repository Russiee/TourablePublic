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
