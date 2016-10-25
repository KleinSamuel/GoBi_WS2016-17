package tasks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
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
		StupidComparator sc = new StupidComparator(biotypesOrgansimnCount);
		TreeMap<String, HashMap<String, Integer>> biotypesOrgansimnCountSorted = new TreeMap<>(sc);
		
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
		
		biotypesOrgansimnCountSorted.putAll(biotypesOrgansimnCount);
		
		AllroundFileWriter.writeXMLForTask1(ch.getDefaultOutputPath()+"biotypes_genes_organisms.xml", biotypesOrgansimnCountSorted);
		
		long end = System.currentTimeMillis();
		
		DebugMessageFactory.printInfoDebugMessage(true, "TASK 1 TOOK "+(end-start)+" MILLISECONDS.");
		
	}
	
	class StupidComparator implements Comparator<String>{

		HashMap<String, HashMap<String, Integer>> base;
		
		public StupidComparator(HashMap<String, HashMap<String, Integer>> base) {
			this.base = base;
		}
		
		public int getTotalCount(HashMap<String, Integer> map){
			int out = 0;
			for(Integer i : map.values()){
				out+=i;
			}
			return out;
		}
		
		@Override
		public int compare(String o1, String o2) {
			if(getTotalCount(base.get(o1)) >= getTotalCount(base.get(o2))){
				return -1;
			}else{
				return 1;
			}
		}
		
	}
	
	public void task_2(){
		
		AllroundFileReader fr = new AllroundFileReader();
		ConfigHelper ch = new ConfigHelper();
		
		HashMap<String, HashMap<String, Integer>> s = fr.readXMLForTask1(ch.getDefaultOutputPath()+"biotypes_genes_organisms.xml");
		StupidComparator sc = new StupidComparator(s);
		TreeMap<String, HashMap<String, Integer>> sorted = new TreeMap<>(sc);
		
		sorted.putAll(s);
		
		HashMap<String, String> annotMap = AllroundFileReader.readAnnotation(ch.getDefaultConfigPath("annot.map"));
		ArrayList<String> pathList = new ArrayList<>();
		int counter = 1;
		
		for(Entry<String, HashMap<String, Integer>> entryMain : sorted.entrySet()){

			Vector<Object> values = new Vector<>(entryMain.getValue().values());
			Vector<Object> descr = new Vector<>();
			
			for(Entry<String, Integer> entrySub : entryMain.getValue().entrySet()){	
				descr.add(annotMap.get(entrySub.getKey()+".gtf"));
			}
			
			Pair<Vector<Object>, Vector<Object>> tmpVector = new Pair<>(values, descr);
			
			BarPlot bp = new BarPlot(tmpVector, entryMain.getKey(), "", "");
			bp.plot();
			
			pathList.add(ch.getDefaultOutputPath()+""+entryMain.getKey());
			
			DebugMessageFactory.printInfoDebugMessage(true, "PLOTTED "+counter+" / "+s.entrySet().size());
			counter++;
		}
		
		AllroundFileWriter.createHTMLforBarplots(ch.getDefaultOutputPath()+"genetypes.html", pathList);
		
	}
	
	public void task_3(){
		
		
		
	}

	public static void main(String[] args) {
		
		Assignment1 as1 = new Assignment1();
		
		as1.task_1();
//		as1.task_2();
		
	}
	
}
