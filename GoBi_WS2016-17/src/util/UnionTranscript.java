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
		if (it.hasNext())
			next = it.next();
		else
			return;
		currentCombinedExon = new Interval(next.getStart(), next.getStop());
		while (it.hasNext()) {
			next = it.next();
			if (next.getStart() > currentCombinedExon.getStop() + 1) {
				combinedExons.add(currentCombinedExon);
				currentCombinedExon = new Interval(next.getStart(), next.getStop());
			} else {
				if (next.getStop() > currentCombinedExon.getStop())
					currentCombinedExon.setStop(next.getStop());
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
