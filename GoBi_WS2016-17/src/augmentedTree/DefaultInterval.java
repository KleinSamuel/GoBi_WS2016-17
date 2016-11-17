package augmentedTree;

import java.util.Arrays;


public class DefaultInterval<T> implements Interval {

	private int start;
	private int stop;
	private T data;
	public DefaultInterval(int start, int stop, T data) {
		super();
		this.start = start;
		this.stop = stop;
		this.data = data;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getStop() {
		return stop;
	}
	public void setStop(int stop) {
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
		if (!(obj instanceof DefaultInterval))
			return false;
		@SuppressWarnings("unchecked")
		DefaultInterval<T> o = (DefaultInterval<T>)obj;
		return o.start==start && o.stop==stop && data.equals(o.data);
	}
	
	public int hashCode() {
		return start+(stop<<13)+(data!=null?data.hashCode()<<17:0);
	}
	
}
