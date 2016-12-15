package util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import augmentedTree.IntervalTree;

public class GenRegVecUtil {

	public static LinkedList<Interval> getIntrons(Collection<Interval> exons) {
		LinkedList<Interval> introns = new LinkedList<>();
		if (exons.size() <= 0) {
			return introns;
		}
		Iterator<Interval> exonIt = exons.iterator();
		Interval exon, nextExon;
		exon = exonIt.next();
		while (exonIt.hasNext()) {
			nextExon = exonIt.next();
			introns.add(new Interval(exon.getStop() + 1, nextExon.getStart() - 1));
			exon = nextExon;
		}
		return introns;
	}

	public static IntervalTree<Interval> merge(Collection<Interval> vectors) {
		IntervalTree<Interval> mergedTree = new IntervalTree<>();
		if (vectors.size() == 0) {
			return mergedTree;
		}
		Iterator<Interval> vectorIt = vectors.iterator();
		Interval merged = null, nextVector = null;
		merged = vectorIt.next();
		while (vectorIt.hasNext()) {
			nextVector = vectorIt.next();
			if (nextVector.getStart() > merged.getStop() + 1) {
				mergedTree.add(merged);
				merged = nextVector;
			} else {
				merged.setStop(nextVector.getStop());
			}
		}
		if (merged != null) {
			mergedTree.add(merged);
		}
		return mergedTree;
	}

}