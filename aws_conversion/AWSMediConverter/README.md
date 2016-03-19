# Media conversion service utilising Amazon Web Sercives

This project contains an AWS Lambda function that responds to events on the S3 database.

Preliminary step:

- install the [AWS Toolkit for Eclipse](https://docs.aws.amazon.com/AWSToolkitEclipse/latest/ug/tke_setup.html) (for which you will need your own AWS account).


In order to work on this project you will have to import it into Eclipse. Assuming that you are working from a fresh clone, follow these steps to get up and running:

- import -> projects from git -> existing local repository -> .../touring/.git -> (with "importing existing projects") AWSMediConverter -> _finish_

Next, you will have to provide static **linux x86_64** binaries of `ffmpeg` and `ffprobe`. For convenience, you can download precompiled ones [here](http://johnvansickle.com/ffmpeg/). Put these binaries in the `files` directory.
