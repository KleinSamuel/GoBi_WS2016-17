package gtf;

import java.util.HashSet;

public class Exon extends GenomicRegion {

	private HashSet<Transcript> parentalTranscripts;
	
	public Exon(String id, int start, int stop, String strand){
		super(id, start, stop, strand);
		this.parentalTranscripts = new HashSet<>();
	}

	@Override
	public int compareTo(GenomicRegion o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public HashSet<Transcript> getParentalTranscripts() {
		return parentalTranscripts;
	}
	
	public void addTranscript(Transcript t){
		this.parentalTranscripts.add(t);
	}

}
