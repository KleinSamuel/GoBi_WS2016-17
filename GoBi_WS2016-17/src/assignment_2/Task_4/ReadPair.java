package assignment_2.Task_4;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import genomeAnnotation.GenomeAnnotation;
import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;
import javafx.util.Pair;
import util.Interval;

public class ReadPair {

	private int id;
	private SAMRecord forward, reverse;
	private String okStat = null;
	public String fwStat = null, rwStat = null;

	public boolean fwSpliced = false, rwSpliced = false, spliced = false, mismatchesFW = false, mismatchesRW = false,
			mismatches = false, splitLonger5NoMisFW = true, splitLonger5NoMisRW = true, splitLonger5NoMis = true;

	public ReadPair(SAMRecord first, SAMRecord second) {
		if (first.getFirstOfPairFlag()) {
			forward = first;
			reverse = second;
		} else {
			forward = second;
			reverse = first;
		}
		id = Integer.parseInt(first.getReadName());
	}

	public String getOKStat() {
		return okStat;
	}

	public String getOKstat(ArrayList<Object> simulmap, boolean transcriptome, boolean convert_to_genomic,
			GenomeAnnotation ga) {
		if (okStat == null) {
			if (!transcriptome && !convert_to_genomic) {
				if (!forward.getReferenceName().equals((String) simulmap.get(1))) {
					fwStat = "wrongChr";
					okStat = "wrongChr";
				}
				if (!reverse.getReferenceName().equals((String) simulmap.get(1))) {
					rwStat = "wrongChr";
					okStat = "wrongChr";
				}
				if (okStat != null && okStat.equals("wrongChr"))
					return okStat;
				calculateOKetc((Vector<Interval>) simulmap.get(6), (Vector<Interval>) simulmap.get(7), false, null,
						simulmap);
			} else {
				if (transcriptome && !convert_to_genomic) {
					if (!forward.getReferenceName().equals((String) simulmap.get(3))) {
						fwStat = "wrongChr";
						okStat = "wrongChr";
					}
					if (!reverse.getReferenceName().equals((String) simulmap.get(3))) {
						rwStat = "wrongChr";
						okStat = "wrongChr";
					}
					if (okStat != null && okStat.equals("wrongChr"))
						return okStat;
					compareTranscriptomeNotConverted(simulmap);
				} else {
					if (transcriptome && convert_to_genomic) {
						String chr = (String) simulmap.get(1), gene = (String) simulmap.get(2),
								tr = (String) simulmap.get(3);
						if (!forward.getReferenceName().equals(tr)) {
							fwStat = "wrongChr";
							okStat = "wrongChr";
						}
						if (!reverse.getReferenceName().equals(tr)) {
							rwStat = "wrongChr";
							okStat = "wrongChr";
						}
						if (okStat != null && okStat.equals("wrongChr"))
							return okStat;
						calculateOKetc((Vector<Interval>) simulmap.get(6), (Vector<Interval>) simulmap.get(7), true, ga,
								simulmap);
					}
				}
			}
		}
		calcOtherStuff(simulmap);
		return okStat;
	}

	private void calcOtherStuff(ArrayList<Object> simulmap) {
		fwSpliced = ((Vector<Interval>) simulmap.get(6)).size() > 1;
		rwSpliced = ((Vector<Interval>) simulmap.get(7)).size() > 1;
		spliced = fwSpliced || rwSpliced;
		mismatchesFW = ((Vector<Integer>) simulmap.get(8)).size() > 0;
		mismatchesRW = ((Vector<Integer>) simulmap.get(9)).size() > 0;
		mismatches = mismatchesFW || mismatchesRW;
		if (!mismatchesFW && fwSpliced) {
			boolean longer5 = true;
			for (Interval i : ((Vector<Interval>) simulmap.get(6))) {
				if (i.getLength() < 5) {
					longer5 = false;
				}
			}
			splitLonger5NoMisFW = longer5;
		}
		if (!mismatchesRW && rwSpliced) {
			boolean longer5 = true;
			for (Interval i : ((Vector<Interval>) simulmap.get(7))) {
				if (i.getLength() < 5) {
					longer5 = false;
				}
			}
			splitLonger5NoMisRW = longer5;
		}
		splitLonger5NoMis = splitLonger5NoMisFW && splitLonger5NoMisRW;
	}

	public int getId() {
		return id;
	}

	public SAMRecord getForward() {
		return forward;
	}

	public SAMRecord getReverse() {
		return reverse;
	}

	public void calculateOKetc(Vector<Interval> fwRegVec, Vector<Interval> rwRegVec, boolean convert_to_genomic,
			GenomeAnnotation ga, ArrayList<Object> simulmap) {
		int fwOKstat = compareRegVectorWithABs(forward, fwRegVec, convert_to_genomic, ga, simulmap);
		int rwOKstat = compareRegVectorWithABs(reverse, rwRegVec, convert_to_genomic, ga, simulmap);
		switch (fwOKstat) {
		case -1:
			fwStat = "everythingElse";
			break;
		case 0:
			fwStat = "partial";
			break;
		case 1:
			fwStat = "ok";
			break;
		}
		switch (rwOKstat) {
		case -1:
			rwStat = "everythingElse";
			break;
		case 0:
			rwStat = "partial";
			break;
		case 1:
			rwStat = "ok";
			break;
		}
		if (fwOKstat == 1 && rwOKstat == 1) {
			okStat = "ok";
		} else {
			if (fwOKstat == -1 || rwOKstat == -1) {
				okStat = "everythingElse";
			} else {
				okStat = "partial";
			}
		}
	}

	/**
	 * returns 1 if ok; 0 if partial; -1 if none of that
	 * 
	 * @param sam
	 */
	public int compareRegVectorWithABs(SAMRecord sam, Vector<Interval> regVec, boolean convert_to_genomic,
			GenomeAnnotation ga, ArrayList<Object> simulmap) {
		Vector<Interval> blocks;
		if (!convert_to_genomic) {
			blocks = mergeAndParseAlignmentBlocks(sam);
		} else {
			Interval i = new Interval(sam.getAlignmentBlocks().get(0).getReferenceStart() - 1,
					sam.getAlignmentBlocks().get(0).getReferenceStart() + sam.getAlignmentBlocks().get(0).getLength()
							- 2);
			String chr = (String) simulmap.get(1), gene = (String) simulmap.get(2), tr = (String) simulmap.get(3);
			Vector<Pair<Integer, Integer>> regs = ga.getChromosome(chr).getGene(gene).getTranscript(tr)
					.getGenomicRegionVector(i.getStart(), i.getStop());
			blocks = new Vector<>();
			for (Pair<Integer, Integer> p : regs) {
				blocks.add(new Interval(p.getKey(), p.getValue()));
			}
		}
		Vector<Interval> regions = regVec;
		if (blocks.size() != regions.size())
			return -1;
		int returnSoFar = 1; // ok
		Iterator<Interval> blockIt = blocks.iterator(), regionsIt = regions.iterator();
		Interval block = null, region = null;
		while (blockIt.hasNext() && regionsIt.hasNext()) {
			block = blockIt.next();
			region = regionsIt.next();
			int comparison = region.compareIntervals(block);
			if (comparison == -1) {
				return -1;
			}
			if (comparison == 0) {
				returnSoFar = 0;
			}
		}
		return returnSoFar;
	}

	/**
	 * returns 1 if ok; 0 if partial; -1 if none of that
	 * 
	 * @param sam
	 */
	public int compareTrRegVecWithTrRegVec(SAMRecord sam, Interval trRef) {
		return trRef.compareIntervals(new Interval(sam.getAlignmentBlocks().get(0).getReferenceStart() - 1,
				sam.getAlignmentBlocks().get(0).getReferenceStart() + sam.getAlignmentBlocks().get(0).getLength() - 2));
	}

	public void compareTranscriptomeNotConverted(ArrayList<Object> simulmap) {
		int fw = compareTrRegVecWithTrRegVec(forward, (Interval) simulmap.get(4));
		int rw = compareTrRegVecWithTrRegVec(reverse, (Interval) simulmap.get(5));
		switch (fw) {
		case -1:
			fwStat = "everythingElse";
			break;
		case 0:
			fwStat = "partial";
			break;
		case 1:
			fwStat = "ok";
			break;
		}
		switch (rw) {
		case -1:
			rwStat = "everythingElse";
			break;
		case 0:
			rwStat = "partial";
			break;
		case 1:
			rwStat = "ok";
			break;
		}
		if (fw == 1 && rw == 1) {
			okStat = "ok";
		} else {
			if (fw == -1 || rw == -1) {
				okStat = "everythingElse";
			} else {
				okStat = "partial";
			}
		}
	}

	/**
	 * returns a vector of intervals; alignmentBlocks like 100-101|101-200 are
	 * merged to 100-200 --> becomes 100-199
	 * 
	 * @param sam
	 * @return
	 */
	public Vector<Interval> mergeAndParseAlignmentBlocks(SAMRecord sam) {
		Vector<Interval> ret = new Vector<>();
		for (AlignmentBlock ab : sam.getAlignmentBlocks()) {
			ret.add(new Interval(ab.getReferenceStart() - 1, ab.getReferenceStart() + ab.getLength() - 2));
		}
		return ret;
		// Interval merged = null;
		// AlignmentBlock current = null;
		// Iterator<AlignmentBlock> blocks =
		// sam.getAlignmentBlocks().iterator();
		// if (blocks.hasNext()) {
		// current = blocks.next();
		// merged = new Interval(current.getReadStart(), current.getReadStart()
		// + current.getLength() - 1);
		// }
		// while (blocks.hasNext()) {
		// current = blocks.next();
		// if (merged.getStop() + 1 == current.getReadStart()) {
		// merged.setStop(merged.getStop() + current.getLength() - 1);
		// } else {
		// ret.add(merged);
		// merged = new Interval(current.getReadStart(), current.getReadStart()
		// + current.getLength() - 1);
		// }
		// }
		// if (merged != null) {
		// ret.add(merged);
		// }
		// return ret;
	}

	// public void calcMissmatches() {
	// Integer missInForw = null, missInRev = null;
	// if (forward.getAttribute("NM") != null && reverse.getAttribute("NM") !=
	// null) {
	// missInForw = (Integer) forward.getAttribute("NM");
	// missInRev = (Integer) reverse.getAttribute("NM");
	// } else {
	// if (forward.getAttribute("nM") != null && reverse.getAttribute("nM") !=
	// null) {
	// missInForw = (Integer) forward.getAttribute("nM");
	// missInRev = (Integer) reverse.getAttribute("nM");
	// } else if (forward.getAttribute("XM") != null &&
	// reverse.getAttribute("XM") != null) {
	// missInForw = (Integer) forward.getAttribute("XM");
	// missInRev = (Integer) reverse.getAttribute("XM");
	// }
	// }
	// if (this.protocol.contains("star")) {
	// this.missmatchCount = missInForw;
	// } else {
	// this.missmatchCount = missInForw + missInRev;
	// }
	// }

}
