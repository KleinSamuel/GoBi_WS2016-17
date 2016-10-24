package genomeAnnotation;

import java.util.HashMap;
import java.util.TreeSet;

public class Transcript extends GenomicRegion {

	private Gene gene;
	private TreeSet<Exon> exons;
	private HashMap<String, CDS> cdss;

	public Transcript(int start, int stop, String id, boolean onNegativeStrand, Gene g) {
		super(start, stop, id, onNegativeStrand);
		gene = g;
		cdss = new HashMap<>();
		exons = new TreeSet<>();
	}

	public void addExon(Exon e) {
		exons.add(e);
		e.add(this);
	}

	public void addCDS(CDS cds) {
		cdss.put(cds.getId(), cds);
	}

}
