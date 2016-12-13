package assignment_2.Task_4;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import genomeAnnotation.GenomeAnnotation;
import genomeAnnotation.Transcript;
import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;
import util.Interval;

@SuppressWarnings("unchecked")
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
				if (okStat != null && okStat.equals("wrongChr")) {
					if (fwStat == null) {
						calculateOKetcForSingleRegVec(mergeAndParseAlignmentBlocks(forward),
								(Vector<Interval>) simulmap.get(6), true);
					}
					if (rwStat == null) {
						calculateOKetcForSingleRegVec(mergeAndParseAlignmentBlocks(reverse),
								(Vector<Interval>) simulmap.get(7), false);
					}
					return okStat;
				}
				calculateOKetc(mergeAndParseAlignmentBlocks(forward), (Vector<Interval>) simulmap.get(6),
						mergeAndParseAlignmentBlocks(reverse), (Vector<Interval>) simulmap.get(7));
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
					if (okStat != null && okStat.equals("wrongChr")) {
						if (fwStat == null) {
							compareTranscriptomeNotConvertedForSingleRegvec((Interval) simulmap.get(4), true);
						}
						if (rwStat == null) {
							compareTranscriptomeNotConvertedForSingleRegvec((Interval) simulmap.get(5), false);
						}
						return okStat;
					}
					compareTranscriptomeNotConverted(simulmap);
				} else {
					if (transcriptome && convert_to_genomic) {
						String chromosomeId = (String) simulmap.get(1);
						Transcript forwardRead = ga.getTranscript(forward.getReferenceName()),
								reverseRead = ga.getTranscript(reverse.getReferenceName());
						if (!forwardRead.getParentalGene().getChromosome().getID().equals(chromosomeId)) {
							fwStat = "wrongChr";
							okStat = "wrongChr";
						}
						if (!reverseRead.getParentalGene().getChromosome().getID().equals(chromosomeId)) {
							rwStat = "wrongChr";
							okStat = "wrongChr";
						}
						if (okStat != null && okStat.equals("wrongChr")) {
							if (fwStat == null) {
								calculateOKetcForSingleRegVec(
										forwardRead.getGenomicRegionVector(forward.getAlignmentStart() - 1,
												forward.getAlignmentEnd() - 2),
										(Vector<Interval>) simulmap.get(6), true);
							}
							if (rwStat == null) {
								calculateOKetcForSingleRegVec(
										reverseRead.getGenomicRegionVector(reverse.getAlignmentStart() - 1,
												reverse.getAlignmentEnd() - 2),
										(Vector<Interval>) simulmap.get(7), false);
							}
							return okStat;
						}
						calculateOKetc(
								forwardRead.getGenomicRegionVector(forward.getAlignmentStart() - 1,
										forward.getAlignmentEnd() - 2),
								(Vector<Interval>) simulmap.get(6),
								reverseRead.getGenomicRegionVector(reverse.getAlignmentStart() - 1,
										reverse.getAlignmentEnd() - 2),
								(Vector<Interval>) simulmap.get(7));
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

	/**
	 * return "ok" if 1, "partial" if 0, else "everythingElse"
	 * 
	 * @param stat
	 * @return
	 */
	public String parseStat(int stat) {
		switch (stat) {
		case -1:
			return "everythingElse";
		case 0:
			return "partial";
		case 1:
			return "ok";
		}
		return null;
	}

	public void calculateOKetcForSingleRegVec(Vector<Interval> regVec, Vector<Interval> regVecRef, boolean forward) {
		int oKstat = compareRegionVectors(regVec, regVecRef);
		if (forward) {
			fwStat = parseStat(oKstat);
		} else {
			rwStat = parseStat(oKstat);
		}
	}

	public void calculateOKetc(Vector<Interval> fwRegVec, Vector<Interval> fwRegVecRef, Vector<Interval> rwRegVec,
			Vector<Interval> rwRegVecRef) {
		int fwOKstat = compareRegionVectors(fwRegVec, fwRegVecRef);
		int rwOKstat = compareRegionVectors(rwRegVec, rwRegVecRef);
		fwStat = parseStat(fwOKstat);
		rwStat = parseStat(rwOKstat);
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

	public int compareRegionVectors(Vector<Interval> regvec1, Vector<Interval> regvecRef) {
		if (regvec1.size() != regvecRef.size())
			return -1;
		int returnSoFar = 1; // ok
		Iterator<Interval> reg1it = regvec1.iterator(), reg2it = regvecRef.iterator();
		Interval region1 = null, region2 = null;
		while (reg1it.hasNext() && reg2it.hasNext()) {
			region1 = reg1it.next();
			region2 = reg2it.next();
			int comparison = region2.compareIntervals(region1);
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

	public void compareTranscriptomeNotConvertedForSingleRegvec(Interval trRef, boolean forwardRead) {
		SAMRecord read = null;
		if (forwardRead)
			read = forward;
		else
			read = reverse;
		int comp = trRef.compareIntervals(new Interval(read.getAlignmentBlocks().get(0).getReferenceStart() - 1,
				read.getAlignmentBlocks().get(0).getReferenceStart() + read.getAlignmentBlocks().get(0).getLength()
						- 2));
		if (forwardRead)
			fwStat = parseStat(comp);
		else
			rwStat = parseStat(comp);
	}

	public void compareTranscriptomeNotConverted(ArrayList<Object> simulmap) {
		int fw = compareTrRegVecWithTrRegVec(forward, (Interval) simulmap.get(4));
		int rw = compareTrRegVecWithTrRegVec(reverse, (Interval) simulmap.get(5));
		fwStat = parseStat(fw);
		rwStat = parseStat(rw);
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

}