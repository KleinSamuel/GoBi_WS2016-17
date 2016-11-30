package gtf;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
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
			if(entryChrom.getValue().getGenes().containsKey(id)){
				return entryChrom.getValue().getGenes().get(id);
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
					return entryTrans.getValue().getCds();
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
	
	public TreeMap<String, Integer> getAmountGenesPerBiotype(){
		
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		ValueComparator vlc = new ValueComparator(map);
		TreeMap<String, Integer> outputMap = new TreeMap<>(vlc);
		
		for(Chromosome c : chromosomes.values()){
			for(Gene g : c.getGenes().values()){
				if(map.containsKey(g.getBioType())){
					map.put(g.getBioType(), map.get(g.getBioType())+1);
				}else{
					map.put(g.getBioType(), 1);
				}
			}
		}
		outputMap.putAll(map);
		return outputMap;
	}
	
	public HashMap<String, HashSet<Gene>> getGenesForEachBiotype(){
		
		HashMap<String, HashSet<Gene>> out = new HashMap<>();
		
		for(Chromosome chr : chromosomes.values()){
			
			for(Gene g : chr.getGenes().values()){
				
				if(out.containsKey(g.getBioType())){
					out.get(g.getBioType()).add(g);
				}else{
					HashSet<Gene> s = new HashSet<>();
					s.add(g);
					out.put(g.getBioType(), s);
				}
			}
		}
		
		return out;
	}
	
	class ValueComparator implements Comparator<String> {

		Map<String, Integer> base;
		
		public ValueComparator(Map<String, Integer> base) {
			this.base = base;
		}
		
		@Override
		public int compare(String o1, String o2) {
			if(base.get(o1) >= base.get(o2)){
				return -1;
			}else{
				return 1;
			}
		}
		
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
