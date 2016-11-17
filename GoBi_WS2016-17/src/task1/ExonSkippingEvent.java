package task1;

import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeSet;

import genomeAnnotation.Exon;
import genomeAnnotation.Gene;
import genomeAnnotation.Intron;
import genomeAnnotation.Transcript;

public class ExonSkippingEvent {

	private Gene g;
	private int intronStart, intronStop;
	private LinkedList<Transcript> wildtypes, splicedVariants;
	private int maxSkippedExons = -1, minSkippedExons = Integer.MAX_VALUE, minSkippedBases = Integer.MAX_VALUE,
			maxSkippedBases = Integer.MIN_VALUE;

	public ExonSkippingEvent(Gene g, int start, int stop, Collection<Transcript> wildtypes,
			Collection<Transcript> splicedVariants) {
		this.g = g;
		intronStart = start;
		intronStop = stop;
		this.wildtypes = new LinkedList<>(wildtypes);
		this.splicedVariants = new LinkedList<>(splicedVariants);
	}

	public Gene getGene() {
		return g;
	}

	public int getIntronStart() {
		return intronStart;
	}

	public int getIntronStop() {
		return intronStop;
	}

	public LinkedList<Transcript> getWildtypes() {
		return wildtypes;
	}

	public LinkedList<Transcript> getSplicedVariants() {
		return splicedVariants;
	}

	public String getSVprots() {
		StringBuilder sb = new StringBuilder();
		for (Transcript splicedVar : splicedVariants)
			sb.append(splicedVar.getCds().getProteinId() + "|");
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public String getWTprots() {
		StringBuilder sb = new StringBuilder();
		for (Transcript wildtype : wildtypes)
			sb.append(wildtype.getCds().getProteinId() + "|");
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public String getWTintronsInSVintrons() {
		TreeSet<Intron> intronsInWTs = new TreeSet<>();
		for (Transcript t : wildtypes) {
			intronsInWTs
					.addAll(t.getIntrons().getIntervalsSpannedBy(intronStart, intronStop, new LinkedList<Intron>()));
		}
		StringBuilder sb = new StringBuilder();
		for (Intron i : intronsInWTs) {
			sb.append(i.getStart() + ":" + i.getStop() + "|");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public int getMaxSkippedExons() {
		return maxSkippedExons;
	}

	public int getMaxSkippedBases() {
		return maxSkippedBases;
	}

	public String getMinMaxSkippingInfo() {
		if (maxSkippedBases < 0 || minSkippedBases < 0 || maxSkippedExons < 0 || minSkippedExons < 0)
			calculateMinMaxSkippingInfo();
		return minSkippedExons + "\t" + maxSkippedExons + "\t" + minSkippedBases + "\t" + maxSkippedBases;
	}

	private void calculateMinMaxSkippingInfo() {
		LinkedList<Exon> skippedExons;
		for (Transcript wildtype : wildtypes) {
			int skippedBases = 0;
			skippedExons = wildtype.getExons().getIntervalsSpannedBy(intronStart, intronStop, new LinkedList<>());
			maxSkippedExons = Math.max(maxSkippedExons, skippedExons.size());
			minSkippedExons = Math.min(minSkippedExons, skippedExons.size());
			for (Exon e : skippedExons)
				skippedBases += e.getLength();
			maxSkippedBases = Math.max(maxSkippedBases, skippedBases);
			minSkippedBases = Math.min(minSkippedBases, skippedBases);
		}

	}

}
