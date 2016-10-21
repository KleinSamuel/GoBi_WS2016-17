package io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Main class for every read in shit
 * 
 * @author Samuel Klein
 *
 */
public class ConfigReader {
	
	/**
	 * 
	 * @return String path to config package
	 */
	public String getDefaultConfigPath(){
		return this.getClass().getClassLoader().getResource("config/gtf-paths.txt").toExternalForm().substring(5);
	}

	/**
	 * Read in a config file.
	 * Seperated key-value pairs
	 * 
	 * @param filepath to file
	 * @param seperator String by which the values are seperated
	 * @param ignoredCharacter String[] of ignored line beginnings
	 * @return HashMap<String, String> with key and value
	 */
	public HashMap<String, String> readFilepathConfig(String filepath, String seperator, String[] ignoredCharacter){
		
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
