package genomeAnnotation;

import java.util.HashMap;
import java.util.Iterator;

import augmentedTree.IntervalTree;

public class Gene extends GenomicRegion {

	private String biotype, name;
	private HashMap<String, Transcript> transcripts;
	private HashMap<String, Exon> exons;
	private IntervalTree<Transcript> transcriptsOnPositiveStrand, transcriptsOnNegativeStrand, transcriptsOnBothStrands;
	private IntervalTree<Exon> exonsOnPositiveStrand, exonsOnNegativeStrand, exonsOnBothStrands;

	private IntervalTree<Intron> intronsOnPositiveStrand = null, intronsOnNegativeStrand = null;

	public Gene(int start, int stop, String id, boolean onNegativeStrand, String biotype, String name) {
		super(start, stop, id, onNegativeStrand);
		transcripts = new HashMap<>();
		transcriptsOnNegativeStrand = new IntervalTree<>();
		transcriptsOnPositiveStrand = new IntervalTree<>();
		transcriptsOnBothStrands = new IntervalTree<>();
		exons = new HashMap<>();
		exonsOnNegativeStrand = new IntervalTree<>();
		exonsOnPositiveStrand = new IntervalTree<>();
		exonsOnBothStrands = new IntervalTree<>();
		this.biotype = biotype;
		this.name = name;
	}

	public void addTranscript(Transcript tr) {
		transcripts.put(tr.getId(), tr);
		if (tr.isOnNegativeStrand())
			transcriptsOnNegativeStrand.add(tr);
		else
			transcriptsOnPositiveStrand.add(tr);
		transcriptsOnBothStrands.add(tr);
	}

	public void addExon(Exon e) {
		if (exons.containsKey(e.getId()))
			return;

		exons.put(e.getId(), e);
		if (e.isOnNegativeStrand())
			exonsOnNegativeStrand.add(e);
		else
			exonsOnPositiveStrand.add(e);
		exonsOnBothStrands.add(e);
	}

	public Transcript getTranscript(String id) {
		return transcripts.get(id);
	}

	public IntervalTree<Intron> getIntrons(boolean onNegativeStrand) {
		if (onNegativeStrand) {
			if (intronsOnNegativeStrand == null)
				calcIntrons(exonsOnNegativeStrand, intronsOnNegativeStrand, onNegativeStrand);
			return intronsOnNegativeStrand;
		} else {
			if (intronsOnPositiveStrand == null)
				calcIntrons(exonsOnPositiveStrand, intronsOnPositiveStrand, onNegativeStrand);
			return intronsOnPositiveStrand;
		}
	}

	public void calcIntrons(IntervalTree<Exon> exons, IntervalTree<Intron> introns, boolean onNegativeStrand) {
		introns = new IntervalTree<Intron>();
		Iterator<Exon> exonIt = exons.iterator();
		Exon current = null, next = null;
		if (exonIt.hasNext()) {
			current = exonIt.next();
			if (current.getStart() > getStart())
				introns.add(new Intron(getStart(), current.getStart() - 1, "", onNegativeStrand));
		}
		while (exonIt.hasNext()) {
			next = exonIt.next();
			introns.add(new Intron(current.getStart() + 1, next.getStop() - 1, "", onNegativeStrand));
			current = next;
		}
		if (current != null) {
			if (current.getStop() < getStop())
				introns.add(new Intron(current.getStop() + 1, getStop(), "", onNegativeStrand));
		}

	}

	public String getBiotype() {
		return biotype;
	}

}
