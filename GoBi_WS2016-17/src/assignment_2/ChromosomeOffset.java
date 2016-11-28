package assignment_2;

public class ChromosomeOffset {

	private String chrId;
	private long offsetStart, offsetStop;

	public ChromosomeOffset(String chrId, long offsetStart, long offsetStop) {
		this.chrId = chrId;
		this.offsetStart = offsetStart;
		this.offsetStop = offsetStop;
	}

	public String getChrId() {
		return chrId;
	}

	public long getOffsetStart() {
		return offsetStart;
	}

	public long getOffsetStop() {
		return offsetStop;
	}

	public int getLength() {
		return (int) (offsetStop - offsetStart) + 1;
	}

	@Override
	public int hashCode() {
		return chrId.hashCode();
	}

	@Override
	public String toString() {
		return chrId + "\t" + offsetStart + "\t" + offsetStop;
	}

}
