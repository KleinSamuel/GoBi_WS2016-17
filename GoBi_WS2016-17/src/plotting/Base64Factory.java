package plotting;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import org.apache.commons.io.FileUtils;

/**
 * Create base64 encoded String from image to integrate it into HTML file.
 * 
 * @author Samuel Klein
 *
 */
public class Base64Factory {

	/**
	 * Encode a byte array to base64-String
	 * 
	 * @param array byte[]
	 * @return String in base64
	 */
	public static String encodeByteArray64(byte[] array){
		return Base64.getEncoder().encodeToString(array);
	}
	
	/**
	 * Convert a file into a byte array
	 * 
	 * @param filePath String path to image file
	 * @return byte[] array of bytes
	 */
	public static byte[] imageToByteArray(String filePath){
		byte[] out = null;
		try {
			out = FileUtils.readFileToByteArray(new File(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out;
	}
}
