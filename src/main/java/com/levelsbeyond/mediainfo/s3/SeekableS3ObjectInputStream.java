package com.levelsbeyond.mediainfo.s3;

import java.io.IOException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.levelsbeyond.io.SeekableInputStream;

public class SeekableS3ObjectInputStream extends SeekableInputStream {
	private final AmazonS3 s3Client;
	private final GetObjectRequest objectRequest;
	private S3ObjectInputStream objectStream;
	private long totalLength = -1;
	private long partialLength = -1;

	public SeekableS3ObjectInputStream(AmazonS3 s3Client, GetObjectRequest objectRequest) throws IOException {
		this.s3Client = s3Client;
		this.objectRequest = objectRequest;

		totalLength = initLength();
		partialLength = totalLength;
		resetStream(0);
	}

	public GetObjectRequest getObjectRequest() {
		return objectRequest;
	}

	public long initLength() throws IOException {
		S3Object s3Obj = s3Client.getObject(objectRequest);
		return s3Obj.getObjectMetadata().getContentLength();
	}

	@Override
	public long getLength() {
		return totalLength;
	}

	public long getPartialLength() {
		return partialLength;
	}

	private void resetStream(long pos) throws IOException {
		if (objectStream != null) {
			objectStream.abort();
		}

		objectRequest.setRange(pos, totalLength);
		objectStream = s3Client.getObject(objectRequest).getObjectContent();
		partialLength = totalLength - pos;
	}

	@Override
	public void seekTo(long pos) throws IOException {
		resetStream(pos);
	}

	@Override
	public int read() throws IOException {
		return objectStream.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return objectStream.read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return objectStream.read(b, off, len);
	}

	@Override
	public long skip(long n) throws IOException {
		return objectStream.skip(n);
	}

	@Override
	public int available() throws IOException {
		return objectStream.available();
	}

	@Override
	public void close() throws IOException {
		objectStream.abort();
	}

	@Override
	public synchronized void mark(int readlimit) {
		objectStream.mark(readlimit);
	}

	@Override
	public synchronized void reset() throws IOException {
		objectStream.reset();
	}

	@Override
	public boolean markSupported() {
		return objectStream.markSupported();
	}

}
