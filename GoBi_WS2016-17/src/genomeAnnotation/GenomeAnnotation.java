package genomeAnnotation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class GenomeAnnotation {

	private String name;
	private LinkedList<Chromosome> chromosomesInFileOrder;
	private HashMap<String, Chromosome> chromosomes;
	private HashMap<String, Transcript> transcripts;

	public GenomeAnnotation(String name) {
		this.name = name;
		chromosomes = new HashMap<>();
		chromosomesInFileOrder = new LinkedList<>();
		transcripts = new HashMap<>();
	}

	public void addChromosome(Chromosome c) {
		chromosomes.put(c.getID(), c);
		chromosomesInFileOrder.add(c);
	}

	public Chromosome getChromosome(String id) {
		return chromosomes.get(id);
	}

	public HashMap<String, Chromosome> getChromosomes() {
		return chromosomes;
	}

	public LinkedList<Chromosome> getChromosomesInFileOrder() {
		return chromosomesInFileOrder;
	}

	public String getName() {
		return name;
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
		// Transcript tr = null;
		// for (Chromosome c : chromosomes.values()) {
		// for (Gene g : c.getGenes().values()) {
		// tr = g.getTranscript(id);
		// if (tr != null)
		// return tr;
		// }
		// }
		// return null;
		return transcripts.get(id);
	}

	public void addTranscript(Transcript t) {
		transcripts.put(t.getId(), t);
	}

	public CDS getCDS(String ccds_id) {
		for (Chromosome c : chromosomes.values()) {
			for (Gene g : c.getGenes().values()) {
				for (Transcript t : g.getAllTranscriptsSorted())
					if ((t.getCCDS_id() != null) && t.getCCDS_id().equals(ccds_id))
						return t.getCds();
			}
		}
		return null;
	}

	public Iterator<Gene> iterator() {
		LinkedList<Iterator<Gene>> it = new LinkedList<>();
		for (Chromosome c : chromosomesInFileOrder) {
			it.add(c.getAllGenesSorted().iterator());
		}

		return new Iterator<Gene>() {

			LinkedList<Iterator<Gene>> iterators = it;

			@Override
			public boolean hasNext() {
				if (iterators.isEmpty()) {
					return false;
				}
				if (iterators.getFirst().hasNext()) {
					return true;
				}
				iterators.removeFirst();
				return hasNext();
			}

			@Override
			public Gene next() {
				if (iterators.isEmpty()) {
					return null;
				}
				if (iterators.getFirst().hasNext()) {
					return iterators.getFirst().next();
				}
				iterators.removeFirst();
				return next();
			}
		};
	}

	public Iterator<Gene> iterator(String chromosomeId, Integer start, Integer stop) {
		if (chromosomeId == null)
			return iterator();
		Chromosome c = chromosomes.get(chromosomeId);
		if (c == null) {
			System.out.println("chromosome with id " + chromosomeId + " does not exist!(GA: Iterator<Gene>)");
			System.exit(1);
		}
		if (start == null) {
			if (stop == null)
				return c.getAllGenesSorted().iterator();
			LinkedList<Gene> genesStartingBelowStop = new LinkedList<>();
			for (Gene g : c.getAllGenesSorted()) {
				if (g.getStart() < stop)
					genesStartingBelowStop.add(g);
				else
					break;
			}
			return genesStartingBelowStop.iterator();
		}
		LinkedList<Gene> genesEndingAfterStart = new LinkedList<>();
		for (Gene g : c.getAllGenesSorted()) {
			if (g.getStop() > start)
				genesEndingAfterStart.add(g);
		}
		return genesEndingAfterStart.iterator();
	}

}