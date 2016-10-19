package assignment_1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

public class GenomeAnnotation implements Iterable<GenomicRegion>{

	private HashMap<String, Chromosome> chromosomes;
	
	public GenomeAnnotation(){
		this.chromosomes = new HashMap<String, Chromosome>();
	}

	public HashMap<String, Chromosome> getChromosomeList() {
		return chromosomes;
	}
	
	public Gene getGene(String id){
		for(Entry<String, Chromosome> entryChrom : chromosomes.entrySet()){
			for(Entry<String, Gene> entryGene : entryChrom.getValue().getGenes().entrySet()){
				if(entryGene.getKey().equals(id)){
					return entryGene.getValue();
				}
			}
		}
		return null;
	}
	
	public Transcript getTranscript(String id){
		for(Entry<String, Chromosome> entryChrom : chromosomes.entrySet()){
			for(Entry<String, Gene> entryGene : entryChrom.getValue().getGenes().entrySet()){
				for(Entry<String, Transcript> entryTrans : entryGene.getValue().getTranscripts().entrySet()){
					if(entryTrans.getKey().equals(id)){
						return entryTrans.getValue();
					}
				}
			}
		}
		return null;
	}
	
	public CDS getCDS(String id){
		for(Entry<String, Chromosome> entryChrom : chromosomes.entrySet()){
			for(Entry<String, Gene> entryGene : entryChrom.getValue().getGenes().entrySet()){
				for(Entry<String, Transcript> entryTrans : entryGene.getValue().getTranscripts().entrySet()){
					if(entryTrans.getValue().getCds().getId().equals(id)){
						return entryTrans.getValue().getCds();
					}
				}
			}
		}
		return null;
	}

	@Override
	public Iterator<GenomicRegion> iterator() {
		
		final List<Iterator<GenomicRegion>> its = new LinkedList<>();
		
		for (Entry<String, Chromosome> entryChrom : chromosomes.entrySet()) {
			
			its.add(entryChrom.getValue().iterator());
		}
		
		return new Iterator<GenomicRegion>() {
			
			List<Iterator<GenomicRegion>> iterators = its;
			
			@Override
			public boolean hasNext() {
				if (iterators.isEmpty()) {
					return false;
				} else if (iterators.get(0).hasNext()) {
					return true;
				} else {
					iterators.remove(0);
					return hasNext();
				}
			}
			
			@Override
			public GenomicRegion next() {
				if (iterators.isEmpty()) {
					return null;
				} else if (iterators.get(0).hasNext()) {
					return iterators.get(0).next();
				} else {
					iterators.remove(0);
					return next();
				}
			}
			
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}


}
