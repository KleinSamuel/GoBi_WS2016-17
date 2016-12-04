package io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import debugStuff.DebugMessageFactory;

public class ExternalFileReader {

	private BufferedReader buffReader;
	
	public void openReader(String filepath){
		try {
			this.buffReader = new BufferedReader(new FileReader(filepath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		DebugMessageFactory.printInfoDebugMessage(ConfigReader.DEBUG_MODE, "Opened External Reader.");
	}
	
	public String readNextLine(){
		try {
			return this.buffReader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void closeReader(){
		try {
			this.buffReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		DebugMessageFactory.printInfoDebugMessage(ConfigReader.DEBUG_MODE, "Closed External Reader.");
	}
}
