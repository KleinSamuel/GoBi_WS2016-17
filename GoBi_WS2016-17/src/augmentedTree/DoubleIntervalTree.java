package augmentedTree;


import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.Stack;

import augmentedTree.AugmentedTreeMap.Entry;


public class DoubleIntervalTree<I extends DoubleInterval> extends AugmentedTreeSet<I, MutableDouble> implements DoubleInterval {

	
	public DoubleIntervalTree() {
		super(new DoubleIntervalComparator<I>(), new DoubleIntervalTreeAugmentation<I>());
	}


	public DoubleIntervalTree(Collection<? extends I> coll) {
		super(new DoubleIntervalComparator<I>(), new DoubleIntervalTreeAugmentation<I>());
		addAll(coll);
	}


	public <C extends Collection<I>> C getIntervalsSpanning(double p, C re) {
		Stack<AugmentedTreeMap.Entry<I, ?>> stack = new Stack<AugmentedTreeMap.Entry<I,?>>();
		if (((AugmentedTreeMap<I,?,MutableDouble>)m).root!=null)
			stack.push(((AugmentedTreeMap<I,?,MutableDouble>)m).root);

		while (!stack.isEmpty()) {
			AugmentedTreeMap.Entry<I, ?> n = stack.pop();
			if (p<=getMaxStop(n)) {
				if (n.left!= null)
					stack.push(n.left);
				double a = n.getKey().getStart();
				double b = n.getKey().getStop();
				if (a<=p && p<=b)
					re.add(n.getKey());
				if (p>=n.getKey().getStart() && n.right != null)
					stack.push(n.right);
			}
		}
		return re;
	}

	/**
	 * Diffs: minStartDiff=-1 and maxStartDiff=3: only intervals are returned which have a startposition in [start-1:start+3]
	 * They have however at least to intersect!
	 * @param <C>
	 * @param start
	 * @param stop
	 * @param re
	 * @param startDiff
	 * @param stopDiff
	 * @return
	 */
	public <C extends Collection<I>> C getIntervals(double start, double stop, C re, double minStartDiff, double maxStartDiff, double minStopDiff, double maxStopDiff) {
		Stack<AugmentedTreeMap.Entry<I, ?>> stack = new Stack<AugmentedTreeMap.Entry<I,?>>();
		if (((AugmentedTreeMap<I,?,MutableDouble>)m).root!=null)
			stack.push(((AugmentedTreeMap<I,?,MutableDouble>)m).root);

		while (!stack.isEmpty()) {
			AugmentedTreeMap.Entry<I, ?> n = stack.pop();
			if (start<=getMaxStop(n)) {
				if (n.left!= null)
					stack.push(n.left);
				double a = n.getKey().getStart();
				double b = n.getKey().getStop();
				if (a<=stop && b>=start && 
						((Double.isInfinite(minStartDiff)&&minStartDiff<0)||a+minStartDiff<=start) && 
						((Double.isInfinite(maxStartDiff)&&maxStartDiff>0)||a+maxStartDiff>=start) && 
						((Double.isInfinite(minStopDiff)&&minStopDiff<0)||b+minStopDiff<=stop) && 
						((Double.isInfinite(maxStopDiff)&&maxStopDiff>0)||b+maxStopDiff>=stop)
					)
					re.add(n.getKey());
				if (stop>=n.getKey().getStart() && n.right != null)
					stack.push(n.right);
			}
		}
		return re;
	}
	
	
	public <C extends Collection<I>> C getIntervalsSpanning(double start, double stop, C re) {
		Stack<AugmentedTreeMap.Entry<I, ?>> stack = new Stack<AugmentedTreeMap.Entry<I,?>>();
		if (((AugmentedTreeMap<I,?,MutableDouble>)m).root!=null)
			stack.push(((AugmentedTreeMap<I,?,MutableDouble>)m).root);

		while (!stack.isEmpty()) {
			AugmentedTreeMap.Entry<I, ?> n = stack.pop();
			if (start<=getMaxStop(n)) {
				if (n.left!= null)
					stack.push(n.left);
				double a = n.getKey().getStart();
				double b = n.getKey().getStop();
				if (a<=start && b>=stop)
					re.add(n.getKey());
				if (stop>=n.getKey().getStart() && n.right != null)
					stack.push(n.right);
			}
		}
		return re;
	}

	public <C extends Collection<I>> C getIntervalsEqual(double start, double stop, C re) {
		Stack<AugmentedTreeMap.Entry<I, ?>> stack = new Stack<AugmentedTreeMap.Entry<I,?>>();
		if (((AugmentedTreeMap<I,?,MutableDouble>)m).root!=null)
			stack.push(((AugmentedTreeMap<I,?,MutableDouble>)m).root);

		while (!stack.isEmpty()) {
			AugmentedTreeMap.Entry<I, ?> n = stack.pop();
			if (start<=getMaxStop(n)) {
				if (n.left!= null)
					stack.push(n.left);
				double a = n.getKey().getStart();
				double b = n.getKey().getStop();
				if (a==start && b==stop)
					re.add(n.getKey());
				if (stop>=n.getKey().getStart() && n.right != null)
					stack.push(n.right);
			}
		}
		return re;
	}


	public <C extends Collection<I>> C getIntervalsIntersecting(double start, double stop, C re) {
		Stack<AugmentedTreeMap.Entry<I, ?>> stack = new Stack<AugmentedTreeMap.Entry<I,?>>();
		if (((AugmentedTreeMap<I,?,MutableDouble>)m).root!=null)
			stack.push(((AugmentedTreeMap<I,?,MutableDouble>)m).root);

		while (!stack.isEmpty()) {
			AugmentedTreeMap.Entry<I, ?> n = stack.pop();
			if (start<=getMaxStop(n)) {
				if (n.left!= null)
					stack.push(n.left);
				double a = n.getKey().getStart();
				double b = n.getKey().getStop();
				if (a<=stop && b>=start)
					re.add(n.getKey());
				if (stop>=n.getKey().getStart() && n.right != null)
					stack.push(n.right);
			}
		}
		return re;
	}
	

	public <C extends Collection<I>> C getIntervalsNeighbor(double start, double stop, C re) {
		Stack<AugmentedTreeMap.Entry<I, ?>> stack = new Stack<AugmentedTreeMap.Entry<I,?>>();
		if (((AugmentedTreeMap<I,?,MutableDouble>)m).root!=null)
			stack.push(((AugmentedTreeMap<I,?,MutableDouble>)m).root);

		double curr = Double.NEGATIVE_INFINITY;
		while (!stack.isEmpty()) {
			AugmentedTreeMap.Entry<I, ?> n = stack.pop();
			if (start<=getMaxStop(n)) {
				if (n.left!= null)
					stack.push(n.left);
				double a = n.getKey().getStart();
				double b = n.getKey().getStop();
				if (a>stop && a-stop<=curr) {
					if (a-stop<curr)re.clear();
					re.add(n.getKey());
					curr = a-stop;
				}
				if (b<start && start-b<=curr) {
					if (start-b<curr)re.clear();
					re.add(n.getKey());
					curr = start-b;
				}
				if (stop>=n.getKey().getStart() && n.right != null)
					stack.push(n.right);
			}
		}
		return re;
	}
	
	public <C extends Collection<I>> C getIntervalsLeftNeighbor(double start, double stop, C re) {
		Stack<AugmentedTreeMap.Entry<I, ?>> stack = new Stack<AugmentedTreeMap.Entry<I,?>>();
		if (((AugmentedTreeMap<I,?,MutableDouble>)m).root!=null)
			stack.push(((AugmentedTreeMap<I,?,MutableDouble>)m).root);

		double curr = Double.NEGATIVE_INFINITY;
		while (!stack.isEmpty()) {
			AugmentedTreeMap.Entry<I, ?> n = stack.pop();
			if (start<=getMaxStop(n)) {
				if (n.left!= null)
					stack.push(n.left);
				double b = n.getKey().getStop();
				if (b<start && b>=curr) {
					if (b>curr)re.clear();
					re.add(n.getKey());
					curr = b;
				}
				if (stop>=n.getKey().getStart() && n.right != null)
					stack.push(n.right);
			}
		}
		return re;
	}
	
	public <C extends Collection<I>> C getIntervalsRightNeighbor(double start, double stop, C re) {
		Stack<AugmentedTreeMap.Entry<I, ?>> stack = new Stack<AugmentedTreeMap.Entry<I,?>>();
		if (((AugmentedTreeMap<I,?,MutableDouble>)m).root!=null)
			stack.push(((AugmentedTreeMap<I,?,MutableDouble>)m).root);

		double curr = Double.NEGATIVE_INFINITY;
		while (!stack.isEmpty()) {
			AugmentedTreeMap.Entry<I, ?> n = stack.pop();
			if (start<=getMaxStop(n)) {
				if (n.left!= null)
					stack.push(n.left);
				double a = n.getKey().getStart();
				if (a>stop && a<=curr) {
					if (a<curr)re.clear();
					re.add(n.getKey());
					curr = a;
				}
				if (stop>=n.getKey().getStart() && n.right != null)
					stack.push(n.right);
			}
		}
		return re;
	}


	public <C extends Collection<I>> C getIntervalsSpannedBy(double start, double stop, C re) {
		Stack<AugmentedTreeMap.Entry<I, ?>> stack = new Stack<AugmentedTreeMap.Entry<I,?>>();
		if (((AugmentedTreeMap<I,?,MutableDouble>)m).root!=null)
			stack.push(((AugmentedTreeMap<I,?,MutableDouble>)m).root);

		while (!stack.isEmpty()) {
			AugmentedTreeMap.Entry<I, ?> n = stack.pop();
			if (start<=getMaxStop(n)) {
				if (n.left!= null)
					stack.push(n.left);
				double a = n.getKey().getStart();
				double b = n.getKey().getStop();
				if (start<=a && b<=stop)
					re.add(n.getKey());
				if (stop>=n.getKey().getStart() && n.right != null)
					stack.push(n.right);
			}
		}
		return re;
	}


	@Override
	public double getStart() {
		return first().getStart();
	}


	@Override
	public double getStop() {
		return getMaxStop(((AugmentedTreeMap<I,?,MutableDouble>)m).root);
	}


	private double getMaxStop(AugmentedTreeMap.Entry<I, ?> node) {
		return ((MutableDouble)node.augmentation).N;
	}

	public Iterator<Set<I>> groupIterator() {
		return groupIterator(0);
	}

	public Iterator<Set<I>> groupIterator(double tolerance) {
		return new GroupIterator(tolerance);
	}


	private class GroupIterator implements Iterator<Set<I>> {

		private Iterator<I> it;
		private I first;
		private double intervalMax;

		private NavigableSet<I> next;
		private double tolerance;

		public GroupIterator(double tolerance) {
			this.tolerance = tolerance;
			it = iterator();
			first = it.hasNext()?it.next():null;
			intervalMax = first!=null?first.getStop():-1;
		}

		@Override
		public boolean hasNext() {
			lookAhead();
			return next!=null;
		}

		@Override
		public NavigableSet<I> next() {
			lookAhead();
			NavigableSet<I> re = next;
			next = null;
			return re;
		}

		private void lookAhead() {
			if (next==null && first!=null) {
				while (it.hasNext()) {
					I current = it.next();
					if (intervalMax+tolerance<current.getStart()) {// new group
						next = subSet(first, true, current, false);
					first = current;
					intervalMax = Math.max(current.getStop(), intervalMax);
					return;
					}
					intervalMax = Math.max(current.getStop(), intervalMax);
				}
				next = tailSet(first, true);
				first = null;
			}
		}

		@Override
		public void remove() {}

	}


	public void check() {
		AugmentedTreeMap.Entry<I,?> node = ((AugmentedTreeMap<I,?,MutableDouble>)m).root;
		check(node);
	}

	private static <I extends DoubleInterval> double check(Entry<I, ?> node) {
		double c = node.getKey().getStop();
		if (node.left!=null)
			c = Math.max(c,check(node.left));
		if (node.right!=null)
			c = Math.max(c,check(node.right));
		if (c!=((MutableDouble)node.augmentation).N)
			throw new RuntimeException("Inconsistent!");
		return c;
	}


    public DoubleIntervalTree<I> clone() {
    	return new DoubleIntervalTree<I>(this);
    }
    
	private static class DoubleIntervalTreeAugmentation<I extends DoubleInterval> implements MapAugmentation<I,MutableDouble> {

		@Override
		public MutableDouble computeAugmentation(AugmentedTreeMap.Entry<I,?> n, MutableDouble currentAugmentation, MutableDouble leftAugmentation,
				MutableDouble rightAugmentation) {
			currentAugmentation.N = n.getKey().getStop();
			if (leftAugmentation!=null)
				currentAugmentation.N = Math.max(currentAugmentation.N,leftAugmentation.N);
			if (rightAugmentation!=null)
				currentAugmentation.N = Math.max(currentAugmentation.N,rightAugmentation.N);
			return currentAugmentation;
		}

		@Override
		public MutableDouble init(I key) {
			return new MutableDouble(key.getStop());
		}

	}

	public static class DoubleIntervalComparator<T extends DoubleInterval> implements Comparator<T> {
		@Override
		public int compare(T o1, T o2) {
			if (o1==o2)
				return 0;

			double re = o1.getStart()-o2.getStart();
			if (re==0)
				re = o1.getStop()-o2.getStop();
			if (re==0 && o1.hashCode()!=o2.hashCode())
				re = o1.hashCode()<o2.hashCode()?1:-1;
			if (re==0 && System.identityHashCode(o1)!=System.identityHashCode(o2))
				re = System.identityHashCode(o1)<System.identityHashCode(o2)?1:-1;
			if (re==0)
				throw new RuntimeException("Collision occured, objects are equal (but they are not the same!)?");

			return (int) Math.signum(re);
		}

	}


}
