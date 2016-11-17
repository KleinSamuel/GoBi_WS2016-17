package genomeAnnotation;

import augmentedTree.IntervalTree;

public class CDS extends GenomicRegion {
	// store ccds_id in genomicRegion; protein_id in cds
	private String proteinId;
	private IntervalTree<CDSPart> cdsParts;

	public CDS(int start, int stop, String id, String proteinId, boolean onNegativeStrand) {

		super(start, stop, id, onNegativeStrand);
		this.proteinId = proteinId;
		cdsParts = new IntervalTree<>();
	}

	public String getProteinId() {
		return proteinId;
	}

	public void addCDSPart(CDSPart cdsp) {
		cdsParts.add(cdsp);
		setStart(cdsParts.first().getStart());
		setStop(cdsParts.last().getStop());
	}

	public IntervalTree<CDSPart> getCDSParts() {
		return cdsParts;
	}

}
