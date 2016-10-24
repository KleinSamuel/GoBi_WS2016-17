package gtf;

import java.util.ArrayList;
import java.util.Iterator;

public class CDS implements GenomicRegion{

	private String id;
	private ArrayList<CDSPart> parts;
	private String chromosomeID;
	private int start = -1;
	private int stop = -1;
	
	public CDS(){
		parts = new ArrayList<CDSPart>();
	}
	
	@Override
	public String getChromosomeID() {
		return this.chromosomeID;
	}

	@Override
	public GenomicRegionType getType() {
		return GenomicRegionType.CDS;
	}

	@Override
	public int getStart() {
		return this.start;
	}

	@Override
	public int getStop() {
		return this.stop;
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
	public boolean isOnNegativeStrand(){
		return false;
	}

	public ArrayList<CDSPart> getParts() {
		return parts;
	}

	public void addPart(CDSPart part) {
		this.parts.add(part);
		
		if(this.start == -1){
			this.start = part.getStart();
		}else{
			this.start = Math.min(this.start, part.getStart());
		}
		
		if(this.stop == -1){
			this.stop = part.getStop();
		}else{
			this.stop = Math.max(this.stop, part.getStop());
		}
		
		this.id = part.getID();
		this.chromosomeID = part.getChromosomeID();
	}

	public String getId() {
		return id;
	}
}
