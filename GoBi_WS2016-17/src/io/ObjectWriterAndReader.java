package io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import debugStuff.DebugMessageFactory;

public class ObjectWriterAndReader {

	
	public static void writeObjectToFile(Object o, String name){
		
		try {
			
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ConfigReader.readConfig().get("object_directory")+"/"+name+".ga"));
			
			oos.writeObject(o);
			
			oos.flush();
			oos.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DebugMessageFactory.printNormalDebugMessage(true, "Stored object in file.");
		
	}
	
	public static Object readObjectFromFile(String name){
		
		Object out = null;
		
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ConfigReader.readConfig().get("object_directory")+"/"+name+".ga"));
		
			out = ois.readObject();
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	
		return out;
	}
}
