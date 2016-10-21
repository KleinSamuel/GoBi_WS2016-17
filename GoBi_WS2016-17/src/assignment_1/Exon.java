package assignment_1;

import java.util.HashMap;
import java.util.Iterator;

public class Exon implements GenomicRegion {

	private String id;
	private int start;
	private int stop;
	private boolean strand;
	private String chromosomeID;
	
	public Exon(int start, int stop, String id, String strand, String chromosomeID){
		this.start = start;
		this.stop = stop;
		this.id = id;
		this.chromosomeID = chromosomeID;
		
		if(strand.charAt(0) == '+'){
			this.strand = true;
		}else if(strand.charAt(0) == '-'){
			this.strand = false;
		}
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
	
	public void setChromosomeID(String id){
		this.chromosomeID = id;
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
		return GenomicRegionType.EXON;
	}
	
}
