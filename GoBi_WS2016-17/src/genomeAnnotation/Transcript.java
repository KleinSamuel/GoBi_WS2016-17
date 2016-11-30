package genomeAnnotation;

import java.util.Iterator;
import java.util.Vector;

import augmentedTree.IntervalTree;
import javafx.util.Pair;

public class Transcript extends GenomicRegion {

	private Gene gene;
	private IntervalTree<Exon> exons;
	private IntervalTree<Intron> introns;
	private CDS cds;

	public Transcript(int start, int stop, String id, boolean onNegativeStrand, Gene g) {
		super(start, stop, id, onNegativeStrand);
		gene = g;
		exons = new IntervalTree<>();
		cds = null;
	}

	public String getCCDS_id() {
		if (cds != null)
			return cds.getId();
		return null;
	}

	public CDS getCds() {
		return cds;
	}

	public boolean hasCDS() {
		return cds != null;
	}

	public void addExon(Exon e) {
		exons.add(e);
		e.add(this);
	}

	public IntervalTree<Exon> getExons() {
		return exons;
	}

	public Gene getParentalGene() {
		return gene;
	}

	public void createCDS(CDS cds) {
		this.cds = cds;
	}

	public void addCDSPart(CDSPart cdsPart) {
		cds.addCDSPart(cdsPart);
	}

	public IntervalTree<Intron> getIntrons() {
		if (introns == null)
			calcIntrons();
		return introns;
	}

	public void calcIntrons() {
		introns = new IntervalTree<>();
		Iterator<Exon> exonIt = getExons().iterator();
		Exon current = null, next = null;
		if (exonIt.hasNext()) {
			current = exonIt.next();
			if (current.getStart() > this.getStart())
				introns.add(new Intron(this.getStart(), current.getStart() - 1, "", this.isOnNegativeStrand()));
		}
		while (exonIt.hasNext()) {
			next = exonIt.next();
			// check that they are not directly one after the other
			if (current.getStop() < next.getStart() - 1)
				introns.add(new Intron(current.getStop() + 1, next.getStart() - 1, "", this.isOnNegativeStrand()));
			current = next;
		}
		if (current != null) {
			if (current.getStop() < this.getStop())
				introns.add(new Intron(current.getStop() + 1, this.getStop(), "", this.isOnNegativeStrand()));
		}
	}

	public int calculateExonicLength() {
		int sum = 0;
		for (Exon e : exons)
			sum += e.getLength();
		return sum;
	}

	public Vector<Pair<Integer, Integer>> getGenomicRegionVector(int startInTranscript, int stopInTranscript) {
		Vector<Pair<Integer, Integer>> genomicRegionVector = new Vector<>();
		int startToSearch = startInTranscript, stopToSearch = stopInTranscript;
		boolean startFound = false;
		for (Exon e : exons) {
			if (e.getLength() <= startToSearch) {
				startToSearch -= e.getLength();
				stopToSearch -= e.getLength();
				continue;
			}
			if (!startFound) {
				if (e.getLength() <= stopToSearch) {
					genomicRegionVector.add(new Pair<Integer, Integer>(e.getStart() + startToSearch, e.getStop()));
					stopToSearch -= e.getLength();
				} else {
					genomicRegionVector
							.add(new Pair<Integer, Integer>(e.getStart() + startToSearch, e.getStart() + stopToSearch));
					return genomicRegionVector;
				}
				startFound = true;
				continue;
			}
			if (e.getLength() <= stopToSearch) {
				genomicRegionVector.add(new Pair<Integer, Integer>(e.getStart(), e.getStop()));
				stopToSearch -= e.getLength();
			} else {
				genomicRegionVector.add(new Pair<Integer, Integer>(e.getStart(), e.getStart() + stopToSearch));
				break;
			}
		}
		return genomicRegionVector;
	}

}
