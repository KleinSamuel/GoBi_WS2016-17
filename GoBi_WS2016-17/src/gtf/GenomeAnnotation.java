package gtf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class GenomeAnnotation implements Iterable<GenomicRegion>{

	private ConcurrentHashMap<String, Chromosome> chromosomes;
	
	public GenomeAnnotation(){
		this.chromosomes = new ConcurrentHashMap<String, Chromosome>();
	}

	public ConcurrentHashMap<String, Chromosome> getChromosomeList() {
		return chromosomes;
	}
	
	public GenomeAnnotation addChromosome(Chromosome chr){
		this.chromosomes.put(chr.getChromosomeID(), chr);
		return this;
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
	
	public int getAmountChromsomes(){
		return this.chromosomes.size();
	}
	
	public int getAmountGenes(){
		int out = 0;
		for(Chromosome c : chromosomes.values()){
			out += c.getGenes().size();
		}
		return out;
	}
	
	public int getAmountExons(){
		int out = 0;
		for(Chromosome c : chromosomes.values()){
			for(Gene g : c.getGenes().values()){
				out += g.getExons().size();
			}
		}
		return out;
	}
	
	public int getAmountTranscripts(){
		int out = 0;
		for(Chromosome c : chromosomes.values()){
			for(Gene g : c.getGenes().values()){
				out += g.getTranscripts().size();
			}
		}
		return out;
	}
	
	public ArrayList<String> getDifferentBiotypes(){
		ArrayList<String> outputList = new ArrayList<>();
		for(Chromosome c : chromosomes.values()){
			for(Gene g : c.getGenes().values()){
				if(!outputList.contains(g.getBioType())){
					outputList.add(g.getBioType());
				}
			}
		}
		return outputList;
	}
	
	public HashMap<String, Integer> getAmountGenesPerBiotype(){
		
		HashMap<String, Integer> outputMap = new HashMap<String, Integer>();
		
		for(Chromosome c : chromosomes.values()){
			for(Gene g : c.getGenes().values()){
				if(outputMap.containsKey(g.getBioType())){
					outputMap.put(g.getBioType(), outputMap.get(g.getBioType())+1);
				}else{
					outputMap.put(g.getBioType(), 1);
				}
			}
		}
		return outputMap;
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
