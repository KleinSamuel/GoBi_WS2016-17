package gtf;

import java.util.Iterator;

public class CDSPart implements GenomicRegion{

	private String id;
	private int start;
	private int stop;
	private StartCodon startCodon;
	private StopCodon stopCodon;
	private boolean strand;
	private String chromosomeID;
	
	public CDSPart(int start, int stop, String proteinID, String chromosomeID){
		this.start = start;
		this.stop = stop;
		this.id = proteinID;
		this.chromosomeID = chromosomeID;
	}
	
	@Override
	public int getStart() {
		return this.start;
	}
	
	@Override
	public int getStop() {
		return this.stop;
	}

	public String getID() {
		return this.id;
	}

	public StartCodon getStartCodon() {
		return startCodon;
	}

	public void setStartCodon(StartCodon startCodon) {
		this.startCodon = startCodon;
	}

	public StopCodon getStopCodon() {
		return stopCodon;
	}

	public void setStopCodon(StopCodon stopCodon) {
		this.stopCodon = stopCodon;
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
	public boolean isOnNegativeStrand() {
		return this.strand;
	}

	@Override
	public String getChromosomeID() {
		return this.chromosomeID;
	}

	@Override
	public GenomicRegionType getType() {
		return GenomicRegionType.CDS_PART;
	}

}
