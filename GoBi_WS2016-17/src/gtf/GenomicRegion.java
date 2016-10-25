package gtf;

public abstract class GenomicRegion implements augmentedTree.Interval, Comparable<GenomicRegion>{
	
	private String id;
	private int start, stop;
	private boolean isOnNegativeStrand;
	
	/* 0 based, last inclusive */
	public GenomicRegion(String id, int start, int stop, String strand) {
		this.id = id;
		this.setStart(start);
		this.setStop(stop);
		this.setOnNegativeStrand(strand);
	}
	
	public boolean contains(GenomicRegion region){
		return (this.getStart() <= region.getStart() && this.getStop() >= region.getStop());
	}
	
	public boolean isContainedIn(GenomicRegion region){
		return (this.getStart() >= region.getStart() && this.getStop() <= region.getStop());
	}
	
	public boolean intersectsOnStart(GenomicRegion region){
		return (this.getStart() <= region.getStart() && this.getStop() <= region.getStop());
	}
	
	public boolean intersectsOnStop(GenomicRegion region){
		return (this.getStart() >= region.getStart() && this.getStop() >= region.getStop());
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id){
		this.id = id;
	}

	public int getStop() {
		return stop;
	}

	public void setStop(int stop) {
		this.stop = stop;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public boolean isOnNegativeStrand() {
		return isOnNegativeStrand;
	}

	public void setOnNegativeStrand(String strand) {
		this.isOnNegativeStrand = strand.equals("-");
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	public int getLength(){
		return (stop - start) + 1;
	}
	
	@Override
	public int compareTo(GenomicRegion o) {
		if(start != o.getStart()){
			return Integer.compare(start, o.getStart());
		}else{
			return Integer.compare(stop, o.getStop());
		}
	}
	
}
