package genomeAnnotation;

import java.util.TreeSet;

public class Transcript extends GenomicRegion {

	private Gene gene;
	private TreeSet<Exon> exons;
	private CDS cds;

	public Transcript(int start, int stop, String id, boolean onNegativeStrand, Gene g) {
		super(start, stop, id, onNegativeStrand);
		gene = g;
		exons = new TreeSet<>();
		cds = null;
	}

	public String getCCDS_id() {
		if (cds != null)
			return cds.getId();
		return null;
	}

	public CDS getCds() {
		return cds;
	}

	public boolean hasCDS() {
		return cds != null;
	}

	public void addExon(Exon e) {
		exons.add(e);
		e.add(this);
	}

	public TreeSet<Exon> getExons() {
		return exons;
	}

	public Gene getParentalGene() {
		return gene;
	}

	public void createCDS(CDS cds) {
		this.cds = cds;
	}

	public void addCDSPart(CDSPart cdsPart) {
		cds.addCDSPart(cdsPart);
	}

	public int calculateExonicLength() {
		int sum = 0;
		for (Exon e : exons)
			sum += e.getLength();
		return sum;
	}

}
