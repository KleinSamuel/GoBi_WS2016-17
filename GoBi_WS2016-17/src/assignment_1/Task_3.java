package assignment_1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

import crawling.EnsemblCrawler;
import gtf.Gene;
import gtf.GenomeAnnotation;
import gtf.ThreadHandler;
import gtf.Transcript;
import io.AllroundFileReader;
import io.AllroundFileWriter;
import io.ConfigHelper;
import io.ConfigReader;
import javafx.util.Pair;
import plotting.LinePlot;

public class Task_3 {

	public void execute_task_3() {

		ConfigHelper ch = new ConfigHelper();

		HashMap<String, String> fileMap = ConfigReader.readFilepathConfig(ch.getDefaultConfigPath("gtf-paths.txt"),"\t", new String[] { "#" });

		/* biotype - file - amount transcripts - amount genes having this many transcripts */
		HashMap<String, TreeMap<String, TreeMap<Integer, Integer>>> biotypesGeneTransCount = new HashMap<>();

		HashMap<String, HashMap<Gene, Integer>> amountMap = new HashMap<>();

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

						amountMap.get(biotypeGeneEntry.getKey()).put(g, g.getTranscripts().size());

					} else {

						HashMap<Gene, Integer> tmp = new HashMap<>();
						tmp.put(g, g.getTranscripts().size());
						amountMap.put(biotypeGeneEntry.getKey(), tmp);

					}
				}
			}
		}

		TreeMap<String, TreeMap<Integer, Integer>> treeMap;

		TreeMap<String, String> annotMap = AllroundFileReader.readAnnotation(ch.getDefaultConfigPath("annot.map"));

		ArrayList<String> pathList = new ArrayList<>();
		ArrayList<ArrayList<String[]>> infoList = new ArrayList<>();
		
		HashMap<String, Integer> biotypesUnSorted = new HashMap<>();
		
		int current = 0;
		
		/* get amount genes with multiple transcripts */
		for (Entry<String, TreeMap<String, TreeMap<Integer, Integer>>> s : biotypesGeneTransCount.entrySet()) {
			current = 0;
			for(Entry<String, TreeMap<Integer, Integer>> entry : s.getValue().entrySet()){
				for(Entry<Integer, Integer> i : entry.getValue().entrySet()){
					if(i.getKey() > 1){
						current += i.getValue();
					}
				}
			}
			biotypesUnSorted.put(s.getKey(), current);
		}
		
		TreeMapByValueSorterStringInteger tms = new TreeMapByValueSorterStringInteger(biotypesUnSorted);
		
		TreeMap<String, Integer> biotypesSorted = new TreeMap<>(tms);
		biotypesSorted.putAll(biotypesUnSorted);
		

//		for (Entry<String, TreeMap<String, TreeMap<Integer, Integer>>> s : biotypesGeneTransCount.entrySet()) {

		for (String biot : biotypesSorted.keySet()){
			
			TreeMap<String, TreeMap<Integer, Integer>> s = biotypesGeneTransCount.get(biot);
			
			treeMap = new TreeMap<>();
			treeMap.putAll(s);

			int maxX = 0;
			int maxY = 0;

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

					if (s2.getKey() < 1) {
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

			LinePlot lp = new LinePlot(new Pair<Vector<Vector<Object>>, Vector<Vector<Object>>>(vectorOfVectors1, vectorOfVectors2), biot, "num tr/genes", "num genes", maxX, maxY, false, false);

			ArrayList<String[]> tmp = new ArrayList<>();

			int cnt = 0;

			TreeMapByValueSorterGeneInteger tvs = new TreeMapByValueSorterGeneInteger(amountMap.get(biot));
			
			TreeMap<Gene, Integer> tmpMap = new TreeMap<>(tvs);
			
			tmpMap.putAll(amountMap.get(biot));

			for (Entry<Gene, Integer> e : tmpMap.entrySet()) {

				if (cnt == 11) {
					break;
				}
				
				int nProts = 0;
				
				for(Transcript t : e.getKey().getTranscripts().values()){
					nProts += t.getCds().getParts().size() > 0 ? 1 : 0;
				}
				
				String symbol = e.getKey().getSymbol();
				String id = e.getKey().getId();
				String chrId = e.getKey().getChromosomeID();
				String strandSymbol = e.getKey().isOnNegativeStrand() ? "-" : "+";
				String biotype = biot;
				String startEnd = e.getKey().getStart()+"-"+e.getKey().getStop();
				String numTrans = "num transcripts: "+e.getKey().getTranscripts().size();
				String numProts = "num proteins: "+nProts;
				
				tmp.add(new String[]{EnsemblCrawler.getUrlForGeneID(id), symbol+" "+id+" ("+chrId+""+strandSymbol+" "+biotype+" "+startEnd+") "+numTrans+" "+numProts});

				cnt++;
			}

			pathList.add(ch.getDefaultOutputPath() + "" + biot);
			infoList.add(tmp);

			lp.addLegendVector(legendLabels);

			lp.plot();

		}

		AllroundFileWriter.createHTMLforPlots(ch.getDefaultOutputPath() + "NumTransPerBiotype.html", pathList, infoList, true);

	}
	
	class TreeMapByValueSorterGeneInteger implements Comparator<Gene> {
		HashMap<Gene, Integer> base;

		public TreeMapByValueSorterGeneInteger(HashMap<Gene, Integer> base) {
			this.base = base;
		}

		@Override
		public int compare(Gene o1, Gene o2) {
			if (base.get(o1) >= base.get(o2)) {
				return -1;
			} else {
				return 1;
			}
		}
	}
	
	class TreeMapByValueSorterStringInteger implements Comparator<String> {
		HashMap<String, Integer> base;

		public TreeMapByValueSorterStringInteger(HashMap<String, Integer> base) {
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
	
}
