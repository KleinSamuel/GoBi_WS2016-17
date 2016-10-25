package gtf;

import java.util.HashMap;
import java.util.Iterator;

import augmentedTree.IntervalTree;

public class Chromosome{

	private String id;
	private HashMap<String, Gene> genes;
	
	IntervalTree<Gene> geneTree;
	
	public Chromosome(String id) {
		this.id = id;
		this.genes = new HashMap<String, Gene>();
		this.geneTree = new IntervalTree<>();
	}

	public Iterator<GenomicRegion> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getChromosomeID() {
		return this.id;
	}

	public HashMap<String, Gene> getGenes() {
		return genes;
	}

	public Chromosome addGene(Gene g) {
		this.genes.put(g.getId(), g);
		this.geneTree.add(g);
		return this;
	}

	public IntervalTree<Gene> getGeneIntervalTree(){
		return this.geneTree;
	}

}
