package alisovets.lib.uilib.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.ClipDescription;
import android.content.Context;
import android.net.Uri;
import android.os.StatFs;
import android.util.Log;
import android.webkit.URLUtil;

/**
 * 
 * Class consists of static methods to help a file working   
 * @author Alexander Lisovets
 *
 */
public class FileUtil {
	private static final String TAG = "FileUtil log";
	public static final String FILE_PREFIX = "file://";

	
	
	
	/**
	 * Copies a file or directory
	 * @param source - a file or directory to copy
	 * @param destination - a destination file 
	 * @param overwrite - flag means that you need to overwrite the existing file
	 * @throws IOException
	 */
	public static void copyFile(File source, File destination, boolean overwrite) throws IOException {

		if (!source.exists()) {
			throw new IOException("The file " + destination + " doesn't exist.");
		}

		if (!overwrite && destination.exists()) {
			throw new IOException("The target file " + destination + " already exists.");
		}

		File parentDirectory = destination.getParentFile();
		if (!parentDirectory.exists() && !parentDirectory.mkdirs()) {
			throw new IOException("1Cannot to create the directory " + parentDirectory.getAbsolutePath());
		}

		if (source.isFile()) {
			copyOrdinaryFile(source, destination);
			return;
		}

		if (source.isDirectory()) {

			List<String> pathList = new ArrayList<String>();
			createFileList(source, source, pathList);

			if (!destination.exists() && !destination.mkdirs()) {
				throw new IOException("Cannot to create the directory " + destination.getAbsolutePath());
			}
			for (String path : pathList) {
				if(Thread.currentThread().isInterrupted()){
					break;
				}
				File file = new File(destination, path);
				File srcFile = new File(source, path);
				if (srcFile.isDirectory()) {
					if (!file.exists() && !file.mkdirs()) {
						throw new IOException("Cannot to create the directory " + parentDirectory.getAbsolutePath());
					}
				} else if (srcFile.isFile()) {
					copyOrdinaryFile(new File(source, path), new File(destination, path));
				}
			}
		}
	}

	/**
	 * Replaces a file or directory
	 * @param source - a file or directory to replace
	 * @param destination - a destination file
	 * @param overwrite - flag means that you need to overwrite the existing file
	 * @param skipUndeletable if it set true then skip undeletable files  
	 * @throws IOException
	 */
	public static void replaceFile(File source, File destination, boolean overwrite, boolean skipUndeletable) throws IOException {

		if (!source.exists()) {
			throw new IOException("The file " + destination + " doesn't exist.");
		}

		if (!overwrite && destination.exists()) {
			throw new IOException("The target file " + destination + " already exists.");
		}

		File parentDirectory = destination.getParentFile();
		if (!parentDirectory.exists() && !parentDirectory.mkdirs()) {
			throw new IOException("Cannot to create the directory " + parentDirectory.getAbsolutePath());
		}

		if (source.isFile()) {
			copyOrdinaryFile(source, destination);
			return;
		}

		if (source.isDirectory()) {

			List<String> pathList = new ArrayList<String>();
			createFileList(source, source, pathList);

			if (!destination.exists() && !destination.mkdirs()) {
				throw new IOException("Cannot to create the directory " + destination.getAbsolutePath());
			}
			for (String path : pathList) {
				if(Thread.currentThread().isInterrupted()){
					break;
				}
				File file = new File(destination, path);
				File srcFile = new File(source, path);
				if (srcFile.isDirectory()) {
					if (!file.exists() && !file.mkdirs()) {
						throw new IOException("Cannot to create the directory " + parentDirectory.getAbsolutePath());
					}
				} else if (srcFile.isFile()) {
					copyOrdinaryFile(new File(source, path), new File(destination, path));
				}
			}
		}
	}
	

	/*
	 * recursively creates the list of relative paths of files in the specified directory.  
	 */
	private static void createFileList(File source, File currentDirectory, List<String> filePathList) {

		int rootPathLength = source.getAbsolutePath().length();

		if (rootPathLength == 1) {
			rootPathLength = 0;
		}

		File[] files = currentDirectory.listFiles();
		if (files == null) {
			return;
		}

		for (int i = 0; i < files.length; i++) {
			if(Thread.currentThread().isInterrupted()){
				break;
			}
			String path = files[i].getAbsolutePath();
			path = path.substring(rootPathLength + 1, path.length());
			filePathList.add(path);

			if (files[i].isDirectory()) {
				createFileList(source, files[i], filePathList);
			}
		}
	}
	

	/**
	 * Creates FileDetails object that contains information about the specified file
	 * @param file - the file details of which need
	 * @return  the FileDetails object with file details
	 */
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static FileDetails createDetails(File file) {
		FileDetails details = new FileDetails();
		details.filename = file.getName();
		details.parentFolder = file.getParent();

		Log.d(TAG, "size= " + file.length());
		if((!file.isDirectory() && !file.isFile())){
			return details;
		} 
		StatFs statFs = new StatFs(file.getAbsolutePath());

		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
			details.totalSpace = (long) statFs.getBlockCount() * (long) statFs.getBlockSize();
			details.freeSpace = (long) statFs.getAvailableBlocks() * (long) statFs.getBlockSize();
		} else {
			details.totalSpace = (long) statFs.getBlockCountLong() * (long) statFs.getBlockSizeLong();
			details.freeSpace = (long) statFs.getAvailableBlocksLong() * (long) statFs.getBlockSizeLong();
		}
		details.usableSpace = details.totalSpace - details.freeSpace;
		details.canRead = file.canRead();
		details.canWrite = file.canWrite();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM);
		details.lastModifiedDate = dateFormat.format(new Date(file.lastModified()));
		details.folderCount = 0;
		details.fileCount = 0;
		details.size = 0;
		details.isDirectory = file.isDirectory();
		if (!details.isDirectory) {
			details.size = file.length();
			return details;
		}
		countDirectorySizeRecursivly(file, details);
		Log.d(TAG, "folderCount = " + details.folderCount + "  fileCount=  " + details.fileCount + "  size= " + details.size);
		return details;

	}

	/**
	 * Counts directory size and sets it in specified FileDetails object 
	 * @param directory - directory to count size 
	 * @param details - the FileDetails object to which the result should be placed
	 */
	private static void countDirectorySizeRecursivly(File directory, FileDetails details) {

		File[] fileArray = directory.listFiles();
		if (fileArray == null) {
			return;
		}
		for (File file : fileArray) {
			if(Thread.currentThread().isInterrupted()){
				break;
			}
			if (file.isFile()) {
				details.fileCount++;
				details.size += file.length();
				continue;
			}

			if (file.isDirectory()) {
				details.folderCount++;
				countDirectorySizeRecursivly(file, details);
			}
		}

	}
	
	/**
	 * Replaces (copy and delete source) a directory or file 
	 * @param sourceFile - file or directory to repalse
	 * @param targetFile - target file
	 * @param skipUndeletable - if it is true then skip undeletable files, if false - stop when there are undeletable files 
	 * @throws IOException
	 */
	public static void replaceRecursively(File sourceFile,  File targetFile,  boolean skipUndeletable) throws IOException {
		
		File parentFile = targetFile.getParentFile();
		if(!parentFile.exists() && !parentFile.mkdirs()){
			throw new IOException("Cannot create directory " + parentFile.getAbsolutePath());
		}
		
		if (sourceFile.isFile()){ 
			if (sourceFile.renameTo(targetFile)) {
				return;
			}
			copyOrdinaryFile(sourceFile, targetFile);
			if (sourceFile.delete() || skipUndeletable) {
				return;
			}
			throw new CannotDeleteFileException("Cannot delete file " + sourceFile.getAbsolutePath());	
		}
		
		File[] fileArray = sourceFile.listFiles();
		if ((fileArray == null) || (fileArray.length == 0)) {
			if(!sourceFile.delete() && !skipUndeletable){
				throw new CannotDeleteFileException("Cannot delete directory " + sourceFile.getAbsolutePath());
			}
			return;
		}
		
		
		for (File file : fileArray) {
			if(Thread.currentThread().isInterrupted()){
				break;
			}
			
			File curentTargetFile = new File(targetFile, file.getName());
			replaceRecursively(file, curentTargetFile, skipUndeletable);
			continue;
						
		}
		
		Log.d(TAG, "Delete " + sourceFile.getAbsolutePath() + " list " + sourceFile.listFiles() + "  " + sourceFile.listFiles().length);
		fileArray = sourceFile.listFiles();
		if ((fileArray == null)  || (fileArray.length == 0)) {
			if(!sourceFile.delete() && !skipUndeletable){
				throw new CannotDeleteFileException("Cannot delete directory " + sourceFile.getAbsolutePath());
			}
			
		}
	}
	
	/**
	 * deletes a file or recurcively deletes directory   
	 * @param topFile - the file or directory to delete
	 * @throws IOException
	 */
	public static void deleteRecursively(File topFile) throws IOException {
		if (topFile.isFile()){ 
			if(!topFile.delete()){
				throw new IOException("Cannot delete file " + topFile.getAbsolutePath());
			}
			return;
		}
		
		File[] fileArray = topFile.listFiles();
		if (fileArray == null) {
			if(!topFile.delete()){
				throw new IOException("Cannot delete directory " + topFile.getAbsolutePath());
			}
			return;
		}
		
		
		for (File file : fileArray) {
			if(Thread.currentThread().isInterrupted()){
				break;
			}
			
			if (file.isFile()) {
				if(!file.delete()){
					throw new IOException("Cannot delete file " + file.getAbsolutePath());
				}
				
				continue;
			}

			if (file.isDirectory()) {
				deleteRecursively(file);
			}
			
		}
		if(!topFile.delete()){
			throw new IOException("Cannot delete directory " + topFile.getAbsolutePath());
		}
	}

	/**
	 * copies file 
	 * @param source 
	 * @param destination
	 * @throws IOException
	 */
	public static void copyOrdinaryFile(File source, File destination) throws IOException {

		InputStream in = new BufferedInputStream(new FileInputStream(source));
		OutputStream out = new BufferedOutputStream(new FileOutputStream(destination));
		try {

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.flush();
		} finally {
			in.close();
			out.close();
		}
	}

	/**
	 * copy file to the clipboard
	 * @param context - Context
	 * @param file - the file to copy
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static void copyToClipboard(Context context, File file) {

		Log.d(TAG, "copyToClipboard " + file.getAbsolutePath());
		
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(FILE_PREFIX + file.getAbsolutePath());

		} else {
			Log.d(TAG, "copy" + file.getAbsolutePath());
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			Uri uri = Uri.fromFile(file);

			Log.d(TAG, "getFileFromClipboard() uri= " + uri);

			android.content.ClipData clip = android.content.ClipData.newRawUri(null, uri);
			clipboard.setPrimaryClip(clip);

		}

	}

	/**
	 * Gets a File object fron the clipboard  
	 * @param context 
	 * @param clean - if true then clean the clipboard
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static File getFileFromClipboard(Context context, boolean clean) {
		Log.d(TAG, "getFileFromClipboard()");

		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			if (clipboard.hasText()) {
				String path = clipboard.getText().toString();
				if ((path.length() > 7) && path.substring(0, 7).equals(FILE_PREFIX)) {
					path = path.substring(6);
					File file = new File(path);
					if (file.exists()) {
						if (clean) {
							clipboard.setText(null);
						}
						return file;
					}

				}
			}
			return null;
		}

		android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

		Log.d(TAG, "getFileFromClipboard() >= 11 is in clipBoard " + clipboard.hasPrimaryClip());
		if (clipboard.hasPrimaryClip() && (clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_URILIST))) {
			android.content.ClipData clip = clipboard.getPrimaryClip();
			Log.d(TAG, "getFileFromClipboard() item count: " + clip.getItemCount());

			android.content.ClipData.Item item = clip.getItemAt(0);
			Uri uri = item.getUri();

			Log.d(TAG, "getFileFromClipboard() uri= " + uri + " path:" + uri.getPath() + "  " + URLUtil.isFileUrl(uri.toString()));

			if (URLUtil.isFileUrl(uri.toString())) {
				Log.d(TAG, "getFileFromClipboard() isFileUrl");
				File file = new File(uri.getPath());
				Log.d(TAG, "getFileFromClipboard() uri.getPath()= " + uri.getPath() + "  file: " + file.getAbsolutePath()); 
				Log.d(TAG, "" + file.getAbsolutePath() + " isFile: "  +file.isFile() + "  " + " isDirectory(): " + file.isDirectory() + " file.isAbsolut(): " + file.isAbsolute() + " canWrite" + file.canWrite() + "  canRead: " + file.canRead() ); 
 						
				if (file.exists()) {
					Log.d(TAG, "getFileFromClipboard() File exists");
					if (clean) {
						clip = android.content.ClipData.newPlainText(null, null);
						clipboard.setPrimaryClip(clip);
					}
					return file;
				}
			}
		}

		return null;

	}
	
	/**
	 * gets a file extension from the specified file 
	 * @param file - the file to get the extension
	 * @return
	 */
	public static String getFileExtension(File file) {

		String fileName = file.getName();
		int pos = fileName.lastIndexOf(".");
		if (pos < 0) {
			return "";
		}

		String extension = fileName.substring(pos + 1);
		if (extension.length() > 5) {
			return "";
		}
		return extension.toLowerCase(Locale.US);

	}

	/**
	 * to hold file details
	 */
	public static class FileDetails {
		public String filename;
		public String parentFolder;
		public boolean isDirectory;
		public int folderCount;
		public int fileCount;
		public long size;
		public boolean canRead;
		public boolean canWrite;
		public long totalSpace;
		public long freeSpace;
		public long usableSpace;
		public String lastModifiedDate;

	}
	
	
}
