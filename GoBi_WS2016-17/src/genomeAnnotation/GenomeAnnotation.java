package genomeAnnotation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class GenomeAnnotation {

	private String name;
	private HashMap<String, Chromosome> chromosomes;

	public GenomeAnnotation(String name) {
		this.name = name;
		chromosomes = new HashMap<>();
	}

	public void addChromosome(Chromosome c) {
		chromosomes.put(c.getID(), c);
	}

	public Chromosome getChromosome(String id) {
		return chromosomes.get(id);
	}

	public Collection<Chromosome> getChromosomes() {
		return chromosomes.values();
	}

	public Gene getGene(String id) {
		Gene g = null;
		for (Chromosome c : chromosomes.values()) {
			g = c.getGene(id);
			if (g != null)
				return g;
		}
		return null;
	}

	public Transcript getTranscript(String id) {
		Transcript tr = null;
		for (Chromosome c : chromosomes.values()) {
			for (Gene g : c.getGenes().values()) {
				tr = g.getTranscript(id);
				if (tr != null)
					return tr;
			}
		}
		return null;
	}

	public Iterator<Gene> iterator(String chromosomeId) {
		Chromosome c = chromosomes.get(chromosomeId);
		if (c != null)
			return c.iterator();
		return null;
	}

}
