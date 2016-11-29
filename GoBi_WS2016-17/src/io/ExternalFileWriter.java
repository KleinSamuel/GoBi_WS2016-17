package io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import debugStuff.DebugMessageFactory;

/**
 * FileWriter that can be used from an external class.
 * 
 * @author Samuel Klein
 */
public class ExternalFileWriter {
	
	private BufferedWriter buffWriter;
	
	public void openWriter(String filePath){
		try {
			buffWriter = new BufferedWriter(new FileWriter(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		DebugMessageFactory.printInfoDebugMessage(ConfigReader.DEBUG_MODE, "Opened External Writer.");
	}
	
	public void writeToWriter(String line){
		try {
			buffWriter.write(line);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeWriter(){
		try {
			buffWriter.flush();
			buffWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		DebugMessageFactory.printInfoDebugMessage(ConfigReader.DEBUG_MODE, "Closed External Writer.");
	}
	
}
