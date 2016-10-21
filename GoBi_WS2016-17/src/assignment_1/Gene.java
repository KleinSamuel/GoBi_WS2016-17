package assignment_1;

import java.util.HashMap;
import java.util.Iterator;

import augmentedTree.IntervalTree;

public class Gene implements GenomicRegion {

	private String id;
	private int start;
	private int stop;
	private boolean strand;
	private String chromosomeID;
	private String bioType;
	
	private HashMap<String, Transcript> transcripts;
	private HashMap<String, Exon> exons;
	
	private IntervalTree<Exon> exonTree;
	
	public Gene(int start, int stop, String id, String strand, String chromosomeID, String biotype){
		this.start = start;
		this.stop = stop;
		this.id = id;
		this.chromosomeID = chromosomeID;
		this.bioType = biotype;
		
		if(strand.charAt(0) == '+'){
			this.strand = true;
		}else if(strand.charAt(0) == '-'){
			this.strand = false;
		}
		
		this.transcripts = new HashMap<String, Transcript>();
		this.exons = new HashMap<String, Exon>();
		this.exonTree = new IntervalTree<Exon>();
	}
	
	public String getID(){
		return this.id;
	}
	
	@Override
	public int getStart() {
		return this.start;
	}

	@Override
	public int getStop() {
		return this.stop;
	}
	
	public void setStart(int start){
		this.start = start;
	}
	
	public void setStop(int stop){
		this.stop = stop;
	}
	
	public void setStrand(String strand){
		if(strand.charAt(0) == '+'){
			this.strand = true;
		}else if(strand.charAt(0) == '-'){
			this.strand = false;
		}
	}
	
	public void setChromosomeID(String chromID){
		this.chromosomeID = chromID;
	}

	@Override
	public Iterator<GenomicRegion> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int compareTo(GenomicRegion o) {
		if(this.start > o.getStart()){
			return 1;
		}else if(this.start < o.getStart()){
			return -1;
		}
		return 0;
	}

	@Override
	public GenomicRegionType getType() {
		return GenomicRegionType.GENE;
	}

	@Override
	public String getChromosomeID() {
		return this.chromosomeID;
	}

	@Override
	public boolean isOnForwardStrand() {
		return this.strand;
	}

	public HashMap<String, Transcript> getTranscripts() {
		return transcripts;
	}

	public Gene addTranscript(Transcript t) {
		this.transcripts.put(t.getID(), t);
		return this;
	}

	public HashMap<String, Exon> getExons() {
		return exons;
	}

	public Gene addExon(Exon e) {
		this.exons.put(e.getID(), e);
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
}
