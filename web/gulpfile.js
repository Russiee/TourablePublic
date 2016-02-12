var gulp = require('gulp');
var jshint = require('gulp-jshint');
var stylish = require('jshint-stylish');
var nodemon = require('gulp-nodemon');

gulp.task('default', ['lint'], function() {
	nodemon({
		script: 'server.js',
		ext: 'js html',
		env: { 'NODE_ENV': 'development' }
	});
});

gulp.task('lint', function() {
  return gulp.src('./lib/*.js')
	.pipe(jshint())
	.pipe(jshint.reporter('jshint-stylish'));
});
