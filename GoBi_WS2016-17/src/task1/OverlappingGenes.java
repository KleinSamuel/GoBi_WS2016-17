package task1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;

import genomeAnnotation.Chromosome;
import genomeAnnotation.Gene;
import genomeAnnotation.GenomeAnnotation;
import plotR.RScriptCaller;

public class OverlappingGenes {

	private GenomeAnnotation ga;
	private String outputDirectory;

	// <biotypePairAB(split by _)<strandCombination<#overlapping_genes,
	// #genesWithThisNumberOfOverlaps>>>
	// not necessary to store disregarding strand --> easy to calculate in R
	// later
	// 0 = same strand; 1 = different strand; 2 = disregarding strand
	private HashMap<String, HashMap<Integer, HashMap<Integer, Integer>>> overlapsPerBiotype;

	public OverlappingGenes(GenomeAnnotation ga, String outputDir) {
		this.ga = ga;
		outputDirectory = outputDir;
		File directory = new File(outputDirectory);
		if (!directory.exists()) {
			System.out.println("directory does not exist! dir: " + directory.getAbsolutePath());
			System.exit(1);
		}
		if (directory.isFile()) {
			System.out.println("dir is a file not a directory! file: " + directory.getAbsolutePath());
			System.exit(1);
		}
		overlapsPerBiotype = new HashMap<>();
	}

	public void writeOverlappingGenesToFile() {
		File outputFile = new File(outputDirectory + "/" + ga.getName() + ".overlaps.tsv");
		System.out.println("calculating gene overlaps");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
			bw.write(getHeaderLine() + "\n");
			for (Chromosome c : ga.getChromosomesInFileOrder()) {
				LinkedList<Gene> genesOverlapping = null;
				for (Gene g : c.getAllGenesSorted()) {
					genesOverlapping = c.getAllGenesSorted().getIntervalsIntersecting(g.getStart(), g.getStop(),
							new LinkedList<Gene>());
					for (Gene gOverlapping : genesOverlapping) {
						if (!g.equals(gOverlapping) && gOverlapping.compareTo(g) > 0) {
							bw.write(getNextLine(c, g, gOverlapping) + "\n");
						}
					}
					addOverlappingGenes(g, genesOverlapping);
				}
			}
			bw.close();
			System.out.println("finished");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void addOverlappingGenes(Gene g, LinkedList<Gene> overlappingGenes) {

		if (overlappingGenes.isEmpty())
			return;

		// 0 genes on same strand, 1 genes on different strand
		int strandComparison = -1;
		String biotypeCombi = null;
		HashMap<Integer, HashMap<Integer, Integer>> inBiotypePair;
		HashMap<Integer, Integer> onStrand;

		// error precalculate partition in biotypes
		HashMap<String, HashMap<Integer, Integer>> numberOfOverlappingGenesPerBiotypePerStrandcomp = new HashMap<>();
		HashMap<Integer, Integer> countsOfOverlappingGenesPerStrandInBiotype = new HashMap<>();
		Integer count = 0, disregarding_strand = 2;
		for (Gene overlappingG : overlappingGenes) {
			if (g.equals(overlappingG))
				continue;
			// fehleranf�llig falls g.equals(overlappingG)
			if (g.getBiotype().equals(overlappingG.getBiotype()) && overlappingG.compareTo(g) < 0)
				continue;
			if (g.isOnNegativeStrand() != overlappingG.isOnNegativeStrand())
				strandComparison = 1;
			else
				strandComparison = 0;
			countsOfOverlappingGenesPerStrandInBiotype = numberOfOverlappingGenesPerBiotypePerStrandcomp
					.get(overlappingG.getBiotype());
			if (countsOfOverlappingGenesPerStrandInBiotype == null) {
				countsOfOverlappingGenesPerStrandInBiotype = new HashMap<>();
				numberOfOverlappingGenesPerBiotypePerStrandcomp.put(overlappingG.getBiotype(),
						countsOfOverlappingGenesPerStrandInBiotype);
			}
			count = countsOfOverlappingGenesPerStrandInBiotype.get(strandComparison);
			if (count == null) {
				countsOfOverlappingGenesPerStrandInBiotype.put(strandComparison, 1);
			} else {
				countsOfOverlappingGenesPerStrandInBiotype.put(strandComparison, count + 1);
			}
			count = countsOfOverlappingGenesPerStrandInBiotype.get(disregarding_strand);
			if (count == null) {
				countsOfOverlappingGenesPerStrandInBiotype.put(disregarding_strand, 1);
			} else {
				countsOfOverlappingGenesPerStrandInBiotype.put(disregarding_strand, count + 1);
			}

		}
		// put into main count map
		for (Entry<String, HashMap<Integer, Integer>> e1 : numberOfOverlappingGenesPerBiotypePerStrandcomp
				.entrySet()) {
			biotypeCombi = g.getBiotype() + "_" + e1.getKey();
			inBiotypePair = overlapsPerBiotype.get(biotypeCombi);
			if (inBiotypePair == null) {
				inBiotypePair = new HashMap<>();
				overlapsPerBiotype.put(biotypeCombi, inBiotypePair);
			}
			for (Entry<Integer, Integer> e2 : e1.getValue().entrySet()) {
				onStrand = inBiotypePair.get(e2.getKey());
				if (onStrand == null) {
					onStrand = new HashMap<>();
					inBiotypePair.put(e2.getKey(), onStrand);
				}
				count = onStrand.get(e2.getValue());
				if (count == null) {
					onStrand.put(e2.getValue(), 1);
				} else {
					onStrand.put(e2.getValue(), count + 1);
				}
			}

		}

	}

	public ArrayList<String> writeOverlapsPerBiotypeToFile(String pathToJarDir) {
		HashMap<Integer, Integer> countsToWrite;
		TreeMap<Integer, Integer> countsSortedAndFilled;
		File next = null;
		ArrayList<String> plotPaths = new ArrayList<>();
		for (Entry<String, HashMap<Integer, HashMap<Integer, Integer>>> e1 : overlapsPerBiotype.entrySet()) {
			next = new File(outputDirectory + "/tmp/" + ga.getName() + "/");
			next.mkdirs();
			next = new File(outputDirectory + "/tmp/" + ga.getName() + "/" + ga.getName() + "_" + e1.getKey()
					+ ".overlapStats");
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(next));
				for (int strandComp = 0; strandComp < 3; strandComp++) {
					countsToWrite = e1.getValue().get(strandComp);
					if (countsToWrite == null) {
						bw.write("0\n0\n\n");
					} else {
						countsSortedAndFilled = getCountMapSortedAndFilled(countsToWrite);
						for (Integer i : countsSortedAndFilled.keySet())
							bw.write(i + "\t");
						bw.write("\n");
						for (Integer i : countsSortedAndFilled.values())
							bw.write(i + "\t");
						bw.write("\n\n");
					}
				}
				bw.close();
				LinkedList<String> args = new LinkedList<>();
				args.add(next.getAbsolutePath().replace(".overlapStats", "_overlapStats.png"));
				args.add(e1.getKey());
				plotPaths.add(next.getAbsolutePath().replace(".overlapStats", "_overlapStats"));
				new RScriptCaller(pathToJarDir + "OverlapPlotter.R", next.getAbsolutePath(), args).execRScript();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		return plotPaths;

	}

	public TreeMap<Integer, Integer> getCountMapSortedAndFilled(HashMap<Integer, Integer> in) {
		TreeMap<Integer, Integer> ret = new TreeMap<>(in);
		for (int i = 0; i < ret.lastKey(); i++) {
			if (!ret.containsKey(i))
				ret.put(i, 0);
		}
		return ret;
	}

	public String getHeaderLine() {
		return "chr\tgeneid1\tgeneid2\tstrand1\tstrand2\tbiotype1\tbiotype2\tnum_overlap_bases";
	}

	public String getNextLine(Chromosome c, Gene g1, Gene g2) {
		return c.getID() + "\t" + g1.getId() + "\t" + g2.getId() + "\t" + g1.getStrand() + "\t" + g2.getStrand() + "\t"
				+ g1.getBiotype() + "\t" + g2.getBiotype() + "\t" + calcOverlapOfTwoGenes(g1, g2);
	}

	private int calcOverlapOfTwoGenes(Gene g1, Gene g2) {
		int minStart = Math.min(g1.getStart(), g2.getStart()), maxStop = Math.max(g1.getStop(), g2.getStop());

		return Math.max(0, maxStop - minStart + 1 - Math.abs(g2.getStop() - g1.getStop())
				- Math.abs(g2.getStart() - g1.getStart()));
	}

}
