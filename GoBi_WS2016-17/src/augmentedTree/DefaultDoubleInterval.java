package augmentedTree;

public class DefaultDoubleInterval<T> implements DoubleInterval {

	private double start;
	private double stop;
	private T data;
	public DefaultDoubleInterval(double start, double stop, T data) {
		super();
		this.start = start;
		this.stop = stop;
		this.data = data;
	}
	public double getStart() {
		return start;
	}
	public void setStart(double start) {
		this.start = start;
	}
	public double getStop() {
		return stop;
	}
	public void setStop(double stop) {
		this.stop = stop;
	}
	
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		return String.valueOf(data)+" ("+start+","+stop+")";
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DefaultDoubleInterval))
			return false;
		@SuppressWarnings("unchecked")
		DefaultDoubleInterval<T> o = (DefaultDoubleInterval<T>)obj;
		return o.start==start && o.stop==stop && data.equals(o.data);
	}
	
	public int hashCode() {
		return (int)(Double.doubleToLongBits(start)+(Double.doubleToLongBits(stop)<<13))+(data!=null?data.hashCode()<<17:0);
	}
	
}
