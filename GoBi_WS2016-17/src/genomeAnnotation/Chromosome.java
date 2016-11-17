package genomeAnnotation;

import java.util.Iterator;

import augmentedTree.IntervalTree;
import gnu.trove.map.hash.THashMap;

public class Chromosome {

	private String id;
	private THashMap<String, Gene> genes;
	private IntervalTree<Gene> genesOnPositiveStrand, genesOnNegativeStrand, genesOnBothStrands;

	public Chromosome(String id) {
		this.id = id;
		genes = new THashMap<>();
		genesOnPositiveStrand = new IntervalTree<>();
		genesOnNegativeStrand = new IntervalTree<>();
		genesOnBothStrands = new IntervalTree<>();
	}

	public void addGene(Gene g) {
		genes.put(g.getId(), g);
		if (g.isOnNegativeStrand())
			genesOnNegativeStrand.add(g);
		else
			genesOnPositiveStrand.add(g);
		genesOnBothStrands.add(g);
	}

	public Gene getGene(String id) {
		return genes.get(id);
	}

	public THashMap<String, Gene> getGenes() {
		return genes;
	}

	public String getID() {
		return id;
	}

	public Iterator<Gene> iterator() {
		return genesOnBothStrands.iterator();
	}

	public IntervalTree<Gene> getAllGenesSorted() {
		return genesOnBothStrands;
	}

}
