package task1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import genomeAnnotation.Chromosome;
import genomeAnnotation.Gene;
import genomeAnnotation.GenomeAnnotation;

public class OverlappingGenes {

	private GenomeAnnotation ga;
	private String outputDirectory;
	private HashSet<String> biotypes;

	// <biotypePairAB(split by/)<#overlapping_genes, #genes>>
	private HashMap<String, HashMap<Integer, HashMap<Integer, Integer>>> overlapsPerBiotype;

	public OverlappingGenes(GenomeAnnotation ga, String outputDir) {
		this.ga = ga;
		outputDirectory = outputDir;
		File directory = new File(outputDirectory);
		if (!directory.exists()) {
			System.out.println("directory does not exist! dir: " + directory.getAbsolutePath());
			System.exit(1);
		}
		if (!directory.isFile()) {
			System.out.println("dir is a file not a directory! file: " + directory.getAbsolutePath());
			System.exit(1);
		}
		biotypes = new HashSet<>();
		overlapsPerBiotype = new HashMap<>();
	}

	public void writeOverlappingGenesToFile() {
		File outputFile = new File(outputDirectory + "/" + ga.getName() + "_overlapping_genes.txt");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
			bw.write(getHeaderLine() + "\n");
			for (Chromosome c : ga.getChromosomesInFileOrder()) {
				LinkedList<Gene> genesOverlapping = null;
				for (Gene g : c.getAllGenesSorted()) {
					genesOverlapping = c.getAllGenesSorted().getIntervalsIntersecting(g.getStart(), g.getStop(),
							new LinkedList<Gene>());
					for (Gene gOverlapping : genesOverlapping) {
						if (!g.equals(gOverlapping) && gOverlapping.getStart() > g.getStart())
							bw.write(getNextLine(c, g, gOverlapping) + "\n");
					}
					biotypes.add(g.getBiotype());
				}
			}
			bw.close();
		} catch (IOException e) {
			System.out.println(e.getStackTrace());
			System.exit(1);
		}
	}

	public void addOverlappingGenes(Gene g, LinkedList<Gene> overlappingGenes) {

		if (overlappingGenes.isEmpty())
			return;

		// 0 genes on same strand, 1 genes on different strand
		int strandComparison = -1;

		for (Gene overlappingG : overlappingGenes) {

		}

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
