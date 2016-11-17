package genomeAnnotation;

public class CDSPart implements augmentedTree.Interval {

	private int start, stop;

	public CDSPart(int start, int stop) {
		this.start = start;
		this.stop = stop;
	}

	@Override
	public int getStart() {
		return start;
	}

	@Override
	public int getStop() {
		return stop;
	}

}
