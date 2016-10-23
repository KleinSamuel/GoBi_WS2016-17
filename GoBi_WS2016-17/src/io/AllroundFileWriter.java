package io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.Map.Entry;

import debugStuff.DebugMessageFactory;

public class AllroundFileWriter {
	
	public static void writeVector(Vector<Object> vector, File file){
		writeVector(vector, file, false);
	}
	
	public static void writeVector(Vector<Object> vector, File file, boolean append){
		
		String convertedVector = "";
		
		for(Object d : vector){
			
			if(d instanceof String){
				convertedVector += "\""+String.valueOf(d)+"\" ";
			}else{
				convertedVector += String.valueOf(d)+" ";
			}
		}
		convertedVector = convertedVector.substring(0, convertedVector.length()-1);
		
		try {
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));
			
			if(append){
				bw.newLine();
				bw.append(convertedVector);
			}else{
				bw.write(convertedVector);
			}
			
			bw.flush();
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void createHTMLforBarplots(String filepath, ArrayList<String> plotPaths){
		
		try {
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));
			
			bw.write("<!DOCTYPE html>\n");
			bw.write("<html>\n");
			bw.write("<body>\n");
			
			for(String fp : plotPaths){
				
				bw.write("<figure>\n");
				
				bw.write("<img src=\""+fp+".png\" width=\"1000\" height=\"1000\">\n");
				
				bw.write("</figure>\n");
			}
			
			bw.write("</body>\n");
			bw.write("</html>");
			
			bw.flush();
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void writeXMLForTask1(String filepath, HashMap<String, HashMap<String, Integer>> map){
	
		try {
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));
			
			bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
			bw.newLine();
			
			for(Entry<String, HashMap<String, Integer>> entry : map.entrySet()){
				
				bw.write("<entry>");
				bw.newLine();
				bw.write("\t<biotype>"+entry.getKey()+"</biotype>");
				bw.newLine();
				bw.write("\t<subentry>");
				bw.newLine();
				
				for(Entry<String, Integer> entry2 : entry.getValue().entrySet()){
					
					bw.write("\t\t<organims>"+entry2.getKey()+"</organism>");
					bw.newLine();
					bw.write("\t\t<amountGenes>"+entry2.getValue()+"</amountGenes>");
					bw.newLine();
				}
				
				bw.write("\t</subentry>");
				bw.newLine();
				bw.write("</entry>");
				bw.newLine();
				
			}
			
			bw.flush();
			bw.close();
			
			DebugMessageFactory.printNormalDebugMessage(true, "FILE SUCCESSFULLY WRITTEN TO "+filepath);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
}
