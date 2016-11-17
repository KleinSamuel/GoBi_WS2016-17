package task1;

import java.util.Iterator;

import genomeAnnotation.Chromosome;
import genomeAnnotation.Gene;
import genomeAnnotation.GenomeAnnotation;
import gnu.trove.map.hash.THashMap;

public class BiotypeAnalysis {

	public static THashMap<String, Integer> genesPerBiotype(GenomeAnnotation ga) {
		THashMap<String, Integer> genesPerBiotype = new THashMap<>();
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
