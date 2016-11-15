package tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import crawling.EnsemblCrawler;
import debugStuff.DebugMessageFactory;
import gtf.Chromosome;
import gtf.Gene;
import gtf.GenomeAnnotation;
import gtf.ThreadHandler;
import io.AllroundFileReader;
import io.AllroundFileWriter;
import io.ConfigHelper;
import io.ConfigReader;
import javafx.util.Pair;
import plotting.BarPlot;
import plotting.LinePlot;

public class Assignment1 {

	public void task_1() {
		long start = System.currentTimeMillis();

		ConfigHelper ch = new ConfigHelper();

		HashMap<String, String> fileMap = ConfigReader.readFilepathConfig(ch.getDefaultConfigPath("gtf-paths.txt"), "\t", new String[] {"#"});

		HashMap<String, HashMap<String, Integer>> biotypesOrgansimnCount = new HashMap<>();
		StupidComparator sc = new StupidComparator(biotypesOrgansimnCount);
		TreeMap<String, HashMap<String, Integer>> biotypesOrgansimnCountSorted = new TreeMap<>(sc);
		
		ThreadHandler th;
		GenomeAnnotation ga;

		for (Entry<String, String> entry : fileMap.entrySet()) {

			th = new ThreadHandler();
			th.startThreads(entry.getValue());
			ga = th.getGenomeAnnotation();

			for (Entry<String, Integer> entry2 : ga.getAmountGenesPerBiotype().entrySet()) {
				if (!biotypesOrgansimnCount.containsKey(entry2.getKey())) {
					biotypesOrgansimnCount.put(entry2.getKey(), new HashMap<String, Integer>());
				}
				biotypesOrgansimnCount.get(entry2.getKey()).put(entry.getKey(), entry2.getValue());	
			}
		}

		biotypesOrgansimnCountSorted.putAll(biotypesOrgansimnCount);

		AllroundFileWriter.writeXMLForTask1(ch.getDefaultOutputPath() + "biotypes_genes_organisms.xml", biotypesOrgansimnCountSorted);
		
		long end = System.currentTimeMillis();

		DebugMessageFactory.printInfoDebugMessage(true, "TASK 1 TOOK " + (end - start) + " MILLISECONDS.");

	}

	class StupidComparator implements Comparator<String> {
		HashMap<String, HashMap<String, Integer>> base;

		public StupidComparator(HashMap<String, HashMap<String, Integer>> base) {
			this.base = base;
		}

		public int getTotalCount(HashMap<String, Integer> map) {
			int out = 0;
			for (Integer i : map.values()) {
				out += i;
			}
			return out;
		}

		@Override
		public int compare(String o1, String o2) {
			if (getTotalCount(base.get(o1)) >= getTotalCount(base.get(o2))) {
				return -1;
			} else {
				return 1;
			}
		}
	}

	class StupidComparator2 implements Comparator<String> {

		HashMap<String, HashMap<Integer, Integer>> base;

		public StupidComparator2(HashMap<String, HashMap<Integer, Integer>> base) {
			this.base = base;
		}

		public int getTotalCount(HashMap<Integer, Integer> map) {
			int out = 0;
			for (Integer i : map.keySet()) {
				out += 1;
			}
			return out;
		}

		@Override
		public int compare(String o1, String o2) {
			if (getTotalCount(base.get(o1)) >= getTotalCount(base.get(o2))) {
				return -1;
			} else {
				return 1;
			}
		}
	}

	/**
	 * Create a barplot for every biotype containing all given gtf files as x-axis and the number of genes as y-axis
	 */
	public void task_2() {

		AllroundFileReader fr = new AllroundFileReader();
		ConfigHelper ch = new ConfigHelper();

		HashMap<String, HashMap<String, Integer>> s = fr.readXMLForTask1(ch.getDefaultOutputPath() + "biotypes_genes_organisms.xml");
		StupidComparator sc = new StupidComparator(s);
		TreeMap<String, HashMap<String, Integer>> sorted = new TreeMap<>(sc);

		sorted.putAll(s);

		TreeMap<String, String> annotMap = AllroundFileReader.readAnnotation(ch.getDefaultConfigPath("annot.map"));
		ArrayList<String> pathList = new ArrayList<>();
		
		int counter = 1;

		for (Entry<String, HashMap<String, Integer>> entryMain : sorted.entrySet()) {

			Vector<Object> values = new Vector<>(entryMain.getValue().values());
			Vector<Object> descr = new Vector<>();

			for (Entry<String, Integer> entrySub : entryMain.getValue().entrySet()) {
				descr.add(annotMap.get(entrySub.getKey()));
			}
			
			/* add a bar for every file which does not contain any genes for this biotype */
			for(String string : annotMap.values()){
				if(!descr.contains(string)){
					descr.add(string);
					values.add(0);
				}
			}

			Pair<Vector<Object>, Vector<Object>> tmpVector = new Pair<>(values, descr);

			BarPlot bp = new BarPlot(tmpVector, entryMain.getKey(), "", "");
			bp.plot();

			pathList.add(ch.getDefaultOutputPath() + "" + entryMain.getKey());

			DebugMessageFactory.printInfoDebugMessage(true, "PLOTTED " + counter + " / " + s.entrySet().size());
			counter++;
		}

		AllroundFileWriter.createHTMLforPlots(ch.getDefaultOutputPath() + "genetypes.html", pathList, null);

	}

	
	class TreeMapByValueSorter implements Comparator<String> {
		HashMap<String, Integer> base;

		public TreeMapByValueSorter(HashMap<String, Integer> base) {
			this.base = base;
		}

		@Override
		public int compare(String o1, String o2) {
			if (base.get(o1) >= base.get(o2)) {
				return -1;
			} else {
				return 1;
			}
		}
	}

	public void task_3() {

		ConfigHelper ch = new ConfigHelper();
		EnsemblCrawler crawler = new EnsemblCrawler();

		HashMap<String, String> fileMap = ConfigReader.readFilepathConfig(ch.getDefaultConfigPath("gtf-paths.txt"),"\t", new String[] { "#" });

		HashMap<String, TreeMap<String, TreeMap<Integer, Integer>>> biotypesGeneTransCount = new HashMap<>();

		HashMap<String, HashMap<String, Integer>> amountMap = new HashMap<>();

		ThreadHandler th;
		GenomeAnnotation ga;

		for (Entry<String, String> entry : fileMap.entrySet()) {

			th = new ThreadHandler();
			th.startThreads(entry.getValue());
			ga = th.getGenomeAnnotation();

			HashMap<String, HashSet<Gene>> genesPerBiotype = ga.getGenesForEachBiotype();

			for (Entry<String, HashSet<Gene>> biotypeGeneEntry : genesPerBiotype.entrySet()) {

				for (Gene g : biotypeGeneEntry.getValue()) {

					int amountTranscripts = g.getTranscripts().size();

					/* if biotype exists */
					if (biotypesGeneTransCount.containsKey(biotypeGeneEntry.getKey())) {

						TreeMap<String, TreeMap<Integer, Integer>> tmp = biotypesGeneTransCount
								.get(biotypeGeneEntry.getKey());

						/* if file exists */
						if (tmp.containsKey(entry.getKey())) {
							TreeMap<Integer, Integer> tmp2 = tmp.get(entry.getKey());

							if (tmp2.containsKey(amountTranscripts)) {
								tmp2.put(amountTranscripts, tmp2.get(amountTranscripts) + 1);
							} else {
								tmp2.put(amountTranscripts, 1);
							}
						}
						/* if file does not exist */
						else {

							TreeMap<Integer, Integer> tmpMap = new TreeMap<>();
							tmpMap.put(amountTranscripts, 1);

							tmp.put(entry.getKey(), tmpMap);
						}
					}
					/* if biotype does not exist create new entry */
					else {
						TreeMap<Integer, Integer> tmpMap = new TreeMap<>();
						tmpMap.put(amountTranscripts, 1);

						TreeMap<String, TreeMap<Integer, Integer>> tmpMap2 = new TreeMap<>();
						tmpMap2.put(entry.getKey(), tmpMap);

						biotypesGeneTransCount.put(biotypeGeneEntry.getKey(), tmpMap2);
					}

					/* add do amount list */
					if (amountMap.containsKey(biotypeGeneEntry.getKey())) {

						amountMap.get(biotypeGeneEntry.getKey()).put(g.getId(), g.getTranscripts().size());

					} else {

						HashMap<String, Integer> tmp = new HashMap<>();
						tmp.put(g.getId(), g.getTranscripts().size());
						amountMap.put(biotypeGeneEntry.getKey(), tmp);

					}
				}
			}
		}

		TreeMap<String, TreeMap<Integer, Integer>> treeMap;

		TreeMap<String, String> annotMap = AllroundFileReader.readAnnotation(ch.getDefaultConfigPath("annot.map"));

		ArrayList<String> pathList = new ArrayList<>();
		ArrayList<ArrayList<String[]>> infoList = new ArrayList<>();

		for (Entry<String, TreeMap<String, TreeMap<Integer, Integer>>> s : biotypesGeneTransCount.entrySet()) {

			treeMap = new TreeMap<>();
			treeMap.putAll(s.getValue());

			int maxX = 0;
			int maxY = 0;

			 if(!s.getKey().equals("protein_coding")){
				 continue;
			 }

			Vector<Vector<Object>> vectorOfVectors1 = new Vector<>();
			Vector<Vector<Object>> vectorOfVectors2 = new Vector<>();

			Vector<Object> legendLabels = new Vector<>();

			for (Entry<String, String> annot : annotMap.entrySet()) {

				TreeMap<Integer, Integer> values = treeMap.get(annot.getKey());

				int counter = 0;

				Vector<Object> vTMP1 = new Vector<>();
				Vector<Object> vTMP2 = new Vector<>();

				if (values == null) {

					vTMP1.add(0);
					vTMP2.add(0);

					legendLabels.add(annot.getValue());

					vectorOfVectors1.add(vTMP1);
					vectorOfVectors2.add(vTMP2);

					continue;
				}

				for (Entry<Integer, Integer> s2 : values.entrySet()) {

					if (s2.getKey() <= 1) {
						continue;
					}

					maxX = Math.max(maxX, s2.getKey());

					vTMP1.add(s2.getKey());

					counter += s2.getValue();

					maxY = Math.max(maxY, counter);

					vTMP2.add(counter);

				}

				legendLabels.add(annot.getValue());

				vectorOfVectors1.add(vTMP1);
				vectorOfVectors2.add(vTMP2);
			}

			LinePlot lp = new LinePlot(new Pair<Vector<Vector<Object>>, Vector<Vector<Object>>>(vectorOfVectors1, vectorOfVectors2), s.getKey(), "num tr/genes", "num genes", maxX, maxY);

			ArrayList<String[]> tmp = new ArrayList<>();

			int cnt = 0;

			TreeMapByValueSorter tvs = new TreeMapByValueSorter(amountMap.get(s.getKey()));
			
			TreeMap<String, Integer> tmpMap = new TreeMap<>(tvs);
			
			tmpMap.putAll(amountMap.get(s.getKey()));

			for (Entry<String, Integer> e : tmpMap.entrySet()) {

				if (cnt == 11) {
					break;
				}

				tmp.add(crawler.getGeneInfo(e.getKey()));

				cnt++;
			}

			pathList.add(ch.getDefaultOutputPath() + "" + s.getKey());
			infoList.add(tmp);

			lp.addLegendVector(legendLabels);

			lp.plot();

		}

		AllroundFileWriter.createHTMLforPlots(ch.getDefaultOutputPath() + "NumTransPerBiotype.html", pathList,infoList);

	}
	
	public void task_7(){
		
		ConfigHelper ch = new ConfigHelper();

		HashMap<String, String> fileMap = ConfigReader.readFilepathConfig(ch.getDefaultConfigPath("gtf-paths.txt"), "\t", new String[] {"#"});
		
		ThreadHandler th;
		GenomeAnnotation gaGC10 = null, gaGC25 = null;

		for (Entry<String, String> entry : fileMap.entrySet()) {

			if(entry.getKey().equals("gencode.v10.annotation.gtf")){
				th = new ThreadHandler();
				th.startThreads(entry.getValue());
				gaGC10 = th.getGenomeAnnotation();
			}else if(entry.getKey().equals("gencode.v25.annotation.gtf")){
				th = new ThreadHandler();
				th.startThreads(entry.getValue());
				gaGC25 = th.getGenomeAnnotation();
			}else{
				continue;
			}
		}
		
		/* check if one annotation is null */
		if((gaGC10 == null) || (gaGC25 == null)){
			DebugMessageFactory.printErrorDebugMessage(true, "GenomeAnnotation was null.");
			System.exit(1);
		}
		
		/* holds number of genes which have mapped to another chromosome */
		int genesChanged = 0;
		int genesNotChanged = 0;
		
		
		/* holds for every chromosome a map of genes with the corresponding difference */
		HashMap<String, HashMap<String, Integer>> geneDistance = new HashMap<>();
		
		HashMap<String, Object[]> geneLength = new HashMap<>();
		
		for (Entry<String, Chromosome> chromosomeEntry : gaGC10.getChromosomeList().entrySet()) {
			
			ArrayList<String> differentGenes = new ArrayList<>(chromosomeEntry.getValue().getGenes().keySet().stream().map(s -> s.substring(0, s.lastIndexOf("."))).collect(Collectors.toSet()));
			
			HashMap<String, Integer> tmp = new HashMap<>();
			ArrayList<Integer> tmpGeneLength = new ArrayList<>();
			
			/* check if same chromosome in both annotations */
			if(gaGC25.getChromosomeList().keySet().contains(chromosomeEntry.getKey())){
				
				Chromosome tmpChrom = gaGC25.getChromosomeList().get(chromosomeEntry.getKey());
				
				Set<String> geneSet = tmpChrom.getGenes().keySet().stream().map(s -> s.substring(0, s.lastIndexOf("."))).collect(Collectors.toSet());
				
				/* get all genes on chromosome */
				for (String geneId : geneSet){
					
					/* check if gene on both chromosomes */
					if(differentGenes.contains(geneId)){
						differentGenes.remove(geneId);
						
						Gene g1 = getGeneWithRegex(geneId, chromosomeEntry.getValue().getGenes());
						Gene g2 = getGeneWithRegex(geneId, tmpChrom.getGenes());
						
						int startDiff = Math.abs(g1.getStart() - g2.getStart());
						int endDiff = Math.abs(g1.getStop() - g2.getStop());
						
						tmp.put(geneId, Math.min(startDiff, endDiff));
						
						tmpGeneLength.add(Math.abs((g1.getStop() + 1 - g1.getStart()) - (g2.getStop() + 1 - g2.getStart())));
						
						genesNotChanged++;
					}
				}

			}else{
				DebugMessageFactory.printInfoDebugMessage(true, "Chromosome "+chromosomeEntry.getKey()+" is not contained in gencode.v25.");
				continue;
			}
			genesChanged += differentGenes.size();
			
			geneDistance.put(chromosomeEntry.getKey(), tmp);
			geneLength.put(chromosomeEntry.getKey(), tmpGeneLength.stream().sorted().toArray());
		}
		
		System.out.println("GENES CHANGED : "+genesChanged);
		System.out.println("GENES NOT CHANGED : "+genesNotChanged);
		
		for (Entry<String, HashMap<String, Integer>> entry : geneDistance.entrySet()){
			
			System.out.println(entry.getKey());
			
			Object[] array = entry.getValue().values().stream().sorted().toArray();
			
			System.out.println("ARRAY:");
			System.out.println(Arrays.toString(array));
			
			Pair<Vector<Object>, Vector<Object>> tmp = cumulativeSum(array);
			Vector<Vector<Object>> vectorOfVectors1 = new Vector<>();
			Vector<Vector<Object>> vectorOfVectors2 = new Vector<>();
			
			vectorOfVectors1.add(tmp.getKey());
			vectorOfVectors2.add(tmp.getValue());
			
			int minX = (int)tmp.getKey().get(0);
			int minY = (int)tmp.getValue().get(0);
			int maxX = (int)tmp.getKey().get(tmp.getKey().size()-1);
			int maxY = (int)tmp.getValue().get(tmp.getValue().size()-1);
			
			LinePlot lp = new LinePlot(new Pair<Vector<Vector<Object>>, Vector<Vector<Object>>>(vectorOfVectors1, vectorOfVectors2), ""+entry.getKey(), "XLAB", "YLAB", minX, minY, maxX, maxY);
			
			lp.plot();
			
		}
		
//		for (Entry<String, Object[]> entry : geneLength.entrySet()){
//			
////			System.out.println(entry.getKey()+" : "+Arrays.toString(entry.getValue()));
//			
//			Vector<Vector<Object>> vectorOfVectors1 = new Vector<>();
//			Vector<Vector<Object>> vectorOfVectors2 = new Vector<>();
//
//			Vector<Object> v1 = new Vector<>();
//			Vector<Object> v2 = new Vector<>();
//			
//			
//			int counter = 0;
//			int counter2 = 0;
//			
//			for (int i = 0; i < entry.getValue().length-1; i++) {
//				
//				System.out.print((int)entry.getValue()[i]);
//				v1.add((int)entry.getValue()[i]);
//				counter2 = 0;
//				
//				while((int)entry.getValue()[i] == (int)entry.getValue()[i+1]){
//					
//					counter2++;
//					i++;
//				}
//				
//				counter += counter2;
//				v2.add(counter);
//				
//				System.out.println("\t\t"+counter);
//			}
//			
//			vectorOfVectors1.add(v1);
//			vectorOfVectors2.add(v2);
//			
//			LinePlot lp = new LinePlot(new Pair<Vector<Vector<Object>>, Vector<Vector<Object>>>(vectorOfVectors1, vectorOfVectors2), ""+entry.getKey(), "XLAB", "YLAB", (int)v1.get(v1.size()-1), (int)v2.get(v2.size()-1));
//			
//			lp.plot();
//			
//		}
		
	}
	
	/**
	 * Return a Gene from the given HashMap<String, Gene>.
	 * 
	 * @param id String substring of a key in the HashMap
	 * @param map HashMap<String, Gene>
	 * @return Gene
	 */
	public static Gene getGeneWithRegex(String id, HashMap<String, Gene> map){
		
		for (Entry<String, Gene> s : map.entrySet()){
			if(s.getKey().substring(0, id.length()).equals(id)){
				return s.getValue();
			}
		}
		return null;
	}
	
	/**
	 * Compute cumulative sum from an array
	 * First vector contains unique values
	 * Second vector contains cumulative sum
	 * 
	 * @param array
	 * @return Pair<Vector<Object>, Vector<Object>> pair of vectors.
	 */
	public static Pair<Vector<Object>, Vector<Object>> cumulativeSum(Object[] array){
		
		Vector<Object> first = new Vector<>();
		Vector<Object> second = new Vector<>();
		
		TreeMap<Integer, Integer> mapTMP = new TreeMap<>();
		
		for (int i = 0; i < array.length; i++) {
			if(mapTMP.containsKey((int)array[i])){
				mapTMP.put((int)array[i], mapTMP.get((int)array[i])+1);
			}else{
				mapTMP.put((int)array[i], 1);
			}
		}
		
		first.addAll(mapTMP.keySet());
		
		int counter = 0;
		for(Integer i : mapTMP.values()){
			counter += i;
			second.add(counter);
		}
		
		System.out.println("CUM SUM:");
		System.out.println(Arrays.toString(first.toArray()));
		System.out.println(Arrays.toString(second.toArray()));
		
		return new Pair<Vector<Object>, Vector<Object>>(first, second);
	}

	public static void main(String[] args) {

		Assignment1 as1 = new Assignment1();

//		as1.task_1();
//		as1.task_2();
//		as1.task_3();
		as1.task_7();

	}

}
