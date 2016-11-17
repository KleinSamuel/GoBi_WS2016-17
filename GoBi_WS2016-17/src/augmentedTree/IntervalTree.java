package augmentedTree;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import augmentedTree.AugmentedTreeMap.Entry;


/**
 * Implementation of IntervalTrees (see Cormen) based on the Red-Black-Tree implementation of {@link TreeMap}s.
 * This class implements a fully functional map interface, i.e. intervals can be added using {@link #add(Object)}
 * or {@link #addAll(Collection)} and removed using either {@link #remove(Object)},{@link #removeAll(Collection)} 
 * or the remove method of any iterator.
 * 
 * The benefit in comparison to a {@link TreeMap} is the addition of several methods regarding interval operation
 * (all methods with getIntervals...). For full flexibility, these methods do not return a fixed implementation of 
 * {@link List} or an array, but any List object must be given as third parameter.
 * 
 * A frequent usage thus is:
 * ArrayList<MyInterval> intersecting = tree.getIntervalsIntersecting(s,e,new ArrayList<MyInterval>());
 * 
 * @author erhard
 *
 * @param <I>
 */
public class IntervalTree<I extends Interval> extends AugmentedTreeSet<I, MutableInteger> implements Interval {

	
	public IntervalTree() {
		super(new IntervalComparator<I>(), new IntervalTreeAugmentation<I>());
	}


	public IntervalTree(Collection<? extends I> coll) {
		super(new IntervalComparator<I>(), new IntervalTreeAugmentation<I>());
		addAll(coll);
	}


	public <C extends Collection<I>> C getIntervalsSpanning(int p, C re) {
		Stack<AugmentedTreeMap.Entry<I, ?>> stack = new Stack<AugmentedTreeMap.Entry<I,?>>();
		if (((AugmentedTreeMap<I,?,MutableInteger>)m).root!=null)
			stack.push(((AugmentedTreeMap<I,?,MutableInteger>)m).root);

		while (!stack.isEmpty()) {
			AugmentedTreeMap.Entry<I, ?> n = stack.pop();
			if (p<=getMaxStop(n)) {
				if (n.left!= null)
					stack.push(n.left);
				int a = n.getKey().getStart();
				int b = n.getKey().getStop();
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
	 * Integer.MIN_VALUE and Integer.MAX_VALUE are considered as -Inf and Inf
	 * @param <C>
	 * @param start
	 * @param stop
	 * @param re
	 * @param startDiff
	 * @param stopDiff
	 * @return
	 */
	public <C extends Collection<I>> C getIntervals(int start, int stop, C re, int minStartDiff, int maxStartDiff, int minStopDiff, int maxStopDiff) {
		Stack<AugmentedTreeMap.Entry<I, ?>> stack = new Stack<AugmentedTreeMap.Entry<I,?>>();
		if (((AugmentedTreeMap<I,?,MutableInteger>)m).root!=null)
			stack.push(((AugmentedTreeMap<I,?,MutableInteger>)m).root);

		while (!stack.isEmpty()) {
			AugmentedTreeMap.Entry<I, ?> n = stack.pop();
			if (start<=getMaxStop(n)) {
				if (n.left!= null)
					stack.push(n.left);
				int a = n.getKey().getStart();
				int b = n.getKey().getStop();
				if (a<=stop && b>=start && 
						(minStartDiff==Integer.MIN_VALUE||a+minStartDiff<=start) && 
						(maxStartDiff==Integer.MAX_VALUE||a+maxStartDiff>=start) && 
						(minStopDiff==Integer.MIN_VALUE||b+minStopDiff<=stop) && 
						(maxStopDiff==Integer.MAX_VALUE||b+maxStopDiff>=stop)
					)
					re.add(n.getKey());
				if (stop>=n.getKey().getStart() && n.right != null)
					stack.push(n.right);
			}
		}
		return re;
	}
	
	
	public <C extends Collection<I>> C getIntervalsSpanning(int start, int stop, C re) {
		Stack<AugmentedTreeMap.Entry<I, ?>> stack = new Stack<AugmentedTreeMap.Entry<I,?>>();
		if (((AugmentedTreeMap<I,?,MutableInteger>)m).root!=null)
			stack.push(((AugmentedTreeMap<I,?,MutableInteger>)m).root);

		while (!stack.isEmpty()) {
			AugmentedTreeMap.Entry<I, ?> n = stack.pop();
			if (start<=getMaxStop(n)) {
				if (n.left!= null)
					stack.push(n.left);
				int a = n.getKey().getStart();
				int b = n.getKey().getStop();
				if (a<=start && b>=stop)
					re.add(n.getKey());
				if (stop>=n.getKey().getStart() && n.right != null)
					stack.push(n.right);
			}
		}
		return re;
	}

	public <C extends Collection<I>> C getIntervalsEqual(int start, int stop, C re) {
		Stack<AugmentedTreeMap.Entry<I, ?>> stack = new Stack<AugmentedTreeMap.Entry<I,?>>();
		if (((AugmentedTreeMap<I,?,MutableInteger>)m).root!=null)
			stack.push(((AugmentedTreeMap<I,?,MutableInteger>)m).root);

		while (!stack.isEmpty()) {
			AugmentedTreeMap.Entry<I, ?> n = stack.pop();
			if (start<=getMaxStop(n)) {
				if (n.left!= null)
					stack.push(n.left);
				int a = n.getKey().getStart();
				int b = n.getKey().getStop();
				if (a==start && b==stop)
					re.add(n.getKey());
				if (stop>=n.getKey().getStart() && n.right != null)
					stack.push(n.right);
			}
		}
		return re;
	}


	public <C extends Collection<I>> C getIntervalsIntersecting(int start, int stop, C re) {
		Stack<AugmentedTreeMap.Entry<I, ?>> stack = new Stack<AugmentedTreeMap.Entry<I,?>>();
		if (((AugmentedTreeMap<I,?,MutableInteger>)m).root!=null)
			stack.push(((AugmentedTreeMap<I,?,MutableInteger>)m).root);

		while (!stack.isEmpty()) {
			AugmentedTreeMap.Entry<I, ?> n = stack.pop();
			if (start<=getMaxStop(n)) {
				if (n.left!= null)
					stack.push(n.left);
				int a = n.getKey().getStart();
				int b = n.getKey().getStop();
				if (a<=stop && b>=start)
					re.add(n.getKey());
				if (stop>=n.getKey().getStart() && n.right != null)
					stack.push(n.right);
			}
		}
		return re;
	}
	

	public <C extends Collection<I>> C getIntervalsNeighbor(int start, int stop, C re) {
		int size = re.size();
		getIntervalsIntersecting(start, stop, re);
		if (size<re.size()) return re;

		getIntervalsLeftNeighbor(start, stop, re);
		if (re.size()==0) return getIntervalsRightNeighbor(start, stop, re);
		
		ArrayList<I> buff = getIntervalsRightNeighbor(start, stop, new ArrayList<I>());
		if (buff.size()==0) return re;
		
		int ldist = start-((Interval)re.iterator().next()).getStop();
		int rdist = buff.get(0).getStart()-stop;
		if (ldist<rdist) return re;
		if (rdist<ldist) re.clear();
		re.addAll(buff);
		return re;
	}
	
	/**
	 * Gets the rightmost intervals (w.r.t. theirs stops) that are either left to start or all intersecting
	 * @param start
	 * @param stop
	 * @param re
	 * @return
	 */
	public <C extends Collection<I>> C getIntervalsLeftNeighbor(int start, int stop, C re) {
		int size = re.size();
		getIntervalsIntersecting(start, stop, re);
		if (size<re.size()) return re;



		Stack<AugmentedTreeMap.Entry<I, ?>> stack = new Stack<AugmentedTreeMap.Entry<I,?>>();
		if (((AugmentedTreeMap<I,?,MutableInteger>)m).root!=null)
			stack.push(((AugmentedTreeMap<I,?,MutableInteger>)m).root);

		int curr = Integer.MIN_VALUE;
		HashSet<AugmentedTreeMap.Entry<I, ?>> cand = new HashSet<AugmentedTreeMap.Entry<I,?>>();
		while (!stack.isEmpty()) {
			AugmentedTreeMap.Entry<I, ?> n = stack.pop();
			int b = n.key.getStop();
			if (b<start) {
				if (b>curr) cand.clear();
				if (b>=curr) {
					curr = b;
					cand.add(n);
				}
			}

			if (start<=getMaxStop(n)) {
				if (n.left!= null)
					stack.push(n.left);
				if (start>=n.getKey().getStart() && n.right != null)
					stack.push(n.right);
			} else {
				b = getMaxStop(n);
				if (b>curr) cand.clear();
				if (b>=curr) {
					curr = b;
					cand.add(n);
				}
			}
		}

		if (curr==Integer.MIN_VALUE) return re; // no interval left of start
		stack.addAll(cand);

		while (!stack.isEmpty()) {
			AugmentedTreeMap.Entry<I, ?> n = stack.pop();
			if (n.getKey().getStop()==curr)
				re.add(n.getKey());
			if (n.left!=null && getMaxStop(n.left)==curr)
				stack.push(n.left);
			if (n.right!=null && getMaxStop(n.right)==curr)
				stack.push(n.right);
		}
		return re;
	}

	/**
	 * Gets the leftmost intervals (w.r.t. their starts), that are either right to stop or all intersecting
	 * @param start
	 * @param stop
	 * @param re
	 * @return
	 */
	public <C extends Collection<I>> C getIntervalsRightNeighbor(int start, int stop, C re) {
		int size = re.size();
		getIntervalsIntersecting(start, stop, re);
		if (size<re.size()) return re;

		Stack<AugmentedTreeMap.Entry<I, ?>> stack = new Stack<AugmentedTreeMap.Entry<I,?>>();
		if (((AugmentedTreeMap<I,?,MutableInteger>)m).root!=null)
			stack.push(((AugmentedTreeMap<I,?,MutableInteger>)m).root);

		int curr = Integer.MAX_VALUE;
		AugmentedTreeMap.Entry<I, ?> best = null;
		while (!stack.isEmpty()) {
			AugmentedTreeMap.Entry<I, ?> n = stack.pop();
			int a = n.getKey().getStart();
			if (a>stop &&a<curr) {
				curr=a;
				best = n;
			}

			if (stop<a) {
				if (n.left!=null)
					stack.push(n.left);
			}
			else{
				if (n.right!=null)
					stack.push(n.right);
			}
		}
		if (best==null) return re; // no hit

		stack.push(best);
		while (!stack.isEmpty()) {
			AugmentedTreeMap.Entry<I, ?> n = stack.pop();
			int a = n.getKey().getStart();
			if (a==curr) {
				re.add(n.key);
				if (n.left!=null)
					stack.push(n.left);
				if (n.right!=null)
					stack.push(n.right);
			}
		}

		return re;
	}


	public <C extends Collection<I>> C getIntervalsSpannedBy(int start, int stop, C re) {
		Stack<AugmentedTreeMap.Entry<I, ?>> stack = new Stack<AugmentedTreeMap.Entry<I,?>>();
		if (((AugmentedTreeMap<I,?,MutableInteger>)m).root!=null)
			stack.push(((AugmentedTreeMap<I,?,MutableInteger>)m).root);

		while (!stack.isEmpty()) {
			AugmentedTreeMap.Entry<I, ?> n = stack.pop();
			if (start<=getMaxStop(n)) {
				if (n.left!= null)
					stack.push(n.left);
				int a = n.getKey().getStart();
				int b = n.getKey().getStop();
				if (start<=a && b<=stop)
					re.add(n.getKey());
				if (stop>=n.getKey().getStart() && n.right != null)
					stack.push(n.right);
			}
		}
		return re;
	}


	@Override
	public int getStart() {
		return first().getStart();
	}


	@Override
	public int getStop() {
		return getMaxStop(((AugmentedTreeMap<I,?,MutableInteger>)m).root);
	}


	private int getMaxStop(AugmentedTreeMap.Entry<I, ?> node) {
		return ((MutableInteger)node.augmentation).N;
	}

	/**
	 * Iterates over overlapping groups of intervals
	 * @return
	 */
	public Iterator<Set<I>> groupIterator() {
		return groupIterator(0);
	}

	/**
	 * Iterates over overlapping groups of intervals; overlap with a tolerance means that two intervals overlap as long as their distance is smaller or equal to the given distance
	 * @return
	 */
	public Iterator<Set<I>> groupIterator(int tolerance) {
		return new GroupIterator(tolerance);
	}

	private class GroupIterator implements Iterator<Set<I>> {

		private Iterator<I> it;
		private I first;
		private int intervalMax;

		private NavigableSet<I> next;
		private int tolerance;

		public GroupIterator(int tolerance) {
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
		AugmentedTreeMap.Entry<I,?> node = ((AugmentedTreeMap<I,?,MutableInteger>)m).root;
		check(node);
	}

	private static <I extends Interval> int check(Entry<I, ?> node) {
		int c = node.getKey().getStop();
		if (node.left!=null)
			c = Math.max(c,check(node.left));
		if (node.right!=null)
			c = Math.max(c,check(node.right));
		if (c!=((MutableInteger)node.augmentation).N)
			throw new RuntimeException("Inconsistent!");
		return c;
	}


    public IntervalTree<I> clone() {
    	return new IntervalTree<I>(this);
    }
    
	private static class IntervalTreeAugmentation<I extends Interval> implements MapAugmentation<I,MutableInteger> {

		@Override
		public MutableInteger computeAugmentation(AugmentedTreeMap.Entry<I,?> n, MutableInteger currentAugmentation, MutableInteger leftAugmentation,
				MutableInteger rightAugmentation) {
			currentAugmentation.N = n.getKey().getStop();
			if (leftAugmentation!=null)
				currentAugmentation.N = Math.max(currentAugmentation.N,leftAugmentation.N);
			if (rightAugmentation!=null)
				currentAugmentation.N = Math.max(currentAugmentation.N,rightAugmentation.N);
			return currentAugmentation;
		}

		@Override
		public MutableInteger init(I key) {
			return new MutableInteger(key.getStop());
		}

	}

	public static class IntervalComparator<T extends Interval> implements Comparator<T> {
		@Override
		public int compare(T o1, T o2) {
			if (o1==o2)
				return 0;

			int re = o1.getStart()-o2.getStart();
			if (re==0)
				re = o1.getStop()-o2.getStop();
			if (re==0 && o1.hashCode()!=o2.hashCode())
				re = o1.hashCode()<o2.hashCode()?1:-1;
			if (re==0 && System.identityHashCode(o1)!=System.identityHashCode(o2))
				re = System.identityHashCode(o1)<System.identityHashCode(o2)?1:-1;
			if (re==0)
				throw new RuntimeException("Collision occured, objects are equal (but they are not the same!)?");

			return re;
		}

	}


}
