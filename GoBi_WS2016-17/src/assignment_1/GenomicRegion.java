package assignment_1;

import augmentedTree.Interval;

public interface GenomicRegion extends Interval, Iterable<GenomicRegion>, Comparable<GenomicRegion>, StrandInformation, ChromosomeInformation{
	
	GenomicRegionType getType();
	
	default boolean contains(GenomicRegion region){
		return (this.getStart() <= region.getStart() && this.getStop() >= region.getStop());
	}
	
	default boolean isContainedIn(GenomicRegion region){
		return (this.getStart() >= region.getStart() && this.getStop() <= region.getStop());
	}
	
	default boolean intersectsOnStart(GenomicRegion region){
		return (this.getStart() <= region.getStart() && this.getStop() <= region.getStop());
	}
	
	default boolean intersectsOnStop(GenomicRegion region){
		return (this.getStart() >= region.getStart() && this.getStop() >= region.getStop());
	}
	
}
