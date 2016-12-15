package util;

import debugStuff.DebugMessageFactory;

public class Interval implements augmentedTree.Interval {

	private int start, stop;

	public Interval(int start, int stop) {
		this.start = start;
		this.stop = stop;
	}

	@Override
	public int getStart() {
		return start;
	}

	@Override
	public int getStop() {
		return stop;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public void setStop(int stop) {
		this.stop = stop;
	}

	public int getLength() {
		return this.stop - this.start + 1;
	}

	@Override
	public String toString() {
		return start + "-" + stop;
	}

	/**
	 * 
	 * @param i
	 * @return
	 */
	public boolean overlaps(Interval i) {
		return !(i.getStop() < this.start || i.getStart() > this.stop);
	}

	/**
	 * returns 1 if equal, 0 if this contains i, -1 if none of that
	 * 
	 * @param i
	 * @return
	 */
	public int compareIntervals(Interval i) {
		if (start == i.getStart() && stop == i.getStop()) {
			return 1;
		}
		if (start <= i.getStart() && stop >= i.getStop()) {
			return 0;
		}
		return -1;
	}

	/**
	 * returns Interval
	 * 
	 * @param s
	 *            splits string by '-'
	 */
	public static Interval parseInterval(String s, int startPlusMinus, int stopPlusMinus) {
		int i = s.indexOf("-");
		if (i < 0) {
			DebugMessageFactory.printErrorDebugMessage(true, "couldn`t parse " + s + " to Interval. '-' is missing");
			return null;
		}
		return new Interval(Integer.parseInt(s.substring(0, i)) + startPlusMinus,
				Integer.parseInt(s.substring(i + 1)) + stopPlusMinus);
	}

}