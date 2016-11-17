package augmentedTree;


import java.util.Collection;
import java.util.Comparator;

import augmentedTree.AugmentedTreeMap.Entry;


/**
 * Items are always sorted by their score! Score must be strictly nonnegative!
 * @author erhard
 *
 * @param <C>
 */
public class CumulativeScoreTree<C> extends AugmentedTreeSet<C, TreeSizeAndScoreSum>  {

	public CumulativeScoreTree(CumulativeScoreTreeScorer<C> scorer, Comparator<? super C> comparator) {
		super(comparator,new ScoreSumTreeAugmentation<C>(scorer));
	}
	
	
	public CumulativeScoreTree(CumulativeScoreTreeScorer<C> scorer, Comparator<? super C> comparator,Collection<? extends C> coll) {
		super(comparator,new ScoreSumTreeAugmentation<C>(scorer));
		addAll(coll);
	}
	
	public CumulativeScoreTree(CumulativeScoreTreeScorer<C> scorer,boolean multi, Comparator<? super C> comparator) {
		super(multi,comparator,new ScoreSumTreeAugmentation<C>(scorer));
	}
	
	
	public CumulativeScoreTree(CumulativeScoreTreeScorer<C> scorer,boolean multi, Comparator<? super C> comparator,Collection<? extends C> coll) {
		super(multi,comparator,new ScoreSumTreeAugmentation<C>(scorer));
		addAll(coll);
	}
	
	
	public CumulativeScoreTree(CumulativeScoreTreeScorer<C> scorer) {
		super(new ScoreSumTreeAugmentation<C>(scorer));
	}
	
	
	public CumulativeScoreTree(CumulativeScoreTreeScorer<C> scorer,Collection<? extends C> coll) {
		super(new ScoreSumTreeAugmentation<C>(scorer));
		addAll(coll);
	}
	
	public CumulativeScoreTree(CumulativeScoreTreeScorer<C> scorer,boolean multi) {
		super(multi,new ScoreSumTreeAugmentation<C>(scorer));
	}
	
	
	public CumulativeScoreTree(CumulativeScoreTreeScorer<C> scorer,boolean multi,Collection<? extends C> coll) {
		super(multi,new ScoreSumTreeAugmentation<C>(scorer));
		addAll(coll);
	}
	
	public void check() {
		AugmentedTreeMap.Entry<C,?> node = ((AugmentedTreeMap<C,?,TreeSizeAndScoreSum>)m).root;
		checkScore(node);
		checkRank(node);
	}
	
	private double checkScore(Entry<C, ?> node) {
		double c = ((ScoreSumTreeAugmentation<C>)m.getAugmentation()).scorer.score(node.key);
		if (node.left!=null)
			c += checkScore(node.left);
		if (node.right!=null)
			c += checkScore(node.right);
		if (Math.abs(c-((TreeSizeAndScoreSum)node.augmentation).score)>1E-9) {
			System.err.println(toTreeString());
			throw new RuntimeException("Inconsistent at "+node);
		}
		return c;
	}
	private int checkRank(Entry<C, ?> node) {
		int c = 1;
		if (node.left!=null)
			c += checkRank(node.left);
		if (node.right!=null)
			c += checkRank(node.right);
		if (c!=((TreeSizeAndScoreSum)node.augmentation).size) {
			System.err.println(toTreeString());
			throw new RuntimeException("Inconsistent at "+node);
		}
		return c;
	}
	
	
	public double getScoreSum() {
		return ((TreeSizeAndScoreSum)((AugmentedTreeMap<C,?,TreeSizeAndScoreSum>)m).root.augmentation).score;
	}
	
	public double getCumulativeScore(C item) {
		return getCumulativeScore(((AugmentedTreeMap<C,?,TreeSizeAndScoreSum>)m).getEntry(item));
	}
	
	public double getMinCumulativeScore(C item) {
		return getCumulativeScore(((AugmentedTreeMap<C,?,TreeSizeAndScoreSum>)m).getMinEntry(item));
	}
	
	public double getMaxCumulativeScore(C item) {
		return getCumulativeScore(((AugmentedTreeMap<C,?,TreeSizeAndScoreSum>)m).getMaxEntry(item));
	}
	
	private double getCumulativeScore(AugmentedTreeMap.Entry<C,?> node) {
		if (node==null) throw new IllegalArgumentException("Not an element!");
		
		AugmentedTreeMap.Entry<C,?> root = ((AugmentedTreeMap<C,?,TreeSizeAndScoreSum>)m).root;
		double r = ((TreeSizeAndScoreSum)node.augmentation).score;
		r-=node.right==null?0:(((TreeSizeAndScoreSum)node.right.augmentation).score);
		for (AugmentedTreeMap.Entry<C,?> u=node; u!=root; u=u.parent) {
			if (u==u.parent.right) {
				r+=((TreeSizeAndScoreSum)u.parent.augmentation).score;
				r-=u.parent.right==null?0:(((TreeSizeAndScoreSum)u.parent.right.augmentation).score);
			}
		}
		
		return r;
	}
	
	/**
	 * Between 1 and size() (inclusive)
	 * @param rank
	 * @return
	 */
	public C getRanked(int rank) {
		if (rank<=0 || rank>size()) throw new IllegalArgumentException("Rank must be between 1 and size!");
		
		AugmentedTreeMap.Entry<C,?> node = ((AugmentedTreeMap<C,?,TreeSizeAndScoreSum>)m).root;
		
		int r = node.left==null?1:(((TreeSizeAndScoreSum)node.left.augmentation).size+1);
		while (r!=rank) {
			if (rank<r) node = node.left;
			else {
				node = node.right; rank-=r;
			}
			r = node.left==null?1:(((TreeSizeAndScoreSum)node.left.augmentation).size+1);
		}
		return node.key;
	}
	
	public int getRank(C item) {
		return getRank(((AugmentedTreeMap<C,?,TreeSizeAndScoreSum>)m).getEntry(item));
	}
	
	public int getMinRank(C item) {
		return getRank(((AugmentedTreeMap<C,?,TreeSizeAndScoreSum>)m).getMinEntry(item));
	}
	
	public int getMaxRank(C item) {
		return getRank(((AugmentedTreeMap<C,?,TreeSizeAndScoreSum>)m).getMaxEntry(item));
	}
	
	private int getRank(AugmentedTreeMap.Entry<C,?> node) {
		if (node==null) throw new IllegalArgumentException("Not an element!");
		
		AugmentedTreeMap.Entry<C,?> root = ((AugmentedTreeMap<C,?,TreeSizeAndScoreSum>)m).root;
		int r = node.left==null?1:(((TreeSizeAndScoreSum)node.left.augmentation).size+1);
		for (AugmentedTreeMap.Entry<C,?> u=node; u!=root; u=u.parent) {
			if (u==u.parent.right)
				r+=u.parent.left==null?1:(((TreeSizeAndScoreSum)u.parent.left.augmentation).size+1);
		}
		
		return r;
	}
	
	
	public static class IncreasingScoreComparator<C> implements Comparator<C> {

		private CumulativeScoreTreeScorer<C> scorer;
		
		public IncreasingScoreComparator(CumulativeScoreTreeScorer<C> scorer) {
			this.scorer = scorer;
		}


		@Override
		public int compare(C o1, C o2) {
			double s1 = scorer.score(o1);
			double s2 = scorer.score(o2);
			return (int) Math.signum(s1-s2);
		}
		
	}
	
	public static class DecreasingScoreComparator<C> implements Comparator<C> {

		private CumulativeScoreTreeScorer<C> scorer;
		
		public DecreasingScoreComparator(CumulativeScoreTreeScorer<C> scorer) {
			this.scorer = scorer;
		}


		@Override
		public int compare(C o1, C o2) {
			double s1 = scorer.score(o1);
			double s2 = scorer.score(o2);
			return (int) Math.signum(s2-s1);
		}
		
	}
	
	public static interface CumulativeScoreTreeScorer<C> {
		double score(C c);
	}

	private static class ScoreSumTreeAugmentation<C> implements MapAugmentation<C,TreeSizeAndScoreSum> {

		private CumulativeScoreTreeScorer<C> scorer;
		
		public ScoreSumTreeAugmentation(
				CumulativeScoreTreeScorer<C> scorer) {
			this.scorer = scorer;
		}

		@Override
		public TreeSizeAndScoreSum computeAugmentation(AugmentedTreeMap.Entry<C,?> n, TreeSizeAndScoreSum currentAugmentation, TreeSizeAndScoreSum leftAugmentation,
				TreeSizeAndScoreSum rightAugmentation) {
			currentAugmentation.score = scorer.score(n.key);
			if (leftAugmentation!=null)
				currentAugmentation.score += leftAugmentation.score;
			if (rightAugmentation!=null)
				currentAugmentation.score += rightAugmentation.score;
			
			currentAugmentation.size = 1;
			if (leftAugmentation!=null)
				currentAugmentation.size += leftAugmentation.size;
			if (rightAugmentation!=null)
				currentAugmentation.size += rightAugmentation.size;
			
			return currentAugmentation;
		}

		@Override
		public TreeSizeAndScoreSum init(C key) {
			return new TreeSizeAndScoreSum(1,scorer.score(key));
		}

	}


}
