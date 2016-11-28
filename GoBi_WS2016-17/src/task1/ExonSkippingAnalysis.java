package task1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeSet;

import augmentedTree.IntervalTree;
import genomeAnnotation.Chromosome;
import genomeAnnotation.Exon;
import genomeAnnotation.Gene;
import genomeAnnotation.GenomeAnnotation;
import genomeAnnotation.Intron;
import genomeAnnotation.Transcript;
import util.TopTen;

public class ExonSkippingAnalysis {

	private GenomeAnnotation ga;
	private String outputDirectory;
	private File outputFile;
	private LinkedList<Gene> maxSkippedExons, maxSkippedBases;

	public ExonSkippingAnalysis(GenomeAnnotation ga, String outputDir) {
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
	}

	public File getOutputFile() {
		return outputFile;
	}

	public String getOutputDir() {
		return outputDirectory;
	}

	private HashMap<Gene, Integer> totalMaxExonSkipped = new HashMap<>(), totalMaxBasesSkipped = new HashMap<>();

	public void analyseExonSkippings() {
		outputFile = new File(outputDirectory + "/" + ga.getName() + "_exon_skipping_events.txt");
		System.out.println("calculating exon skippings");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
			bw.write(getHeaderLine() + "\n");
			for (Chromosome c : ga.getChromosomesInFileOrder()) {
				for (Gene g : c.getAllGenesSorted()) {
					LinkedList<ExonSkippingEvent> eses = analyseGene(g);
					for (ExonSkippingEvent ese : eses) {
						bw.write(getNextLine(ese) + "\n");
						Integer total = totalMaxExonSkipped.get(g);
						if (total == null)
							totalMaxExonSkipped.put(g, ese.getMaxSkippedExons());
						else
							totalMaxExonSkipped.put(g, total + ese.getMaxSkippedExons());
						total = totalMaxBasesSkipped.get(g);
						if (total == null)
							totalMaxBasesSkipped.put(g, ese.getMaxSkippedBases());
						else
							totalMaxBasesSkipped.put(g, total + ese.getMaxSkippedBases());
					}
				}
			}
			bw.close();
			System.out.println("finished");
			maxSkippedExons = new TopTen<Gene>(totalMaxExonSkipped).getTopTen();
			maxSkippedBases = new TopTen<Gene>(totalMaxBasesSkipped).getTopTen();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public LinkedList<Gene> getTopTenExon() {
		return maxSkippedExons;
	}

	public LinkedList<Gene> getTopTenBases() {
		return maxSkippedBases;
	}

	public int getTotalMaxExonSkipped(Gene g) {
		return totalMaxExonSkipped.get(g);
	}

	public int getTotalMaxBasesSkipped(Gene g) {
		return totalMaxBasesSkipped.get(g);
	}

	public String getTopTenExonSkippingText() {
		StringBuilder sb = new StringBuilder();
		for (Gene g : maxSkippedExons) {
			sb.append(g.getInfoLine()[2] + " maxSkippedExons: " + totalMaxExonSkipped.get(g) + " maxSkippedBases: "
					+ totalMaxBasesSkipped.get(g) + "\n");
		}
		if (sb.length() > 0)
			sb.deleteCharAt(sb.length());
		return sb.toString();
	}

	public String getTopTenBasesSkippingText() {
		StringBuilder sb = new StringBuilder();
		for (Gene g : maxSkippedBases) {
			sb.append(g.getInfoLine()[2] + " maxSkippedExons: " + totalMaxExonSkipped.get(g) + " maxSkippedBases: "
					+ totalMaxBasesSkipped.get(g) + "\n");
		}
		if (sb.length() > 0)
			sb.deleteCharAt(sb.length());
		return sb.toString();
	}

	// calculate all different introns in the transcripts and check if it is in
	// all transcripts or if there are exons in it
	public LinkedList<ExonSkippingEvent> analyseGene(Gene g) {

		LinkedList<ExonSkippingEvent> events = new LinkedList<>();

		// get all transcripts with a cds, calc their introns and store them
		// unique in HashMap, remember parental transcripts
		IntervalTree<Intron> allIntrons = null;
		HashMap<Integer, HashMap<Integer, HashSet<Transcript>>> uniqueIntronStartStops = new HashMap<>();
		HashMap<Integer, HashSet<Transcript>> stops = null;
		HashSet<Transcript> transcripts = null;
		for (Transcript t : g.getAllTranscriptsSorted()) {
			if (t.hasCDS()) {
				allIntrons = t.getIntrons();
				for (Intron intron : allIntrons) {
					stops = uniqueIntronStartStops.get(intron.getStart());
					if (stops == null) {
						stops = new HashMap<>();
						uniqueIntronStartStops.put(intron.getStart(), stops);
					}
					transcripts = stops.get(intron.getStop());
					if (transcripts == null) {
						transcripts = new HashSet<>();
						stops.put(intron.getStop(), transcripts);
					}
					transcripts.add(t);
				}
			}
		}
		TreeSet<ExonSkippingIntron> uniqueIntrons = new TreeSet<>();
		for (Entry<Integer, HashMap<Integer, HashSet<Transcript>>> e : uniqueIntronStartStops.entrySet()) {
			for (Entry<Integer, HashSet<Transcript>> stop : e.getValue().entrySet())
				uniqueIntrons.add(new ExonSkippingIntron(e.getKey(), stop.getKey(), stop.getValue()));
		}
		LinkedList<Exon> skippedExons;
		HashSet<Transcript> transcriptsContainingSkippedExons;
		for (ExonSkippingIntron exonSkippingIntron : uniqueIntrons) {
			// get all exons of gene in an intron --> parents are possible wts;
			skippedExons = g.getAllExonsSorted().getIntervalsSpannedBy(exonSkippingIntron.getStart(),
					exonSkippingIntron.getStop(), new LinkedList<>());
			transcriptsContainingSkippedExons = new HashSet<>();
			for (Exon skippedExon : skippedExons) {
				transcriptsContainingSkippedExons.addAll(skippedExon.getParentalTranscripts());
			}
			// check for transcripts with exons in an intron if it has an exon
			// ending with intron start - 1 and one starting with intron end + 1
			LinkedList<Transcript> wildtypes = new LinkedList<>();
			for (Transcript tr : transcriptsContainingSkippedExons) {
				if (tr.hasCDS() && checkForWildtype(tr, exonSkippingIntron))
					wildtypes.add(tr);
			}
			if (!wildtypes.isEmpty())
				events.add(new ExonSkippingEvent(g, exonSkippingIntron.getStart(), exonSkippingIntron.getStop(),
						wildtypes, exonSkippingIntron.getTranscriptsContainingThis()));
		}

		return events;

	}

	public boolean checkForWildtype(Transcript possibleWildtype, ExonSkippingIntron i) {

		boolean foundStart = false, foundStop = false;
		if (i.getStart() == possibleWildtype.getStart()) {
			foundStart = true;
		}
		if (i.getStop() == possibleWildtype.getStop())
			foundStop = true;
		if (foundStart && foundStop)
			return true;
		for (Exon e : possibleWildtype.getExons()) {
			if (e.getStop() < i.getStart() - 1)
				continue;
			if (e.getStart() > i.getStop() + 1)
				break;
			if (e.getStart() == i.getStop() + 1)
				foundStop = true;
			if (e.getStop() == i.getStart() - 1)
				foundStart = true;
			if (e.getStart() == i.getStop() + 1 && e.getStop() == i.getStart() - 1) {
				System.out.println("Error!! No exon in between!! Intron == Exon");
				System.exit(1);
			}
		}

		return foundStart && foundStop;

	}

	public String getHeaderLine() {
		return "id\tsymbol\tchr\tstrand\tnprots\tntrans\tSV\tWT\tSV_prots\tWT_prots\tmin_skipped_exon\tmax_skipped_exon\tmin_skipped_bases\tmax_skipped_bases";
	}

	public String getNextLine(ExonSkippingEvent ese) {
		StringBuilder sb = new StringBuilder();
		sb.append(ese.getGene().getId() + "\t" + ese.getGene().getName() + "\t" + ese.getGene().getChromosome().getID()
				+ "\t" + ese.getGene().getStrand() + "\t");
		sb.append(getTranscriptProteinCount(ese.getGene()) + "\t");
		sb.append(ese.getIntronStart() + ":" + ese.getIntronStop() + "\t");
		sb.append(ese.getWTintronsInSVintrons() + "\t");

		sb.append(ese.getSVprots() + "\t" + ese.getWTprots() + "\t");
		sb.append(ese.getMinMaxSkippingInfo());

		// for (Transcript tr : ese.getWildtypes())
		// sb.append(tr.getId() + "|");
		// sb.deleteCharAt(sb.length() - 1);
		// sb.append("\t");
		// for (Transcript tr : ese.getSplicedVariants())
		// sb.append(tr.getId() + "|");
		// sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public String getTranscriptProteinCount(Gene g) {
		StringBuilder sb = new StringBuilder();

		int nprots = 0, ntrans = 0;
		for (Transcript t : g.getAllTranscriptsSorted()) {
			ntrans++;
			if (t.hasCDS())
				nprots++;
		}
		sb.append(nprots + "\t" + ntrans);

		return sb.toString();
	}

	public class ExonSkippingIntron implements augmentedTree.Interval, Comparable<ExonSkippingIntron> {

		private int start, stop;
		private HashSet<Transcript> trsContainingThis;

		public ExonSkippingIntron(int start, int stop, HashSet<Transcript> trs) {
			this.start = start;
			this.stop = stop;
			this.trsContainingThis = trs;
		}

		@Override
		public int getStart() {
			return start;
		}

		@Override
		public int getStop() {
			return stop;
		}

		public HashSet<Transcript> getTranscriptsContainingThis() {
			return trsContainingThis;
		}

		@Override
		public int compareTo(ExonSkippingIntron o) {
			if (start != o.getStart())
				return Integer.compare(start, o.getStart());
			else
				return Integer.compare(stop, o.getStop());
		}

	}

}
