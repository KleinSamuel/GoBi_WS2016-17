package gtf;

import java.util.HashMap;

public class Transcript extends GenomicRegion{
	
	private Gene gene;
	private HashMap<String, Exon> exons;
	private CDS cds;
	
	public Transcript(String id, int start, int stop, String strand, Gene gene){
		super(id, start, stop, strand);
		
		this.gene = gene;
		this.exons = new HashMap<String, Exon>();
		this.cds = new CDS();
	}

	public HashMap<String, Exon> getExons() {
		return exons;
	}

	public Transcript addExon(Exon exon) {
		this.exons.put(exon.getId(), exon);
		return this;
	}

	public CDS getCds() {
		return cds;
	}

	public void addCds(CDS cds) {
		this.cds = cds;
	}
	
	public boolean hasCDS(){
		return cds.getParts().size() == 0;
	}

	public Gene getGene() {
		return gene;
	}

	public void setGene(Gene gene) {
		this.gene = gene;
	}
}
