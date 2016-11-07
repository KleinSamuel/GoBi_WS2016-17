package util;

import java.util.Iterator;

import augmentedTree.IntervalTree;
import genomeAnnotation.Exon;
import genomeAnnotation.Gene;

public class UnionTranscript {

	private Gene g;
	private int exonicLength = -1;
	private IntervalTree<Interval> combinedExons;

	public UnionTranscript(Gene g) {
		this.g = g;
	}

	public void calculateCombinedExons() {
		combinedExons = new IntervalTree<>();
		Interval currentCombinedExon = null;
		Exon next = null;
		Iterator<Exon> it = g.getAllExonsSorted().iterator();
		next = it.next();
		if (next == null)
			return;
		currentCombinedExon = new Interval(next.getStart(), next.getStop());
		while ((next = it.next()) != null) {
			if (next.getStart() > currentCombinedExon.getStop()) {
				combinedExons.add(currentCombinedExon);
				currentCombinedExon = new Interval(next.getStart(), next.getStop());
			} else {
				currentCombinedExon.setStart(next.getStop());
			}
		}
		if (currentCombinedExon != null)
			combinedExons.add(currentCombinedExon);
	}

	public IntervalTree<Interval> getCombinedExons() {
		if (combinedExons == null)
			calculateCombinedExons();
		return combinedExons;
	}

	public int getExonicLength() {
		if (exonicLength < 0) {
			calculateExonicLength();
		}
		return exonicLength;
	}

	public void calculateExonicLength() {
		if (combinedExons == null)
			calculateCombinedExons();
		int sum = 0;
		for (Interval i : combinedExons)
			sum += i.getStop() - i.getStart() + 1;
		exonicLength = sum;
	}

}
