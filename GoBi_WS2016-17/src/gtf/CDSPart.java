package gtf;

public class CDSPart extends GenomicRegion{

	private StartCodon startCodon;
	private StopCodon stopCodon;
	
	public CDSPart(String id, int start, int stop, String strand){
		super(id, start, stop, strand);
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

}
