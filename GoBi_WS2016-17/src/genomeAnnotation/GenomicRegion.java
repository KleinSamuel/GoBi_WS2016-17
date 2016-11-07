package genomeAnnotation;

public abstract class GenomicRegion implements augmentedTree.Interval, Comparable<GenomicRegion> {

	private int start, stop;
	private boolean onNegativeStrand;
	private String id;

	// 0-based; last inclusive;
	public GenomicRegion(int start, int stop, String id, boolean onNegativeStrand) {
		this.id = id;
		this.start = start;
		this.stop = stop;
		this.onNegativeStrand = onNegativeStrand;
	}

	public String getId() {
		return id;
	}

	public boolean isOnNegativeStrand() {
		return onNegativeStrand;
	}

	public String getStrand() {
		if (isOnNegativeStrand())
			return "-";
		return "+";
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public int getLength() {
		return getStop() - getStart() + 1;
	}

	@Override
	public int getStart() {
		return start;
	}

	@Override
	public int getStop() {
		return stop;
	}

	@Override
	public int compareTo(GenomicRegion o) {
		if (start != o.getStart())
			return Integer.compare(start, o.getStart());
		else
			return Integer.compare(stop, o.getStop());
	}

	@Override
	public boolean equals(Object arg0) {
		return this.id.equals(((GenomicRegion) arg0).getId());
	}

	public void setStart(int start) {
		this.start = start;
	}

	public void setStop(int stop) {
		this.stop = stop;
	}

	public void setOnNegativeStrand(boolean onNegativeStrand) {
		this.onNegativeStrand = onNegativeStrand;
	}

}
