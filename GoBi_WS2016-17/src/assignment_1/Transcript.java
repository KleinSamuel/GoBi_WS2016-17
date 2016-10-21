package assignment_1;

import java.util.HashMap;
import java.util.Iterator;

public class Transcript implements GenomicRegion{

	private String id;
	private int start;
	private int stop;
	private boolean strand;
	private String chromosomeID;
	
	private HashMap<String, Exon> exons;
	private CDS cds;
	
	public Transcript(int start, int stop, String id, String chromosomeID, String strand){
		this.start = start;
		this.stop = stop;
		this.id = id;
		this.chromosomeID = chromosomeID;
		
		if(strand.charAt(0) == '+'){
			this.strand = true;
		}else if(strand.charAt(0) == '-'){
			this.strand = false;
		}
		
		this.exons = new HashMap<String, Exon>();
		this.cds = new CDS();
	}

	public String getID() {
		return id;
	}

	public int getStart() {
		return start;
	}

	public int getStop() {
		return stop;
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

	public HashMap<String, Exon> getExons() {
		return exons;
	}

	public Transcript addExon(Exon exon) {
		this.exons.put(exon.getID(), exon);
		return this;
	}

	@Override
	public Iterator<GenomicRegion> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int compareTo(GenomicRegion o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isOnForwardStrand() {
		return this.strand;
	}

	@Override
	public String getChromosomeID() {
		return this.chromosomeID;
	}

	@Override
	public GenomicRegionType getType() {
		return GenomicRegionType.TRANSCRIPT;
	}

	public CDS getCds() {
		return cds;
	}

	public void addCds(CDS cds) {
		this.cds = cds;
	}
	
}
