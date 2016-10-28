package io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AllroundFileReader {

	
	public HashMap<String, HashMap<String, Integer>> readXMLForTask1(String filepath){
		
		HashMap<String, HashMap<String, Integer>> mainMap = new HashMap<>();
		
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			
			String line = "";
			String biotype = "";
			String org = "";
			HashMap<String, Integer> map = new HashMap<>();
			
			Pattern biotypePattern = Pattern.compile("<biotype>(.*)?<\\/biotype>");
			Pattern organismPattern = Pattern.compile("<organism>(.*)?<\\/organism>");
			Pattern amountPattern = Pattern.compile("<amountGenes>(.*)?<\\/amountGenes>");
			Matcher biotypeMatcher, organismMatcher, amountMatcher;
			
			while((line = br.readLine()) != null){
				
				biotypeMatcher = biotypePattern.matcher(line);
				organismMatcher = organismPattern.matcher(line);
				amountMatcher = amountPattern.matcher(line);
				
				if(biotypeMatcher.find()){
					biotype = biotypeMatcher.group(1);
				}else if(organismMatcher.find()){
					org = organismMatcher.group(1);
				}else if(amountMatcher.find()){					
					map.put(org, Integer.parseInt(amountMatcher.group(1)));
				}
				
				if(line.equals("</entry>")){
					
					mainMap.put(biotype, map);
					map = new HashMap<>();
				}
				
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return mainMap;
		
	}
	
	public static TreeMap<String, String> readAnnotation(String filepath){
		
		TreeMap<String, String> output = new TreeMap<>();
		
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			
			String line = "";
			
			while((line = br.readLine()) != null){
				String[] lineArray = line.split("\t");
				output.put(lineArray[1], lineArray[0]);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return output;
	}
	
}
