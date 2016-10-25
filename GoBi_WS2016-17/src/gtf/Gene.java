package gtf;

import java.util.HashMap;

import augmentedTree.IntervalTree;

public class Gene extends GenomicRegion {

	private String bioType;
	private HashMap<String, Transcript> transcripts;
	private HashMap<String, Exon> exons;
	private IntervalTree<Exon> exonTree;
	private Chromosome chromosome;
	
	public Gene(String id, int start, int stop, String strand, String biotype, Chromosome chromosome){
		super(id, start, stop, strand);
		this.bioType = biotype;
		this.transcripts = new HashMap<String, Transcript>();
		this.exons = new HashMap<String, Exon>();
		this.exonTree = new IntervalTree<>();
		this.chromosome = chromosome;
	}

	public HashMap<String, Transcript> getTranscripts() {
		return transcripts;
	}

	public Gene addTranscript(Transcript t) {
		this.transcripts.put(t.getId(), t);
		return this;
	}

	public HashMap<String, Exon> getExons() {
		return exons;
	}

	public Gene addExon(Exon e) {
		this.exons.put(e.getId(), e);
		this.exonTree.add(e);
		return this;
	}

	public IntervalTree<Exon> getExonIntervalTree(){
		return this.exonTree;
	}

	public String getBioType() {
		return bioType;
	}

	public void setBioType(String bioType) {
		this.bioType = bioType;
	}

	public Chromosome getChromosome() {
		return chromosome;
	}

	public void setChromosome(Chromosome chromosome) {
		this.chromosome = chromosome;
	}
}
