package tasks;

import java.util.HashMap;
import java.util.Map.Entry;

import assignment_1.GenomeAnnotation;
import assignment_1.ThreadHandler;
import debugStuff.DebugMessageFactory;
import io.AllroundFileWriter;
import io.ConfigHelper;
import io.ConfigReader;

public class Assignment1_Task1 {

	public static void main(String[] args) {
		
		long start = System.currentTimeMillis();
		
		ConfigHelper ch = new ConfigHelper();
		
		HashMap<String, String> fileMap = ConfigReader.readFilepathConfig(ch.getDefaultConfigPath("gtf-paths.txt"), "\t", new String[]{"#"});
		
		HashMap<String, HashMap<String, Integer>> biotypesOrgansimnCount = new HashMap<>();
		
		for(Entry<String,String> entry : fileMap.entrySet()){
			
			ThreadHandler th = new ThreadHandler();
			th.startThreads(entry.getValue());
			GenomeAnnotation ga = th.getGenomeAnnotation();
			
			for(Entry<String, Integer> entry2 : ga.getAmountGenesPerBiotype().entrySet()){
				if(!biotypesOrgansimnCount.containsKey(entry2.getKey())){
					biotypesOrgansimnCount.put(entry2.getKey(), new HashMap<String, Integer>());
				}
				biotypesOrgansimnCount.get(entry2.getKey()).put(entry.getKey(), entry2.getValue());
			}
			
		}
		
		AllroundFileWriter.writeXMLForTask1("/home/k/kleins/Desktop/TestfileGOBI.xml", biotypesOrgansimnCount);
		
		long end = System.currentTimeMillis();
		
		DebugMessageFactory.printInfoDebugMessage(true, "TASK 1 TOOK "+(end-start)+" MILLISECONDS.");
		
	}
	
}
