package genomeAnnotation;

import java.util.HashMap;

import augmentedTree.IntervalTree;
import util.GenRegVecUtil;
import util.Interval;
import util.UnionTranscript;

public class Gene extends GenomicRegion {

	private Chromosome chromosome;
	private String biotype, name;
	private HashMap<String, Transcript> transcripts;
	private HashMap<String, Exon> exons;
	private IntervalTree<Transcript> transcriptsOnPositiveStrand, transcriptsOnNegativeStrand, transcriptsOnBothStrands;
	private IntervalTree<Exon> exonsOnPositiveStrand, exonsOnNegativeStrand, exonsOnBothStrands;
	private IntervalTree<Interval> union = null;

	private IntervalTree<Interval> intronsOnPositiveStrand = null, intronsOnNegativeStrand = null;

	public Gene(int start, int stop, String id, boolean onNegativeStrand, String biotype, String name,
			Chromosome parent) {
		super(start, stop, id, onNegativeStrand);
		transcripts = new HashMap<>();
		transcriptsOnNegativeStrand = new IntervalTree<>();
		transcriptsOnPositiveStrand = new IntervalTree<>();
		transcriptsOnBothStrands = new IntervalTree<>();
		exons = new HashMap<>();
		exonsOnNegativeStrand = new IntervalTree<>();
		exonsOnPositiveStrand = new IntervalTree<>();
		exonsOnBothStrands = new IntervalTree<>();
		this.biotype = biotype;
		this.name = name;
		chromosome = parent;
	}

	public void addTranscript(Transcript tr) {
		transcripts.put(tr.getId(), tr);
		if (tr.isOnNegativeStrand())
			transcriptsOnNegativeStrand.add(tr);
		else
			transcriptsOnPositiveStrand.add(tr);
		transcriptsOnBothStrands.add(tr);
	}

	public void addExon(Exon e) {
		if (exons.containsKey(e.getId()))
			return;

		exons.put(e.getId(), e);
		if (e.isOnNegativeStrand())
			exonsOnNegativeStrand.add(e);
		else
			exonsOnPositiveStrand.add(e);
		exonsOnBothStrands.add(e);
	}

	public Transcript getTranscript(String id) {
		return transcripts.get(id);
	}

	public Exon getExon(String id) {
		return exons.get(id);
	}

	public IntervalTree<Transcript> getAllTranscriptsSorted() {
		return transcriptsOnBothStrands;
	}

	public Chromosome getChromosome() {
		return chromosome;
	}

	public IntervalTree<Interval> getIntrons(boolean onNegativeStrand) {
		if (onNegativeStrand) {
			if (intronsOnNegativeStrand == null)
				intronsOnNegativeStrand = GenRegVecUtil.getExonsAsTree(exonsOnNegativeStrand);
			return intronsOnNegativeStrand;
		} else {
			if (intronsOnPositiveStrand == null)
				intronsOnPositiveStrand = GenRegVecUtil.getExonsAsTree(exonsOnPositiveStrand);
			return intronsOnPositiveStrand;
		}
	}

	public IntervalTree<Exon> getAllExonsSorted() {
		return exonsOnBothStrands;
	}

	public String getBiotype() {
		return biotype;
	}

	private Transcript longestTr = null;

	public Transcript getLongestTr() {
		if (longestTr == null)
			getLongestTranscriptLength();
		return longestTr;
	}

	public int getLongestTranscriptLength() {
		Transcript longestTranscript = null;
		int longestLength = -1, currentLength = -1;
		for (Transcript tr : transcriptsOnBothStrands) {
			if (longestTranscript == null) {
				longestTranscript = tr;
				longestLength = longestTranscript.calculateExonicLength();
			} else {
				currentLength = tr.calculateExonicLength();
				if (currentLength > longestLength) {
					longestLength = currentLength;
					longestTranscript = tr;
				}
			}
		}
		longestTr = longestTranscript;
		return longestLength;
	}

	public IntervalTree<Interval> getUnionTranscript() {
		if (union == null) {
			union = new UnionTranscript(this).getCombinedExons();
		}
		return union;
	}

	public String getName() {
		return name;
	}

	public String[] getInfoLine() {
		int numProts = 0;
		for (Transcript t : transcriptsOnBothStrands)
			if (t.hasCDS())
				numProts++;
		return new String[] { "http://www.ensembl.org/Homo_sapiens/Gene/Summary?db=core;g=<" + getId() + ">",
				name + " " + getId() + "(" + chromosome.getID() + getStrand() + " " + biotype + " " + getStart() + "-"
						+ getStop() + ") num transcripts: " + transcripts.size() + " num proteins: " + numProts };
	}

}
