package util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

public class TopTen<T> {

	private HashMap<T, Integer> allCounts;

	public TopTen(HashMap<T, Integer> counts) {
		allCounts = counts;
	}

	public LinkedList<T> getTopTen() {
		TreeSet<T> sorted = new TreeSet<>(new Comparator<T>() {

			@Override
			public int compare(T o1, T o2) {
				// sort descending
				return allCounts.get(o2).compareTo(allCounts.get(o1));
			}

		});

		sorted.addAll(allCounts.keySet());

		LinkedList<T> topTen = new LinkedList<>();

		if (sorted.size() < 10)
			topTen.addAll(sorted);
		else {
			while (topTen.size() < 10) {
				topTen.add(sorted.pollFirst());
			}
		}

		return topTen;

	}

}
