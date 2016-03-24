# Media conversion service utilising Amazon Web Sercives

This project contains an AWS Lambda function that responds to events on the S3 database.

Preliminary step:

- install the [AWS Toolkit for Eclipse](https://docs.aws.amazon.com/AWSToolkitEclipse/latest/ug/tke_setup.html) (for which you will need your own AWS account).


In order to work on this project you will have to import it into Eclipse. Assuming that you are working from a fresh clone, follow these steps to get up and running:

- import -> projects from git -> existing local repository -> .../touring/.git -> (with "importing existing projects") AWSMediConverter -> _finish_

# Dependencies

Next, you will have to provide static **linux x86_64** binaries of `ffmpeg` and `ffprobe`. For convenience, you can download precompiled ones [here](http://johnvansickle.com/ffmpeg/). Put these binaries in the `files` directory.

The two primary libraries used, DCM4CHE3 and the AWS Java SDK, both come with their own third party libraries, however not all are necessary to compile the lambda function.

Download the following JARs and add them to your project's buildpath

* commons-cli-1.3.1.jar
* commons-codec-1.6.jar
* commons-io-2.4.jar
* commons-lang3-3.4.jar
* commons-logging-1.1.3.jar
* dcm4che-core-3.3.8-SNAPSHOT.jar
* dcm4che-dict-3.3.8-SNAPSHOT.jar
* dcm4che-image-3.3.8-SNAPSHOT.jar
* dcm4che-imageio-3.3.8-SNAPSHOT.jar
* dcm4che-tool-dcm2jpg-3.3.8-SNAPSHOT.jar
* ffmpeg-0.4.jar
* gson-2.6.2.jar
* guava-19.0.jar
* httpclient-4.3.6.jar
* httpcore-4.3.3.jar
* joda-time-2.8.1.jar
* log4j-1.2.17.jar
* modelmapper-0.7.5.jar
* slf4j-api-1.7.5.jar
* slf4j-log4j12-1.7.5.jar