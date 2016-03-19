# Media conversion service utilising Amazon Web Sercives

This project contains an AWS Lambda function that responds to events on the S3 database.

In order to work on this project you will have to import it into Eclipse. You will also have to install the [AWS Toolkit for Eclipse](https://docs.aws.amazon.com/AWSToolkitEclipse/latest/ug/tke_setup.html) (for which you will need your own AWS account). All the jars in `files/` will have to be added to your buildpath.

Next, you will have to provide static **linux x86_64** binaries of `ffmpeg` and `ffprobe`. For convenience, you can download precompiled ones [here](http://johnvansickle.com/ffmpeg/).
