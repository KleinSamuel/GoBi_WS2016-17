package task1;

import java.util.HashMap;
import java.util.Iterator;

import genomeAnnotation.Chromosome;
import genomeAnnotation.Gene;
import genomeAnnotation.GenomeAnnotation;

public class BiotypeAnalysis {

	public static HashMap<String, Integer> genesPerBiotype(GenomeAnnotation ga) {
		HashMap<String, Integer> genesPerBiotype = new HashMap<>();
		String biotype = null;
		Integer biotypeCount = null;

		for (Chromosome c : ga.getChromosomes().values()) {
			for (Iterator<Gene> it = c.iterator(); it.hasNext();) {
				biotype = it.next().getBiotype();
				biotypeCount = genesPerBiotype.get(biotype);
				if (biotypeCount != null)
					genesPerBiotype.put(biotype, biotypeCount + 1);
				else
					genesPerBiotype.put(biotype, 1);
			}
		}

		return genesPerBiotype;

	}

}
