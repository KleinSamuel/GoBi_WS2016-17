package io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Main class for every config read in shit
 * 
 * @author Samuel Klein
 */
public class ConfigReader {
	
	public static boolean DEBUG_MODE = true;
	
	public static HashMap<String, String> readConfig(){
		
		ConfigHelper ch = new ConfigHelper();
		HashMap<String, String> configMap = new HashMap<>();
		
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(ch.getDefaultConfigPath("configuration.txt")));
			
			String line = "";
			
			while((line = br.readLine()) != null){
				if(line.startsWith("#")){
					continue;
				}
				String[] tmpArray = line.split("\t");
				
				if(tmpArray[1].equals("DEFAULT")){
					
					switch (tmpArray[0]) {
					case "output_directory":
						configMap.put(tmpArray[0], ch.getDefaultOutputPath());
						break;
					case "temp_directory":
						configMap.put(tmpArray[0], ch.getDefaultTempPath());
						break;
					case "object_directory":
						configMap.put(tmpArray[0], ch.getDefaultObjectOutputPath());
						break;
					}
					
				}else{
					configMap.put(tmpArray[0], tmpArray[1]);
				}
				
			}
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return configMap;
	}

	/**
	 * Read in a configuration file.
	 * Separated key-value pairs
	 * 
	 * @param filepath to file
	 * @param seperator String by which the values are separated
	 * @param ignoredCharacter String[] of ignored line beginnings
	 * @return HashMap<String, String> with key and value
	 */
	public static HashMap<String, String> readFilepathConfig(String filepath, String seperator, String[] ignoredCharacter){
		
		HashMap<String, String> outputMap = new HashMap<>();
		ArrayList<String> ignoredCharList = new ArrayList<>();
		
		for (int i = 0; i < ignoredCharacter.length; i++) {
			ignoredCharList.add(ignoredCharacter[i]);
		}
		
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			String line = null;
			
			while((line = br.readLine())!= null){
				
				if(ignoredCharList.contains(String.valueOf(line.charAt(0)))){
					continue;
				}
				
				String[] lineArray = line.split(seperator);
				outputMap.put(lineArray[0], lineArray[1]);
			}
			
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return outputMap;
	}
	
	
}
