package assignment_3;

import java.util.HashSet;
import java.util.LinkedList;

import genomeAnnotation.Exon;
import genomeAnnotation.Gene;
import genomeAnnotation.Transcript;
import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;
import javafx.util.Pair;
import util.GenRegVecUtil;
import util.Interval;

public class ReadPair {

	private SAMRecord forward, reverse;
	private boolean isStarMapping;
	private LinkedList<Interval> blocksForward, blocksReverse;
	private LinkedList<Interval> intronsForward, intronsReverse;
	private LinkedList<Gene> matchedGenes = null;
	private LinkedList<Transcript> matchedTranscripts;
	private int mismatchCount = 0, clippingSize = 0, splitCount = -2, geneDistance = 0, pcrIndex = 0;
	private boolean antisense = false;
	private Interval genomicRegion;

	public ReadPair(SAMRecord forward, SAMRecord reverse, boolean isStarMapping) {
		this.forward = forward;
		this.reverse = reverse;
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
			return;
		}
		if (splitCount > -1) {
			if (splitCount != (Integer.parseInt(((String) forward.getAttribute("XX")).split("\t")[3].split(":")[1]))) {
				System.out.println(splitCount + "!="
						+ Integer.parseInt(((String) forward.getAttribute("XX")).split("\t")[3].split(":")[1]));

				debugError();
			}
			calcMissmatches();
			calcClipping();
			getMatchedGenes();
		}
	}

	public void debugError() {
		System.out.println(forward.getAttribute("XX"));
		System.out.println(reverse.getAttribute("XX"));
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
			HashSet<Gene> possibleForwardGene = BAMFileReader.ga.getChromosome(getReferenceName())
					.getSpecificStrandGenes(!forward.getReadNegativeStrandFlag()).getIntervalsSpanning(
							blocksForward.getFirst().getStart(), blocksForward.getLast().getStop(), new HashSet<>()),
					possibleReverseGene = BAMFileReader.ga.getChromosome(getReferenceName())
							.getSpecificStrandGenes(!forward.getReadNegativeStrandFlag())
							.getIntervalsSpanning(blocksReverse.getFirst().getStart(),
									blocksReverse.getLast().getStop(), new HashSet<>());
			HashSet<Gene> possibleMatchedGenes = new HashSet<>();
			for (Gene g : possibleForwardGene) {
				if (possibleReverseGene.contains(g)) {
					possibleMatchedGenes.add(g);
				}
			}
			if (possibleMatchedGenes.size() == 0) {
				matchedGenes = new LinkedList<>();
				return matchedGenes;
			}
			// for test:
			matchedGenes = new LinkedList<>();
			matchedGenes.addAll(possibleMatchedGenes);
			return matchedGenes;

			// HashSet<Exon> exonsSpanningForwardBlocks = new HashSet<>(),
			// exonsSpanningReverseBlocks = new HashSet<>();
			// HashSet<Exon> nextExons = null, nextStrandExons = new
			// HashSet<>(); // if
			// // empty
			// // -->
			// // no
			// // exon
			// // spanning
			// // regionBlock
			// // -->
			// // not
			// // matching
			// HashSet<Gene> forwardMatchingGenes = new HashSet<>();
			// for (Gene g : possibleMatchedGenes) {
			// for (Interval forwardInt : blocksForward) {
			// nextExons =
			// g.getAllExonsSorted().getIntervalsSpanning(forwardInt.getStart(),
			// forwardInt.getStop(),
			// new HashSet<>());
			// if (!nextExons.isEmpty()) {
			// nextStrandExons.addAll(nextExons);
			// } else {
			// nextStrandExons = new HashSet<>();
			// break;
			// }
			// }
			// if (!nextStrandExons.isEmpty()) {
			// forwardMatchingGenes.add(g);
			// exonsSpanningForwardBlocks.addAll(nextStrandExons);
			// }
			// }
			// nextStrandExons = new HashSet<>();
			// LinkedList<Gene> allMatched = new LinkedList<>();
			// for (Gene g : forwardMatchingGenes) {
			// for (Interval reverseInt : blocksReverse) {
			// nextExons =
			// g.getAllExonsSorted().getIntervalsSpanning(reverseInt.getStart(),
			// reverseInt.getStop(),
			// new HashSet<>());
			// if (!nextExons.isEmpty()) {
			// nextStrandExons.addAll(nextExons);
			// } else {
			// nextStrandExons = new HashSet<>();
			// break;
			// }
			// }
			// if (!nextStrandExons.isEmpty()) {
			// allMatched.add(g);
			// exonsSpanningReverseBlocks.addAll(nextStrandExons);
			// }
			// }
			// matchedGenes = allMatched;
		}
		return matchedGenes;
	}

	public String getAttributeXX() {
		return "mm:" + mismatchCount + "\tclipping:" + clippingSize + "\tgcount:" + matchedGenes.size() + "\tnsplit:"
				+ splitCount + "\tgdist:" + geneDistance + "\tantisense:" + antisense + "\tpcrindex:" + pcrIndex;
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
		String[] o = getAttributeXX().split("\t"), r = ((String) forward.getAttribute("XX")).split("\t");
		for (int i = 0; i < 3; i++) {
			if (!o[i].equals(r[i])) {
				System.out.println(getAttributeXX() + "\n" + forward.getAttribute("XX"));
				return false;
			}
		}
		return true;
	}

}
