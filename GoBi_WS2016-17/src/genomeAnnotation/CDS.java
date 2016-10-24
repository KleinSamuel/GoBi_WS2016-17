package genomeAnnotation;

import augmentedTree.IntervalTree;

public class CDS extends GenomicRegion {

	//IntervalTree<CDSPart> cdsParts;

	public CDS(int start, int stop, String id, boolean onNegativeStrand) {

		super(start, stop, id, onNegativeStrand);
	}

}
