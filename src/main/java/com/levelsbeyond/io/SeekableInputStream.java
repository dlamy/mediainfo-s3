package com.levelsbeyond.io;

import java.io.IOException;
import java.io.InputStream;

public abstract class SeekableInputStream extends InputStream {

	public abstract void seekTo(long pos) throws IOException;

	public abstract long getLength();
}
