package tasks;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;

import debugStuff.DebugMessageFactory;
import gtf.GenomeAnnotation;
import gtf.ThreadHandler;
import io.AllroundFileReader;
import io.AllroundFileWriter;
import io.ConfigHelper;
import io.ConfigReader;
import javafx.util.Pair;
import plotting.BarPlot;

public class Assignment1 {
	
	public void task_1(){
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
	
	public void task_2(){
		
		AllroundFileReader fr = new AllroundFileReader();
		ConfigHelper ch = new ConfigHelper();
		
		HashMap<String, HashMap<String, Integer>> s = fr.readXMLForTask1(ch.getDefaultOutputPath()+"biotypes_genes_organisms.xml");
		
		HashMap<String, String> annotMap = AllroundFileReader.readAnnotation(ch.getDefaultOutputPath()+"annot.map");
		
		for(Entry<String, HashMap<String, Integer>> entryMain : s.entrySet()){
			
			if(!entryMain.getKey().equals("protein_coding")){
				continue;
			}

			Vector<Object> values = new Vector<>(entryMain.getValue().values());
			Vector<Object> descr = new Vector<>();
			
			for(Entry<String, Integer> entrySub : entryMain.getValue().entrySet()){	
				descr.add(annotMap.get(entrySub.getKey()+".gtf"));
			}
			
			Pair<Vector<Object>, Vector<Object>> tmpVector = new Pair<>(values, descr);
			
			BarPlot bp = new BarPlot(tmpVector, entryMain.getKey(), "", "");
			bp.plot();
			
		}
		
	}

	public static void main(String[] args) {
		
		Assignment1 as1 = new Assignment1();
		
		as1.task_2();
		
	}
	
}
