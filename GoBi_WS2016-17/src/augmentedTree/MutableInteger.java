package augmentedTree;

public class MutableInteger extends Number implements Comparable<MutableInteger>{
	public int N;
	public MutableInteger(int N) {
		this.N = N;
	}
	@Override
	public String toString() {
		return N+"";
	}
	@Override
	public double doubleValue() {
		return N;
	}
	@Override
	public float floatValue() {
		return N;
	}
	@Override
	public int intValue() {
		return N;
	}
	@Override
	public long longValue() {
		return N;
	}
	@Override
	public int compareTo(MutableInteger o) {
		return N-o.N;
	}
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MutableInteger))
			return false;
		MutableInteger n = (MutableInteger) obj;
		return N==n.N;
	}
	@Override
	public int hashCode() {
		return N;
	}
}
