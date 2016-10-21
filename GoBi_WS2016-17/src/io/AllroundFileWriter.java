package io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import debugStuff.DebugMessageFactory;

public class AllroundFileWriter {

	
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
