package com.levelsbeyond.mediainfo.s3;

import java.lang.reflect.Method;

import com.sun.jna.FunctionMapper;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.WString;

import static java.util.Collections.*;


interface MediaInfoLibrary extends Library {
	
	MediaInfoLibrary INSTANCE = (MediaInfoLibrary) Native.loadLibrary("mediainfo", MediaInfoLibrary.class, singletonMap(OPTION_FUNCTION_MAPPER, new FunctionMapper() {
		
		@Override
		public String getFunctionName(NativeLibrary lib, Method method) {
			// MediaInfo_New(), MediaInfo_Open() ...
			return "MediaInfo_" + method.getName();
		}
	}));
	
	
	/**
	 * Create a new handle.
	 * 
	 * @return handle
	 */
	Pointer New();
	

	/**
	 * Open a file and collect information about it (technical information and tags).
	 * 
	 * @param handle
	 * @param file full name of the file to open
	 * @return 1 if file was opened, 0 if file was not not opened
	 */
	int Open(Pointer handle, WString file);

	int Open_Buffer_Init(Pointer handle, long length, long offset);

	/**
	 *  Open a stream (Continue)

		Open a stream and collect information about it (technical information and tags)

		Parameters
			Buffer	pointer to the stream
			Buffer_Size	Count of bytes to read
			Returns a bitfield 
				bit 0: Is Accepted (format is known) 
				bit 1: Is Filled (main data is collected) 
				bit 2: Is Updated (some data have beed updated, example: duration for a real time MPEG-TS stream) 
				bit 3: Is Finalized (No more data is needed, will not use further data) 
				bit 4-15: Reserved 
				bit 16-31: User defined
				
	 * @param buffer
	 * @param size
	 * @return
	 */
	int Open_Buffer_Continue(Pointer handle, byte[] buffer, int size);

	long Open_Buffer_Continue_GoTo_Get(Pointer handle);

	int Open_Buffer_Finalize(Pointer handle);

	/**
	 * Configure or get information about MediaInfo.
	 * 
	 * @param handle
	 * @param option The name of option
	 * @param value The value of option
	 * @return Depends on the option: by default "" (nothing) means No, other means Yes
	 */
	WString Option(Pointer handle, WString option, WString value);
	

	/**
	 * Get all details about a file.
	 * 
	 * @param handle
	 * @return All details about a file in one string
	 */
	WString Inform(Pointer handle);
	

	/**
	 * Get a piece of information about a file (parameter is a string).
	 * 
	 * @param handle
	 * @param streamKind Kind of stream (general, video, audio...)
	 * @param streamNumber Stream number in Kind of stream (first, second...)
	 * @param parameter Parameter you are looking for in the stream (Codec, width, bitrate...),
	 *            in string format ("Codec", "Width"...)
	 * @param infoKind Kind of information you want about the parameter (the text, the measure,
	 *            the help...)
	 * @param searchKind Where to look for the parameter
	 * @return a string about information you search, an empty string if there is a problem
	 */
	WString Get(Pointer handle, int streamKind, int streamNumber, WString parameter, int infoKind, int searchKind);
	

	/**
	 * Get a piece of information about a file (parameter is an integer).
	 * 
	 * @param handle
	 * @param streamKind Kind of stream (general, video, audio...)
	 * @param streamNumber Stream number in Kind of stream (first, second...)
	 * @param parameter Parameter you are looking for in the stream (Codec, width, bitrate...),
	 *            in integer format (first parameter, second parameter...)
	 * @param infoKind Kind of information you want about the parameter (the text, the measure,
	 *            the help...)
	 * @return a string about information you search, an empty string if there is a problem
	 */
	WString GetI(Pointer handle, int streamKind, int streamNumber, int parameterIndex, int infoKind);
	

	/**
	 * Count of streams of a stream kind (StreamNumber not filled), or count of piece of
	 * information in this stream.
	 * 
	 * @param handle
	 * @param streamKind Kind of stream (general, video, audio...)
	 * @param streamNumber Stream number in this kind of stream (first, second...)
	 * @return number of streams of the given stream kind
	 */
	int Count_Get(Pointer handle, int streamKind, int streamNumber);
	

	/**
	 * Close a file opened before with Open().
	 * 
	 * @param handle
	 */
	void Close(Pointer handle);
	

	/**
	 * Dispose of a handle created with New().
	 * 
	 * @param handle
	 */
	void Delete(Pointer handle);
	
}
