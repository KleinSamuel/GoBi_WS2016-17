package task1;

import java.util.LinkedList;

import genomeAnnotation.Gene;
import genomeAnnotation.Transcript;

public class ExonSkippingEvent {

	private Gene g;
	private int intronStart, intronStop;
	private LinkedList<Transcript> wildtypes, splicedVariants;
		
	public ExonSkippingEvent(Gene g, int start, int stop) {
		this.g = g;
		intronStart = start;
		intronStop = stop;
		wildtypes = new LinkedList<>();
		splicedVariants = new LinkedList<>();
	}


}
