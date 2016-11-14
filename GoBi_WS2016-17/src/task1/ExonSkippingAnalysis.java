package task1;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeSet;

import genomeAnnotation.Exon;
import genomeAnnotation.Gene;
import genomeAnnotation.GenomeAnnotation;
import genomeAnnotation.Intron;
import genomeAnnotation.Transcript;

public class ExonSkippingAnalysis {

	private GenomeAnnotation ga;

	public ExonSkippingAnalysis(GenomeAnnotation ga) {
		this.ga = ga;
	}

	// calculate all different introns in the transcripts and check if it is in
	// all transcripts or if there are exons in it
	public LinkedList<ExonSkippingEvent> analyseGene(Gene g) {

		LinkedList<ExonSkippingEvent> events = new LinkedList<>();

		// get all transcripts with a cds, calc their introns and store them
		// unique in hashMap
		TreeSet<Intron> allIntrons = null;
		HashMap<Integer, HashMap<Integer, HashSet<Transcript>>> uniqueIntronStartStops = new HashMap<>();
		HashMap<Integer, HashSet<Transcript>> stops = null;
		HashSet<Transcript> transcripts = null;
		for (Transcript t : g.getAllTranscriptsSorted()) {
			if (t.hasCDS()) {
				allIntrons = calcIntrons(t);
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
			skippedExons = g.getAllExonsSorted().getIntervalsIntersecting(exonSkippingIntron.getStart(),
					exonSkippingIntron.getStop(), new LinkedList<>());
			transcriptsContainingSkippedExons = new HashSet<>();
			for (Exon skippedExon : skippedExons)
				transcriptsContainingSkippedExons.addAll(skippedExon.getParentalTranscripts());

			// check for transcripts with exons in an intron if it has an exon
			// ending with intron start - 1 and one starting with intron end + 1
			for (Transcript tr : transcriptsContainingSkippedExons) {
				checkForWildtype(tr, exonSkippingIntron);
			}

		}

		return events;

	}

	public boolean checkForWildtype(Transcript possibleWildtype, ExonSkippingIntron i) {
		
	}

	public TreeSet<Intron> calcIntrons(Transcript t) {
		TreeSet<Intron> introns = new TreeSet<>();
		Iterator<Exon> exonIt = t.getExons().iterator();
		Exon current = null, next = null;
		if (exonIt.hasNext()) {
			current = exonIt.next();
			if (current.getStart() > t.getStart())
				introns.add(new Intron(t.getStart(), current.getStart() - 1, "", t.isOnNegativeStrand()));
		}
		while (exonIt.hasNext()) {
			next = exonIt.next();
			// check that they are not directly one after the other
			if (current.getStop() < next.getStart() - 1)
				introns.add(new Intron(current.getStop() + 1, next.getStart() - 1, "", t.isOnNegativeStrand()));
			current = next;
		}
		if (current != null) {
			if (current.getStop() < t.getStop())
				introns.add(new Intron(current.getStop() + 1, t.getStop(), "", t.isOnNegativeStrand()));
		}
		return introns;
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
