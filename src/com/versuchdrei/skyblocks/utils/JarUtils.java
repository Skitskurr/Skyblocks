package com.versuchdrei.skyblocks.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * a util class for methods regarding the jar file
 * @author VersuchDrei
 * @version 1.0
 */
public class JarUtils {
	
	private static final String SEPARATOR = "/";
	
	/**
	 * copies the given folder and all it's subfolders and -files out of the jar into the given directory
	 * @param source the source folder in the jar
	 * @param destination the destination folder in the system
	 * @return true on success, otherwise false
	 */
	public static boolean copyFolder(final String source, final String destination) {
		final File destinationPath = new File(destination);
		if(!destinationPath.exists()) {
			destinationPath.mkdirs();
		}
		
		File sourcePath = null;
		String jarPath = JarUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		if(!jarPath.startsWith("file")) {
			jarPath = "file://" + jarPath;
		}
		
		try {
			sourcePath = new File(new URI(jarPath));
		} catch(final URISyntaxException ex) {
			ex.printStackTrace();
			return false;
		}
		
		try(final ZipInputStream inputStream = new ZipInputStream(new FileInputStream(sourcePath))) {
			final int sourcelength = source.length();
			ZipEntry entry;
			while((entry = inputStream.getNextEntry()) != null) {
				final String fileName = entry.getName();
				if(!fileName.startsWith(source + JarUtils.SEPARATOR)) {
					continue;
				}
				
				final File file = new File(destination + File.separator + fileName.substring(sourcelength));
				if(fileName.endsWith(JarUtils.SEPARATOR)){
					if(file.isFile()) {
						file.delete();
					}
					file.mkdirs();
					continue;
				}
				
				if(file.exists()) {
					continue;
				}
				
				if(!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				
				final FileOutputStream outputStream = new FileOutputStream(file);
				
				final byte[] buffer = new byte[1024];
				int length;
				while((length = inputStream.read(buffer)) > 0) {
					outputStream.write(buffer, 0, length);
				}
				outputStream.close();
			}
			inputStream.closeEntry();
		} catch(final IOException ex) {
			ex.printStackTrace();
			return false;
		}
		
		
		return true;
	}

}
