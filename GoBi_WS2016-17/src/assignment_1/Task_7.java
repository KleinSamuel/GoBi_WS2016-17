package assignment_1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.stream.Collectors;

import debugStuff.DebugMessageFactory;
import gtf.Chromosome;
import gtf.Gene;
import gtf.GenomeAnnotation;
import gtf.ThreadHandler;
import gtf.Transcript;
import io.AllroundFileWriter;
import io.ConfigHelper;
import io.ConfigReader;
import javafx.util.Pair;
import plotting.LinePlot;

public class Task_7 {

	public void execute_task_7() {

		ConfigHelper ch = new ConfigHelper();

		HashMap<String, String> fileMap = ConfigReader.readFilepathConfig(ch.getDefaultConfigPath("gtf-paths.txt"),
				"\t", new String[] { "#" });

		ThreadHandler th;
		GenomeAnnotation gaGC10 = null, gaGC25 = null;

		for (Entry<String, String> entry : fileMap.entrySet()) {

			if (entry.getKey().equals("gencode.v10.annotation.gtf")) {
				th = new ThreadHandler();
				th.startThreads(entry.getValue());
				gaGC10 = th.getGenomeAnnotation();
			} else if (entry.getKey().equals("gencode.v25.annotation.gtf")) {
				th = new ThreadHandler();
				th.startThreads(entry.getValue());
				gaGC25 = th.getGenomeAnnotation();
			} else {
				continue;
			}
		}

		/* check if one annotation is null */
		if ((gaGC10 == null) || (gaGC25 == null)) {
			DebugMessageFactory.printErrorDebugMessage(true, "GenomeAnnotation was null.");
			System.exit(1);
		}

		/* holds paths for plot to add the to HTML */
		ArrayList<String> pathList = new ArrayList<>();

		/*
		 * holds for every chromosome a map of genes with the corresponding
		 * difference
		 */
		HashMap<String, HashMap<String, Integer>> geneDistance = new HashMap<>();

		/* HashMap for the gene length */
		HashMap<String, Object[]> geneLength = new HashMap<>();

		ArrayList<Integer> transcriptDifference = new ArrayList<>();
		ArrayList<Integer> cdsDifference = new ArrayList<>();

		/* check if gene is unique or in both files */
		HashSet<String> genesFile1 = new HashSet<>();
		HashSet<String> genesFile2 = new HashSet<>();

		for (Chromosome c : gaGC10.getChromosomeList().values()) {
			genesFile1.addAll(c.getGenes().keySet().stream().map(s -> s.substring(0, s.lastIndexOf(".")))
					.collect(Collectors.toSet()));
		}

		for (Chromosome c : gaGC25.getChromosomeList().values()) {
			genesFile2.addAll(c.getGenes().keySet().stream().map(s -> s.substring(0, s.lastIndexOf(".")))
					.collect(Collectors.toSet()));
		}

		HashSet<String> genesOnBoth = new HashSet<>(genesFile1);
		HashSet<String> genesUnique = new HashSet<>();

		genesOnBoth.retainAll(genesFile2);

		/*
		 * holds cutted id for all genes which are in both files and have
		 * changed chromosome
		 */
		HashSet<String> changed = new HashSet<>();

		for (Entry<String, Chromosome> chromosomeEntry : gaGC10.getChromosomeList().entrySet()) {

			ArrayList<String> differentGenes = new ArrayList<>(chromosomeEntry.getValue().getGenes().keySet().stream()
					.map(s -> s.substring(0, s.lastIndexOf("."))).collect(Collectors.toSet()));

			HashMap<String, Integer> tmp = new HashMap<>();
			ArrayList<Integer> tmpGeneLength = new ArrayList<>();

			/* check if same chromosome in both annotations */
			if (gaGC25.getChromosomeList().keySet().contains(chromosomeEntry.getKey())) {

				Chromosome tmpChrom = gaGC25.getChromosomeList().get(chromosomeEntry.getKey());

				Set<String> geneSet = tmpChrom.getGenes().keySet().stream().map(s -> s.substring(0, s.lastIndexOf(".")))
						.collect(Collectors.toSet());

				/* get all genes on chromosome */
				for (String geneId : geneSet) {

					/* check if gene on both chromosomes */
					if (differentGenes.contains(geneId)) {
						differentGenes.remove(geneId);

						Gene g1 = getGeneWithRegex(geneId, chromosomeEntry.getValue().getGenes());
						Gene g2 = getGeneWithRegex(geneId, tmpChrom.getGenes());

						int startDiff = Math.abs(g1.getStart() - g2.getStart());
						int endDiff = Math.abs(g1.getStop() - g2.getStop());

						tmp.put(geneId, Math.min(startDiff, endDiff));

						tmpGeneLength
								.add(Math.abs((g1.getStop() + 1 - g1.getStart()) - (g2.getStop() + 1 - g2.getStart())));

						/* compute transcript difference */
						transcriptDifference.add(Math.abs(g1.getTranscripts().size() - g2.getTranscripts().size()));

						int g1CDS = 0;
						int g2CDS = 0;

						/* compute cds difference */
						for (Transcript t : g1.getTranscripts().values()) {
							g1CDS += t.getCds().getParts().size();
						}
						for (Transcript t : g2.getTranscripts().values()) {
							g2CDS += t.getCds().getParts().size();
						}

						cdsDifference.add(Math.abs(g1CDS - g2CDS));

					} else {
						if (genesOnBoth.contains(geneId)) {
							changed.add(geneId);
						}
					}
				}

				for (String s : differentGenes) {
					if (genesOnBoth.contains(s)) {
						changed.add(s);
					}
				}

			} else {
				DebugMessageFactory.printInfoDebugMessage(true,
						"Chromosome " + chromosomeEntry.getKey() + " is not contained in gencode.v25.");
				continue;
			}

			geneDistance.put(chromosomeEntry.getKey(), tmp);
			geneLength.put(chromosomeEntry.getKey(), tmpGeneLength.stream().sorted().toArray());
		}

		// System.out.println("GENES CHANGED : "+changed.size());
		// System.out.println("GENES NOT CHANGED :
		// "+(genesOnBoth.size()-changed.size()));

		/* SECOND SUBTASK */

		Vector<Vector<Object>> vectorOfVectors1 = new Vector<>();
		Vector<Vector<Object>> vectorOfVectors2 = new Vector<>();

		ArrayList<Integer> tmpList = new ArrayList<>();

		for (Entry<String, HashMap<String, Integer>> entry : geneDistance.entrySet()) {
			tmpList.addAll(entry.getValue().values().stream().sorted().collect(Collectors.toList()));
		}

		Pair<Vector<Object>, Vector<Object>> tmp = cumulativeSum(tmpList.toArray());

		vectorOfVectors1.add(tmp.getKey());
		vectorOfVectors2.add(tmp.getValue());

		int minX = (int) tmp.getKey().get(0);
		int minY = (int) tmp.getValue().get(0);
		int maxX = (int) tmp.getKey().get(tmp.getKey().size() - 1);
		int maxY = (int) tmp.getValue().get(tmp.getValue().size() - 1);

		LinePlot lp = new LinePlot(
				new Pair<Vector<Vector<Object>>, Vector<Vector<Object>>>(vectorOfVectors1, vectorOfVectors2),
				"Chromsomal Distance", "distance", "Amount genes", minX, minY, maxX, maxY, true, false);
		lp.showLegend = false;
		lp.filename = "chrdist";
		lp.plot();

		pathList.add(ch.getDefaultOutputPath() + lp.filename);

		/* THIRD SUBTASK */

		vectorOfVectors1 = new Vector<>();
		vectorOfVectors2 = new Vector<>();

		tmpList = new ArrayList<>();

		for (Entry<String, Object[]> entry : geneLength.entrySet()) {
			for (Object i : entry.getValue()) {
				tmpList.add((int) i);
			}
		}

		tmp = cumulativeSum(tmpList.stream().sorted().toArray());

		vectorOfVectors1.add(tmp.getKey());
		vectorOfVectors2.add(tmp.getValue());

		minX = (int) tmp.getKey().get(0);
		minY = (int) tmp.getValue().get(0);
		maxX = (int) tmp.getKey().get(tmp.getKey().size() - 1);
		maxY = (int) tmp.getValue().get(tmp.getValue().size() - 1);

		lp = new LinePlot(new Pair<Vector<Vector<Object>>, Vector<Vector<Object>>>(vectorOfVectors1, vectorOfVectors2),
				"Gene Length Differences", "length difference in bp", "amount genes", minX, minY, maxX, maxY, true, false);
		lp.showLegend = false;
		lp.filename = "glengthdiff";
		lp.plot();

		pathList.add(ch.getDefaultOutputPath() + lp.filename);

		/* FOURTH SUBTASK */

		tmp = cumulativeSum(transcriptDifference.stream().sorted().collect(Collectors.toList()).toArray());

		minX = (int) tmp.getKey().get(0);
		minY = (int) tmp.getValue().get(0);
		maxX = (int) tmp.getKey().get(tmp.getKey().size() - 1);
		maxY = (int) tmp.getValue().get(tmp.getValue().size() - 1);

		vectorOfVectors1 = new Vector<>();
		vectorOfVectors2 = new Vector<>();

		vectorOfVectors1.add(tmp.getKey());
		vectorOfVectors2.add(tmp.getValue());

		tmp = cumulativeSum(cdsDifference.stream().sorted().collect(Collectors.toList()).toArray());

		vectorOfVectors1.add(tmp.getKey());
		vectorOfVectors2.add(tmp.getValue());

		minX = Math.min(minX, (int) tmp.getKey().get(0));
		minY = Math.min(minY, (int) tmp.getValue().get(0));
		maxX = Math.max(maxX, (int) tmp.getKey().get(tmp.getKey().size() - 1));
		maxY = Math.max(maxY, (int) tmp.getValue().get(tmp.getValue().size() - 1));

		Vector<Object> labels = new Vector<>();

		labels.add("transcript");
		labels.add("cds");

		lp = new LinePlot(new Pair<Vector<Vector<Object>>, Vector<Vector<Object>>>(vectorOfVectors1, vectorOfVectors2),
				"difference #transcripts and #cds", "difference", "amount genes", minX, minY, maxX, maxY, true, false);
		lp.filename = "andiff";
		lp.showLegend = true;
		lp.addLegendVector(labels);
		lp.plot();

		pathList.add(ch.getDefaultOutputPath() + lp.filename);

		AllroundFileWriter.createHTMLforPlots(ch.getDefaultOutputPath() + "genome_versions.html", pathList, null, true);
	}

	/**
	 * Return a Gene from the given HashMap<String, Gene>.
	 * 
	 * @param id String substring of a key in the HashMap
	 * @param map HashMap<String, Gene>
	 * @return Gene
	 */
	public static Gene getGeneWithRegex(String id, HashMap<String, Gene> map) {

		for (Entry<String, Gene> s : map.entrySet()) {
			if (s.getKey().substring(0, id.length()).equals(id)) {
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
		
		return new Pair<Vector<Object>, Vector<Object>>(first, second);
	}

}
