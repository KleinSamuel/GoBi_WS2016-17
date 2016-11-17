package plotting;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

public class Base64Factory {

	public static String encodeByteArray64(byte[] array){
		return Base64.getEncoder().encodeToString(array);
	}
	
	public static byte[] imageToByteArray(String filePath){
		
		BufferedImage bim = null;
		
		try {
			
			bim = ImageIO.read(new File(filePath));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(bim == null){
			return null;
		}
		
		WritableRaster raster = bim.getRaster();
		DataBufferByte data = (DataBufferByte)raster.getDataBuffer();
		
		return data.getData();
	}
	
}
