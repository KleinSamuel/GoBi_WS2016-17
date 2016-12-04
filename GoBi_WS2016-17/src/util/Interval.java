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

	/**
	 * returns Interval
	 * 
	 * @param s
	 *            splits string by '-'
	 */
	public static Interval parseInterval(String s) {
		int i = s.indexOf("-");
		if (i < 0) {
			DebugMessageFactory.printErrorDebugMessage(true, "couldn`t parse " + s + " to Interval. '-' is missing");
			return null;
		}
		return new Interval(Integer.parseInt(s.substring(0, i)), Integer.parseInt(s.substring(i + 1)));
	}

}
