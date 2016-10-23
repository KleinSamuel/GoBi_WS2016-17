package io;

import java.io.File;
import java.io.IOException;

/**
 * Handle temporary files
 * 
 * @author Samuel Klein
 */
public class TemporaryFile {

	/**
	 * Create a temporary file which self destroys after the java VM terminates.
	 * 
	 * @param filepath to directory for temporary file
	 * @return File temporary file
	 */
	public static File createTempFile(String filepath){
		
		File tmp = null;
		
		try {
			tmp = File.createTempFile("tmp", ".txt", new File(filepath));
			tmp.deleteOnExit();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tmp;
	}
	
	/**
	 * Create a temporary file which self destroys after the java VM terminates.
	 * 
	 * @return File temporary file
	 */
	public static File createTempFile(){
		return createTempFile(ConfigReader.readConfig().get("temp_directory"));
	}
	
}
