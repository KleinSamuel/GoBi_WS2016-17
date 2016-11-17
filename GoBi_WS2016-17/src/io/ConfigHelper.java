package io;

import java.net.URISyntaxException;

/**
 * Class to get path to local files
 * 
 * @author Samuel Klein
 */
public class ConfigHelper {

	/**
	 * @return String path to directory in which the jar was executed
	 */
	public String getPathToDirOutsideOfJar(){
		String out = "";
		try {
			out = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return out.substring(0, out.lastIndexOf("/"));
	}
	
	/**
	 * @return String path to configuration package
	 */
	public String getDefaultConfigPath(String filename){
		return this.getClass().getClassLoader().getResource("config/"+filename).toExternalForm().substring(5);
//		return getPathToDirOutsideOfJar()+"/config/"+filename;
	}
	
	/**
	 * @return String path to default output directory which is the output package
	 */
	public String getDefaultOutputPath(){
		return this.getClass().getProtectionDomain().getCodeSource().getLocation().toExternalForm().substring(5).replace("bin", "src")+"output/";
//		return getPathToDirOutsideOfJar()+"/output/";
	}
	
	public String getDefaultTempPath(){
		return this.getClass().getProtectionDomain().getCodeSource().getLocation().toExternalForm().substring(5).replace("bin", "src")+"tempFiles/";
//		return getPathToDirOutsideOfJar()+"/tempfiles/";
	}
	
	public String getDefaultObjectOutputPath(){
		return this.getClass().getProtectionDomain().getCodeSource().getLocation().toExternalForm().substring(5).replace("bin", "src")+"objectFiles/";
	}
}
