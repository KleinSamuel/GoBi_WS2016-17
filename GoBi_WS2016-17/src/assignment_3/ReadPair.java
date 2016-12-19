package assignment_3;

import java.util.HashSet;
import java.util.LinkedList;

import augmentedTree.IntervalTree;
import genomeAnnotation.Exon;
import genomeAnnotation.Gene;
import genomeAnnotation.Transcript;
import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;
import util.GenRegVecUtil;
import util.Interval;

public class ReadPair {

	private SAMRecord forward, reverse;
	private boolean isStarMapping;
	private LinkedList<Interval> blocksForward, blocksReverse;
	private LinkedList<Interval> intronsForward, intronsReverse;
	private LinkedList<Gene> matchedGenes = null; // stores transcript matches
													// or merged matches or
													// intronic matches
	private LinkedList<Transcript> matchedTranscripts;
	private int mismatchCount = 0, clippingSize = 0, splitCount = -2, geneDistance = Integer.MIN_VALUE, pcrIndex = 0;
	private boolean intergenic = false, antisense = false, intronic = false, merged = false;
	private Interval genomicRegion;
	private String XX;

	private boolean debugCheck = false;

	public ReadPair(SAMRecord forward, SAMRecord reverse, boolean isStarMapping) {
		this.forward = forward;
		this.reverse = reverse;
		XX = (String) forward.getAttribute("XX");
		if (XX == null)
			XX = (String) reverse.getAttribute("XX");
		if (getReadName().equals("30808979"))
			debugCheck = true;
		this.isStarMapping = isStarMapping;
		// blocksForward = GenRegVecUtil.parseAlignmentBlocks(forward);
		blocksForward = new LinkedList<>();
		for (AlignmentBlock ab : forward.getAlignmentBlocks()) {
			blocksForward.add(new Interval(ab.getReferenceStart() - 1, ab.getReferenceStart() + ab.getLength() - 2));
		}
		blocksForward = GenRegVecUtil.merge(blocksForward);
		intronsForward = GenRegVecUtil.getIntrons(blocksForward);
		blocksReverse = new LinkedList<>();
		for (AlignmentBlock ab : reverse.getAlignmentBlocks()) {
			blocksReverse.add(new Interval(ab.getReferenceStart() - 1, ab.getReferenceStart() + ab.getLength() - 2));
		}
		blocksReverse = GenRegVecUtil.merge(blocksReverse);
		// blocksReverse = GenRegVecUtil.parseAlignmentBlocks(reverse);
		intronsReverse = GenRegVecUtil.getIntrons(blocksReverse);
		genomicRegion = new Interval(Math.min(blocksForward.getFirst().getStart(), blocksReverse.getFirst().getStart()),
				Math.max(blocksForward.getLast().getStop(), blocksReverse.getLast().getStop()));
		calcSplitCount();
		if (splitCount == -1) {
			if (!XX.contains("inconsistent")) {
				System.out.println("Error");
				System.exit(1);
			}
			return;
		}
		if (splitCount > -1) {
			if (splitCount != (Integer.parseInt(XX.split("\t")[3].split(":")[1]))) {
				System.out.println(splitCount + "!=" + Integer.parseInt(XX.split("\t")[3].split(":")[1]));
				debugError();
			}
			calcMissmatches();
			calcClipping();
			getMatchedGenes();
		}
		if (XX.contains("MERGED") && !merged) {
			System.out.println("should be merged " + getReadName());
			System.out.println("intronic: " + intronic);
			System.out.println(getAttributeXX());
			debugError();
		}
		if (!XX.contains("MERGED") && merged) {
			System.out.println("shouldn't be merged " + getReadName());
			System.out.println(getAttributeXX());
			debugError();
		}
		if (!XX.contains("INTRON") && intronic) {
			System.out.println("shouldn't be intronic " + getReadName());
			System.out.println(getAttributeXX());
			debugError();
		}
		if (XX.contains("INTRON") && !intronic) {
			System.out.println("should be intronic " + getReadName());
			System.out.println(getAttributeXX());
			debugError();
		}
		if (XX.contains("antisense:true") && !antisense) {
			System.out.println("should be antisense " + getReferenceName() + " " + forward.getReadNegativeStrandFlag());
			debugError();
		}
	}

	public void debugError() {
		System.out.println(XX);
		System.out.print("forward:");
		for (AlignmentBlock ab : forward.getAlignmentBlocks()) {
			System.out.print((ab.getReferenceStart() - 1) + "-" + (ab.getReferenceStart() + ab.getLength() - 2) + "|");
		}
		System.out.println();
		System.out.print("reverse:");
		for (AlignmentBlock ab : reverse.getAlignmentBlocks()) {
			System.out.print((ab.getReferenceStart() - 1) + "-" + (ab.getReferenceStart() + ab.getLength() - 2) + "|");
		}
		System.out.println();
		System.out.print("forwardReadStart:");
		for (AlignmentBlock ab : forward.getAlignmentBlocks()) {
			System.out.print((ab.getReadStart() - 1) + "-" + (ab.getReadStart() + ab.getLength() - 2) + "|");
		}
		System.out.println();
		System.out.print("reverseReadStart:");
		for (AlignmentBlock ab : reverse.getAlignmentBlocks()) {
			System.out.print((ab.getReadStart() - 1) + "-" + (ab.getReadStart() + ab.getLength() - 2) + "|");
		}
		System.out.println();
		System.out.print("forwardMe:");
		for (Interval ab : blocksForward) {
			System.out.print(ab.toString() + "|");
		}
		System.out.println();
		System.out.print("reverseMe:");
		for (Interval ab : blocksReverse) {
			System.out.print(ab.toString() + "|");
		}
		System.out.println();
		System.out.print("intronsFor:");
		for (Interval ab : intronsForward) {
			System.out.print(ab.toString() + "|");
		}
		System.out.println();
		System.out.print("intronsRev:");
		for (Interval ab : intronsReverse) {
			System.out.print(ab.toString() + "|");
		}
		System.out.println();
		System.exit(1);
	}

	public void calcMissmatches() {
		Integer missInForw = (Integer) forward.getAttribute("NM");
		Integer missInRev = (Integer) reverse.getAttribute("NM");
		if (missInForw == null || missInRev == null) {
			missInForw = (Integer) forward.getAttribute("nM");
			missInRev = (Integer) reverse.getAttribute("nM");
		}
		if (missInForw == null || missInRev == null) {
			missInForw = (Integer) forward.getAttribute("XM");
			missInRev = (Integer) reverse.getAttribute("XM");
		}
		if (isStarMapping) {
			mismatchCount = missInForw;
		} else {
			mismatchCount = missInForw + missInRev;
		}
	}

	public void calcClipping() {
		int clipp = Math.abs(forward.getAlignmentStart() - forward.getUnclippedStart())
				+ Math.abs(forward.getAlignmentEnd() - forward.getUnclippedEnd())
				+ Math.abs(reverse.getAlignmentStart() - reverse.getUnclippedStart())
				+ Math.abs(reverse.getAlignmentEnd() - reverse.getUnclippedEnd());

		this.clippingSize = clipp;
	}

	public int getSplitCount() {
		if (splitCount == -2) {
			calcSplitCount();
		}
		return splitCount;
	}

	/**
	 * returns -1 if splitInconsistent
	 */
	public void calcSplitCount() {
		if (checkIfSplitInconsistent()) {
			splitCount = -1;
		} else {
			splitCount = intronsForward.size() + intronsReverse.size();
			for (Interval i : intronsForward) {
				for (Interval j : intronsReverse) {
					if (i.overlaps(j)) {
						splitCount--;
					}

				}
			}
		}
	}

	public boolean checkIfSplitInconsistent() {
		for (Interval intronFw : intronsForward) {
			for (Interval i : blocksReverse) {
				if (i.overlaps(intronFw)) {
					return true;
				}
			}
		}
		for (Interval intronRv : intronsReverse) {
			for (Interval i : blocksForward) {
				if (i.overlaps(intronRv)) {
					return true;
				}
			}
		}
		return false;
	}

	public LinkedList<Gene> getMatchedGenes() {
		if (matchedGenes == null) {
			// get genes spanning forward read and genes spanning reverse reads
			// spanning means: gene.start <= start && gene.stop >= stop
			LinkedList<Gene> possibleGenes = BAMFileReader.ga.getChromosome(getReferenceName())
					.getSpecificStrandGenes(forward.getReadNegativeStrandFlag())
					.getIntervalsSpanning(genomicRegion.getStart(), genomicRegion.getStop(), new LinkedList<>());
			if (possibleGenes.size() == 0) {
				intergenic = BAMFileReader.ga.getChromosome(getReferenceName())
						.getSpecificStrandGenes(forward.getReadNegativeStrandFlag())
						.getIntervalsIntersecting(genomicRegion.getStart(), genomicRegion.getStop(), new LinkedList<>())
						.isEmpty();

				if (!intergenic)
					geneDistance = 0;
				else
					getGeneDist();
				antisense = checkIfAntisense();
				matchedGenes = new LinkedList<>();
				return matchedGenes;
			}
			// for test:
			// matchedGenes = possibleGenes;
			// return matchedGenes;

			HashSet<Exon> nextExons = null, nextStrandExons = new HashSet<>();
			HashSet<Transcript> possibleTrsFW = new HashSet<>();// if
			// empty
			// -->
			// no
			// exon
			// spanning
			// regionBlock
			// -->
			// not
			// matching
			HashSet<Gene> forwardMatchingGenesMerged = new HashSet<>(); // matching
																		// at
																		// least
																		// merged
			HashSet<Gene> intronicGenes = new HashSet<>();
			HashSet<Transcript> possiblePerfectTrs = new HashSet<>();
			for (Gene g : possibleGenes) {
				HashSet<Transcript> trOverlaps = new HashSet<>();
				possibleTrsFW = new HashSet<>();
				for (Interval forwardInt : blocksForward) {
					nextExons = g.getAllExonsSorted().getIntervalsSpanning(forwardInt.getStart(), forwardInt.getStop(),
							new HashSet<>());
					if (!nextExons.isEmpty()) {
						nextStrandExons.addAll(nextExons);
						if (possibleTrsFW.isEmpty()) {
							for (Exon e : nextExons) {
								possibleTrsFW.addAll(e.getParentalTranscripts());
							}
						} else {
							for (Exon e : nextExons) {
								for (Transcript t : e.getParentalTranscripts()) {
									if (possibleTrsFW.contains(t)) {
										trOverlaps.add(t);
									}
								}
							}
							possibleTrsFW = new HashSet<>(trOverlaps);
							trOverlaps = new HashSet<>();
						}
					} else {
						nextStrandExons = new HashSet<>();
						trOverlaps = new HashSet<>();
						possibleTrsFW = new HashSet<>();
						intronicGenes.add(g);
						break;
					}
				}
				if (!nextStrandExons.isEmpty()) {
					forwardMatchingGenesMerged.add(g);
				}
				if (!possibleTrsFW.isEmpty()) {
					possiblePerfectTrs.addAll(possibleTrsFW);
				}
			}
			nextStrandExons = new HashSet<>();
			LinkedList<Gene> allMatched = new LinkedList<>();
			for (Gene g : forwardMatchingGenesMerged) {
				HashSet<Transcript> trOverlaps = new HashSet<>();
				for (Interval reverseInt : blocksReverse) {
					nextExons = g.getAllExonsSorted().getIntervalsSpanning(reverseInt.getStart(), reverseInt.getStop(),
							new HashSet<>());
					if (!nextExons.isEmpty()) {
						nextStrandExons.addAll(nextExons);
						if (!possiblePerfectTrs.isEmpty()) {
							for (Exon e : nextExons) {
								for (Transcript t : e.getParentalTranscripts()) {
									if (possiblePerfectTrs.contains(t)) {
										trOverlaps.add(t);
									}
								}
							}
							HashSet<Transcript> tmp = new HashSet<>();
							for (Transcript t : possiblePerfectTrs) {
								if (!t.getParentalGene().equals(g)) {
									tmp.add(t);
								}
							}
							possiblePerfectTrs = tmp;
							possiblePerfectTrs.addAll(trOverlaps);
							trOverlaps = new HashSet<>();
						}
					} else {
						nextStrandExons = new HashSet<>();
						intronicGenes.add(g);
						HashSet<Transcript> tmp = new HashSet<>();
						for (Transcript t : possiblePerfectTrs) {
							if (!t.getParentalGene().equals(g)) {
								tmp.add(t);
							}
						}
						possiblePerfectTrs = tmp;
						break;
					}
				}
				if (!nextStrandExons.isEmpty()) {
					allMatched.add(g);
				}
			}
			// all matched contains merged; possiblePerfectTrs contains possibly
			// perfect hit transcripts --> check if perfect hit
			// intronic genes contains intronic genes
			HashSet<Gene> matchedGenesAndTrs = new HashSet<>();
			matchedTranscripts = checkTranscripts(possiblePerfectTrs);
			if (debugCheck) {
				for (Transcript t : matchedTranscripts)
					System.out.print(t.getId() + "|");
				System.out.println();
			}
			for (Transcript t : matchedTranscripts) {
				matchedGenesAndTrs.add(t.getParentalGene());
			}
			matchedGenes = new LinkedList<>();
			matchedGenes.addAll(matchedGenesAndTrs);
			if (matchedGenes.isEmpty()) { // zwar keine transcript maps aber
											// merged
				for (Gene g : possibleGenes) {
					if (checkIfMerged(g)) {
						matchedGenes.add(g);
						merged = true;
					}
				}
				if (merged) {
					return matchedGenes;
				}
			} else {
				return matchedGenes;
			}
			if (allMatched.isEmpty()) {
				// no transcript can map because merged is false --> intronic
				intronic = true;
				matchedGenes = new LinkedList<>();
				matchedGenes.addAll(intronicGenes);
				return matchedGenes;
			}
		}
		return matchedGenes;

	}

	public boolean checkIfAntisense() {
		LinkedList<Gene> possibleGenes = BAMFileReader.ga.getChromosome(getReferenceName())
				.getSpecificStrandGenes(!forward.getReadNegativeStrandFlag())
				.getIntervalsSpanning(genomicRegion.getStart(), genomicRegion.getStop(), new LinkedList<>());
		if (possibleGenes.isEmpty())
			return false;
		OUTER: for (Gene g : possibleGenes) {
			for (Interval forwardInt : blocksForward) {
				if (g.getAllExonsSorted()
						.getIntervalsSpanning(forwardInt.getStart(), forwardInt.getStop(), new LinkedList<>())
						.isEmpty()) {
					continue OUTER;
				}
			}
			for (Interval reverseInt : blocksReverse) {
				if (g.getAllExonsSorted()
						.getIntervalsSpanning(reverseInt.getStart(), reverseInt.getStop(), new LinkedList<>())
						.isEmpty()) {
					continue OUTER;
				}
			}
			return true;
		}
		return false;
	}

	public boolean checkIfMerged(Gene g) {
		IntervalTree<Interval> mergedExons = g.getUnionTranscript();
		for (Interval forwardInt : blocksForward) {
			if (mergedExons.getIntervalsSpanning(forwardInt.getStart(), forwardInt.getStop(), new HashSet<>())
					.isEmpty())
				return false;
		}
		for (Interval reverseInt : blocksReverse) {
			if (mergedExons.getIntervalsSpanning(reverseInt.getStart(), reverseInt.getStop(), new HashSet<>())
					.isEmpty())
				return false;
		}
		return true;
	}

	public int getGeneDist() {
		if (geneDistance == Integer.MIN_VALUE) {
			LinkedList<Gene> neighbourLeft = BAMFileReader.ga.getChromosome(getReferenceName())
					.getSpecificStrandGenes(forward.getReadNegativeStrandFlag())
					.getIntervalsLeftNeighbor(genomicRegion.getStart(), genomicRegion.getStop(), new LinkedList<>()),
					neighbourRight = BAMFileReader.ga.getChromosome(getReferenceName())
							.getSpecificStrandGenes(forward.getReadNegativeStrandFlag()).getIntervalsRightNeighbor(
									genomicRegion.getStart(), genomicRegion.getStop(), new LinkedList<>());
			if (!neighbourLeft.isEmpty() || !neighbourRight.isEmpty()) {
				if (neighbourLeft.isEmpty()) {
					geneDistance = neighbourRight.getFirst().getStart() - genomicRegion.getStop() - 1;
				} else {
					if (neighbourRight.isEmpty()) {
						geneDistance = genomicRegion.getStart() - neighbourLeft.getFirst().getStop() - 1;
					} else {
						geneDistance = Math.min((genomicRegion.getStart() - neighbourLeft.getFirst().getStop()),
								(neighbourRight.getFirst().getStart() - genomicRegion.getStop())) - 1;
					}
				}
			}
			if (geneDistance < 0) {
				geneDistance = 0;
			}
		}
		return geneDistance;
	}

	private LinkedList<Transcript> checkTranscripts(HashSet<Transcript> possibleTrs) {
		LinkedList<Transcript> ret = new LinkedList<>();
		for (Transcript tr : possibleTrs) {
			if (checkTranscript(tr)) {
				ret.add(tr);
			}
		}
		return ret;
	}

	public boolean checkTranscript(Transcript tr) {
		boolean fw = false, rv = false;
		if (blocksForward.size() == 1) {
			fw = true;
		} else {
			for (Interval i : intronsForward) {
				if (tr.getIntrons().getIntervalsEqual(i.getStart(), i.getStop(), new LinkedList<>()).isEmpty())
					return false;
			}
			fw = true;
		}
		if (blocksReverse.size() == 1) {
			rv = true;
		} else {
			for (Interval i : intronsReverse) {
				if (tr.getIntrons().getIntervalsEqual(i.getStart(), i.getStop(), new LinkedList<>()).isEmpty())
					return false;
			}
			rv = true;
		}
		return fw && rv;
	}

	public String getAttributeXX() {
		String s = "mm:" + mismatchCount + "\tclipping:" + clippingSize + "\tgcount:" + matchedGenes.size()
				+ "\tnsplit:" + splitCount + "\tgdist:" + geneDistance + "\tantisense:" + antisense + "\tpcrindex:"
				+ pcrIndex;
		s += "\tgenes: ";
		for (Gene g : matchedGenes) {
			s += g.getId() + "," + g.getBiotype() + ";";
		}
		if (matchedTranscripts != null) {
			for (Transcript t : matchedTranscripts) {
				s += t.getId() + ";";
			}
		}
		return s;
	}

	public SAMRecord getForward() {
		return forward;
	}

	public SAMRecord getReverse() {
		return reverse;
	}

	public String getReadName() {
		return forward.getReadName();
	}

	public String getReferenceName() {
		return forward.getReferenceName();
	}

	public boolean checkIfOutputEqualsRef() {
		String[] o = getAttributeXX().split("\t"), r = XX.split("\t");
		for (int i = 0; i < 3; i++) {
			if (!o[i].equals(r[i])) {
				System.out.println(getAttributeXX() + "\n" + XX);
				return false;
			}
		}
		return true;
	}

}
