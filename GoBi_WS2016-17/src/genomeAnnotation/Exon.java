package genomeAnnotation;

import java.util.HashSet;

public class Exon extends GenomicRegion {

	private HashSet<Transcript> parentalTranscripts;

	public Exon(int start, int stop, String id, boolean onNegativeStrand) {

		super(start, stop, id, onNegativeStrand);
		parentalTranscripts = new HashSet<>();
	}

	public void add(Transcript parent) {
		parentalTranscripts.add(parent);
	}
	
	public HashSet<Transcript> getParentalTranscripts(){
		return parentalTranscripts;
	}

}
