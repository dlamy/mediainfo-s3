package com.levelsbeyond.mediainfo.s3;

import org.junit.Test;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;

import static java.lang.String.*;

public class MediaInfoTest {

	@Test
	public void testMediaInfoS3() throws Exception {
		String testS3PublicKey = "";
		String testS3SecretKey = "";
		String testS3Bucket = "mediainfo_testmedia";
		String testS3ObjectKey = "DARIUS GUITARS.mov";

		AWSCredentials myCredentials = new BasicAWSCredentials(testS3PublicKey, testS3SecretKey);
		AmazonS3 s3Client = new AmazonS3Client(myCredentials);

		long startTime = System.currentTimeMillis();
		try (SeekableS3ObjectInputStream objectData = new SeekableS3ObjectInputStream(s3Client, new GetObjectRequest(testS3Bucket, testS3ObjectKey))) {
			try (MediaInfo mediaInfo = MediaInfo.get(objectData).withFullOutput().withRawLanguage().open()) {

				String data = mediaInfo.inform();
				System.out.println(
						format("FROM S3 %s:\n%s", objectData.getObjectRequest(), data)
						);
			}
		}

		long endTime = System.currentTimeMillis();
		System.out.println(format("S3 Object Inspection took %d millis.", (endTime - startTime)));
    }
}
