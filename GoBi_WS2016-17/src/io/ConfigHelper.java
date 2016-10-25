package io;

/**
 * Class to get path to local files
 * 
 * @author Samuel Klein
 */
public class ConfigHelper {

	/**
	 * @return String path to configuration package
	 */
	public String getDefaultConfigPath(String filename){
		return this.getClass().getClassLoader().getResource("config/"+filename).toExternalForm().substring(5);
	}
	
	/**
	 * @return String path to default output directory which is the output package
	 */
	public String getDefaultOutputPath(){
		return this.getClass().getProtectionDomain().getCodeSource().getLocation().toExternalForm().substring(5).replace("bin", "src")+"output/";
	}
	
	public String getDefaultTempPath(){
		return this.getClass().getProtectionDomain().getCodeSource().getLocation().toExternalForm().substring(5).replace("bin", "src")+"tempFiles/";
	}
	
	public String getDefaultObjectOutputPath(){
		return this.getClass().getProtectionDomain().getCodeSource().getLocation().toExternalForm().substring(5).replace("bin", "src")+"objectFiles/";
	}
}
