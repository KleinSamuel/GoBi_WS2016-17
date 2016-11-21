package assignment2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

import gnu.trove.map.hash.TCharCharHashMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

public class GenomeSequenceExtractor {

	// input: 1. file with chromosomes to be analyzed -> default 1-23, X, Y, MT
	// -> contains offsets for a chromosome(tab-separated) -> default
	// calculateOffsets()
	// Bsp.: X 0 130000
	// chrId start stop --> start 0-based; stop incl.
	// 2. fastaFile

	private THashSet<String> chromosomesToBeAnalyzed;
	private THashMap<String, ChromosomeOffset> offsets;
	private RandomAccessFile ras;
	static int lineLength = 60;

	public GenomeSequenceExtractor(String offsetFilePath, String fastaFilePath) {
		chromosomesToBeAnalyzed = new THashSet<>();
		offsets = new THashMap<>();
		if (offsetFilePath == null) {
			defaultChromosomesToBeAnalyzed();
			calculateOffsets(fastaFilePath);
		} else {
			readOffsetFile(offsetFilePath);
		}
		for (ChromosomeOffset co : offsets.values()) {
			System.out.println(co.toString());
		}
		openRandomAccessFile();
	}

	private void openRandomAccessFile() {
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static TCharCharHashMap replaceForReverseComplement = new TCharCharHashMap() {
		{
			put('A', 'T');
			put('T', 'A');
			put('C', 'G');
			put('G', 'C');
			put('N', 'N');
		}
	};

	public void defaultChromosomesToBeAnalyzed() {
		for (int i = 1; i < 23; i++) {
			chromosomesToBeAnalyzed.add(String.valueOf(i));
		}
		chromosomesToBeAnalyzed.add("X");
		chromosomesToBeAnalyzed.add("Y");
		chromosomesToBeAnalyzed.add("MT");
	}

	public void calculateOffsets(String fastaFile) {
		try {
			Long time = System.currentTimeMillis();
			Process p = Runtime.getRuntime().exec("grep '>'" + fastaFile);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String s = null, chrId = null;
			String[] split = null;
			while ((s = stdInput.readLine()) != null) {
				split = s.split("\\s+");
				chrId = split[0].substring(1);
				if (!chromosomesToBeAnalyzed.contains(chrId))
					continue;
				split = split[2].split(":");
				offsets.put(chrId,
						new ChromosomeOffset(chrId, Long.parseLong(split[3]) - 1, Long.parseLong(split[4]) - 1));
			}
			time = System.currentTimeMillis() - time;
			System.out.println("calculating offsets took " + time + "ms");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// start 0-based, end incl.
	public String getSequence(String chr, int start, int end, boolean reverse_complement) {
		ChromosomeOffset co = offsets.get(chr);
		if (co == null) {
			System.out.println("Chromosome " + chr + " couldn't be found");
			return null;
		}

		return null;
	}

	public String reverseComplement(String seq) {
		StringBuilder sb = new StringBuilder();
		for (char c : seq.toCharArray()) {
			sb.append(replaceForReverseComplement.get(c));
		}
		return sb.reverse().toString();
	}

	// file has to be zero-based+last incl. <--> fasta: 1-based+last incl.
	public void readOffsetFile(String offsetFilePath) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(offsetFilePath)));
			String line = null;
			String[] split = null;
			while ((line = br.readLine()) != null) {
				split = line.split("\t");
				offsets.put(split[0],
						new ChromosomeOffset(split[0], Integer.parseInt(split[1]), Integer.parseInt(split[2])));
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
