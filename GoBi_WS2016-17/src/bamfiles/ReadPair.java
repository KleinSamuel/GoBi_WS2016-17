package bamfiles;

import java.util.LinkedList;

import augmentedTree.IntervalTree;
import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;
import util.GenRegVecUtil;
import util.Interval;

public class ReadPair {

	private SAMRecord forward, reverse;
	private boolean isStarMapping;
	private IntervalTree<Interval> blocksForward, blocksReverse;
	private LinkedList<Interval> intronsForward, intronsReverse;
	private int mismatchCount = 0, clippingSize = 0;

	public ReadPair(SAMRecord forward, SAMRecord reverse, boolean isStarMapping) {
		this.forward = forward;
		this.reverse = reverse;
		this.isStarMapping = isStarMapping;
		blocksForward = new IntervalTree<>();
		for (AlignmentBlock ab : forward.getAlignmentBlocks()) {
			blocksForward.add(new Interval(ab.getReferenceStart(), ab.getReferenceStart() + ab.getLength() - 1));
		}
		blocksForward = GenRegVecUtil.merge(blocksForward);
		intronsForward = GenRegVecUtil.getIntrons(blocksForward);
		blocksReverse = new IntervalTree<>();
		for (AlignmentBlock ab : reverse.getAlignmentBlocks()) {
			blocksReverse.add(new Interval(ab.getReferenceStart(), ab.getReferenceStart() + ab.getLength() - 1));
		}
		blocksReverse = GenRegVecUtil.merge(blocksReverse);
		intronsReverse = GenRegVecUtil.getIntrons(blocksReverse);
	}

	public void calcMissmatches() {
		Integer missInForw = (Integer) forward.getAttribute("NM");
		Integer missInRev = (Integer) reverse.getAttribute("NM");
		if (missInForw == null || missInRev == null) {
			missInForw = (Integer) forward.getAttribute("nM");
			missInRev = (Integer) reverse.getAttribute("nM");
		} else {
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

	/**
	 * returns -1 if splitInconsistent
	 */
	public int getSplitCount() {
		if (checkIfSplitInconsistent()) {
			return -1;
		} else {
			return intronsForward.size() + intronsReverse.size();
		}
	}

	public boolean checkIfSplitInconsistent() {
		if (blocksForward.size() == 1 && blocksReverse.size() == 1) {
			return false;
		}

		for (Interval i : intronsForward) {
			for (Interval j : blocksReverse) {
				if (i.overlaps(j)) {
					return true;
				}
			}
		}
		for (Interval i : intronsReverse) {
			for (Interval j : blocksForward) {
				if (i.overlaps(j)) {
					return true;
				}
			}
		}
		return false;
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

}
