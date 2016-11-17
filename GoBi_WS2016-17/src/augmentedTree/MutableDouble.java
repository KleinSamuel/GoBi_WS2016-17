package augmentedTree;

public class MutableDouble extends Number{
	public double N;
	public MutableDouble(double N) {
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
		return (float)N;
	}
	@Override
	public int intValue() {
		return (int)N;
	}
	@Override
	public long longValue() {
		return (long)N;
	}
}
