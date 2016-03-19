package conversion;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;

import org.dcm4che3.imageio.plugins.dcm.DicomImageReadParam;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

public class LambdaFunctionHandler implements RequestHandler<S3Event, String> {
	
	private static final String VAR = "/var/task";
	private static final String TMP = "/tmp";
	
	private static final String LOG4J_PROP = "/var/task/log4j.properties";
	private static final String FFMPEG = "/tmp/ffmpeg";
	private static final String FFPROBE = "/tmp/ffprobe";
	
	private static final String AVI = ".avi";
	private static final String MP4 = ".mp4";
	private static final String MP4_MIME = "video/mp4";
	private static final String JPG = ".jpg";
	private static final String JPG_MIME = "image/jpeg";
	private static final String DICOM = ".dcm";
	private static final String FILE_TYPE_PATTERN = "(.+)(\\.\\w{3,4})";
	
	private LambdaLogger logger;
	
    public String handleRequest(S3Event event, Context context) {
    	logger = context.getLogger();
    	String msg = "";
    	
    	moveBinaries();
//    	testFFmpeg();
    	
        S3EventNotification.S3EventNotificationRecord record = event.getRecords().get(0);

        String bucket = record.getS3().getBucket().getName();
        String key = record.getS3().getObject().getKey().replace("+",  " "); // this has the filename
        
        try {
			key = URLDecoder.decode(key, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// abandon thread if this happens?
			e1.printStackTrace();
		}
        
        Matcher matcher = Pattern.compile(FILE_TYPE_PATTERN).matcher(key);
        String fileName = null;
        String fileType = null;
        
        // separate the file's name and type. Note fileType has the dot
        if (matcher.matches()) {
        	fileName = matcher.group(1);
        	fileType = matcher.group(2);
        } else {
        	logger.log(String.format("Could not match file %s", key));        	
        }
        
        boolean success = false;
        
        if (fileType != null) {
        	switch (fileType) {
			case AVI:
				success = convertAviToMp4(bucket, fileName, key);
				msg = "Converted avi to mp4";
				break;
			case DICOM:
				success = convertDicom(bucket, fileName, key);
				msg = "Converted dcm to jpg";
				break;
			default:
				logger.log(String.format("No need to convert %s", key));
				msg = "Skipped conversion";
				break;
			}
        }
        
        if (success) {
        	deleteFileFromS3Bucket(bucket, key);
        }

        return msg;
    }
    
    /**
     * Conversion innit.
     * 
     * @param bucket the S3 bucket that the file was uploaded to
     * @param fileName the file's name, without the .extension on the end
     * @param key the file's name, with the extension included
     */
    private boolean convertAviToMp4(String bucket, String fileName, String key) {
    	logger.log(String.format("Converting %s to mp4", key));
    	boolean success = false;
    	
    	File aviFile = new File(TMP + "/" + key);
    	String mp4FileName = fileName + MP4;
    	String convertedFilePath = TMP + "/" + mp4FileName;
    	AmazonS3Client s3Client = new AmazonS3Client();
    	
    	// this saves the S3Object to the specified file. Don't know what to do with the metadata
    	ObjectMetadata s3ObjectMeta = s3Client.getObject(new GetObjectRequest(bucket, key), aviFile);
    	
    	// do conversion to mp4 with ffmeg
    	try {
        	FFmpeg ffmpeg = new FFmpeg(FFMPEG);
        	FFprobe ffprobe = new FFprobe(FFPROBE);
        	
        	FFmpegBuilder builder = new FFmpegBuilder()
        		.setInput(aviFile.getAbsolutePath())
        			.overrideOutputFiles(true)
        		.addOutput(convertedFilePath)
        			.setFormat("mp4")
        			.setVideoCodec("libx264")
        			.done();
        	
        	FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        	executor.createJob(builder).run();
        	
        } catch (Exception e) {
        	e.printStackTrace();
        }
    	
    	// load converted file back into the bucket
    	File mp4File = new File(convertedFilePath);
    	
    	if (mp4File.exists()) {
    		s3Client.putObject(bucket, mp4FileName, mp4File);
    		success = true;
    	}
    	
    	// delete the two video files in /tmp
    	aviFile.delete();
    	mp4File.delete();
    	
    	return success;
    }
    
    private boolean convertDicom(String bucket, String fileName, String key) {
    	File dcmFile = new File(TMP + "/" + key);
    	boolean success = false;
    	AmazonS3Client s3Client = new AmazonS3Client();
    	
    	// this saves the S3Object to the specified file
    	ObjectMetadata s3ObjectMeta = s3Client.getObject(new GetObjectRequest(bucket, key), dcmFile);
    	
    	try {
    		ImageReader imageReader = ImageIO.getImageReadersByFormatName("DICOM").next();
    		ImageInputStream iis = ImageIO.createImageInputStream(dcmFile);
			imageReader.setInput(iis,false);
			
			int frameCount = 1;
			
			try {
				frameCount = imageReader.getNumImages(false);				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (frameCount == 1) {
				logger.log("File has one frame");
				success = convertDcmToJpg(bucket, fileName, key, imageReader, iis);
			} else {
				logger.log(String.format("File has %d frames", frameCount));
				success = convertDcmToMp4(bucket, fileName, key, imageReader, iis, frameCount);
			}
			
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	dcmFile.delete();
    	return success;
    }
    
    private boolean convertDcmToJpg(String bucket, String fileName, String key, ImageReader imageReader, ImageInputStream inputStream) {
    	logger.log(String.format("Converting %s to jpg", key));
    	boolean success = false;
    	
    	String jpgFileName = fileName + JPG;
    	String jpgFilePath = TMP + "/" + jpgFileName;
    	
    	
    	
    	// do conversion to jpg with dcm4che2
    	try {
    		DicomImageReadParam dicomImageReadParam = (DicomImageReadParam) imageReader.getDefaultReadParam();

    		BufferedImage myJpegImage = imageReader.read(0, dicomImageReadParam);
    		inputStream.close();

    		if (myJpegImage == null) {
    			System.out.println("Could not read image!!");
    		}

    		try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(jpgFilePath))) {
    			ImageIO.write(myJpegImage, "jpg", outputStream);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}

    		System.out.println("Completed");

    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	    	
    	// load converted file back into the bucket
    	File jpgFile = new File(jpgFilePath);
    	
    	if (jpgFile.exists()) {
    		AmazonS3Client s3Client = new AmazonS3Client();
    		s3Client.putObject(bucket, jpgFileName, jpgFile);   
    		success = true;
    	}    	
    	
    	jpgFile.delete();
    	return success;
    }
    
    private boolean convertDcmToMp4(String bucket, String fileName, String key, ImageReader imageReader,
    		ImageInputStream inputStream, int frameCount) {
    	logger.log(String.format("Converting %s to mp4", key));
    	boolean success = true;
    	
    	// directory for intermediate jpegs
    	String dirPath = TMP + "/" + fileName + "Temp";
    	File tempDir = new File(dirPath);
    	tempDir.mkdir();
    	String jpgTempBase = dirPath + "/" + fileName;
    	
    	String mp4FileName = fileName + MP4;
    	String mp4FilePath = TMP + "/" + mp4FileName;
    
    	try {
    		DicomImageReadParam dicomImageReadParam = (DicomImageReadParam) imageReader.getDefaultReadParam();
    		
    		for (int i = 0; i < frameCount; ++i) {
    			BufferedImage myJpegImage = imageReader.read(i, dicomImageReadParam);
    			String num = String.format("%03d", i);
    			
    			try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(jpgTempBase + num + JPG))) {
        			ImageIO.write(myJpegImage, "jpg", outputStream);
        		} catch (IOException e) {
        			e.printStackTrace();
        			inputStream.close();
        			success = false;
        		}
    		}
    		
    		inputStream.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	if (success) {
    		try {
            	FFmpeg ffmpeg = new FFmpeg(FFMPEG);
            	FFprobe ffprobe = new FFprobe(FFPROBE);
            	
            	FFmpegBuilder builder = new FFmpegBuilder()
            		.setInput(jpgTempBase + "%3d" + JPG)
            			.overrideOutputFiles(true)
            		.addOutput(mp4FilePath)
            			.setFormat("mp4")
            			.setVideoCodec("libx264")
            			.setVideoFrameRate(24, 1)
            			.done();
            	
            	FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
            	executor.createJob(builder).run();
            } catch (Exception e) {
            	e.printStackTrace();
            }
    	}
    	
    	File mp4File = new File(mp4FilePath);
    	
    	if (mp4File.exists()) {
    		AmazonS3Client s3Client = new AmazonS3Client();
    		s3Client.putObject(bucket, mp4FileName, mp4File);   
    		success = true;
    	}
    	
    	for (File f : tempDir.listFiles()) {
    		f.delete();
    	}
    	
    	tempDir.delete();
    	
    	return success;
    }
    
    private void deleteFileFromS3Bucket(String bucket, String key) {
    	logger.log(String.format("Deleting file %s in bucket %s", key, bucket));
    	
    	AmazonS3Client s3 = new AmazonS3Client();
    	s3.deleteObject(bucket, key);
    }
    
    private void testFFmpeg() {
    	try {
        	FFmpeg ffmpeg = new FFmpeg(FFMPEG);
        	System.out.println(ffmpeg.version());
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    /**
     * Amazon puts uploaded binaries into /var/tasks and resets the permissions to be
     * non-executable. For whatever reason, we cannot change permissions on files in
     * /var/task, but we can move them to /tmp, which we have more freedom in.
     * <p>
     * This method first checks to see if the ffmpeg binaries are in /tmp. If they are
     * not, then they need to be moved there from /var/task and have their permissions
     * changed.
     */
    private void moveBinaries() {
    	ProcessBuilder ls = new ProcessBuilder("ls", TMP, "|", "grep", "'ff'");
    	boolean inWrongDir = true;
    	
    	try {
    		Process p = ls.start();
    		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
    		
    		// if this line is empty, it means that the binaries are not in /tmp
    		String line = br.readLine();
    		br.close();
    		
    		if (line.length() > 5) {
    			inWrongDir = false;
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	if (inWrongDir) {
    		ProcessBuilder move_1 = new ProcessBuilder("mv", VAR + "/ffmpeg", TMP);
    		ProcessBuilder move_2 = new ProcessBuilder("mv", VAR + "/ffprobe", TMP);
    		ProcessBuilder chmod_1 = new ProcessBuilder("chmod", "755", TMP + "/ffmpeg");
    		ProcessBuilder chmod_2 = new ProcessBuilder("chmod", "755", TMP + "/ffprobe");

    		try {
    			Process p1 = move_1.start();
    			Process p2 = move_2.start();

    			p1.waitFor();
    			p2.waitFor();

    			Process p3 = chmod_1.start();
    			Process p4 = chmod_2.start();

    			p3.waitFor();
    			p4.waitFor();
    		} catch (Exception e) {
    			System.out.println(e.getMessage());
    		}
    	}
    }
}