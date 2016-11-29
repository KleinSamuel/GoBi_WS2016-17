package assignment_2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import debugStuff.DebugMessageFactory;
import genomeAnnotation.Exon;
import genomeAnnotation.Gene;
import genomeAnnotation.GenomeAnnotation;
import genomeAnnotation.Transcript;
import io.ConfigReader;

public class GenomeSequenceExtractor {

	// input: 1. file with chromosomes to be analyzed -> default 1-23, X, Y, MT
	// -> contains offsets for a chromosome(tab-separated) -> default
	// calculateOffsets() --> if file not there: creates offset-file on this
	// path
	// Bsp.: X 130000
	// chrId start --> start 0-based;
	// 2. fastaFile

	private HashSet<String> chromosomesToBeAnalyzed;
	private HashMap<String, Long> offsets;
	private RandomAccessFile raf;
	private FileInputStream fis;
	private InputStreamReader isr;
	private BufferedReader br;
	static int lineLength = 60, newLineChars = 1;
	public GenomeAnnotation ga;

	public GenomeSequenceExtractor(String offsetFilePath, String fastaFilePath, GenomeAnnotation ga) {
		chromosomesToBeAnalyzed = new HashSet<>();
		offsets = new HashMap<>();
		if (offsetFilePath == null || !new File(offsetFilePath).exists()) {
			defaultChromosomesToBeAnalyzed();
			calculateOffsets(offsetFilePath, fastaFilePath);
		} else {
			readOffsetFile(offsetFilePath);
		}
//		for (Entry<String, Long> offset : offsets.entrySet()) {
//			DebugMessageFactory.printInfoDebugMessage(ConfigReader.DEBUG_MODE, offset.getKey() + "\t" + offset.getValue());
//		}
		openRandomAccessFile(fastaFilePath);
		this.ga = ga;
	}

	private void openRandomAccessFile(String fasta) {
		try {
			raf = new RandomAccessFile(fasta, "r");
			fis = new FileInputStream(raf.getFD());
			isr = new InputStreamReader(fis);
			br = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static HashMap<Character, Character> replaceForReverseComplement = new HashMap<Character, Character>() {
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

	public void calculateOffsets(String offsetFilePath, String fastaFile) {
		try {
			DebugMessageFactory.printInfoDebugMessage(ConfigReader.DEBUG_MODE, "started calculating offsets from " + fastaFile);
			Long time = System.currentTimeMillis();
			ProcessBuilder p = new ProcessBuilder(new String[] { "bash", "-c", "grep -b -A1 '^>' " + fastaFile });
			p.redirectErrorStream(true);
			Process proc = p.start();
			proc.waitFor();
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(offsetFilePath)));
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String s = null, chrId = null;
			String[] split = null;
			while ((s = stdInput.readLine()) != null) {
				split = s.split("\\s+");
				chrId = split[0].split(">")[1];
				if (!chromosomesToBeAnalyzed.contains(chrId)) {
					stdInput.readLine();
					stdInput.readLine();
					continue;
				}
				s = stdInput.readLine();
				Long offset = Long.parseLong(s.split("-")[0]);
				offsets.put(chrId, offset);
				bw.write(chrId + "\t" + offset + "\n");
				stdInput.readLine();
			}
			bw.close();
			stdInput.close();
			proc.destroy();
			time = System.currentTimeMillis() - time;
			DebugMessageFactory.printInfoDebugMessage(ConfigReader.DEBUG_MODE, "calculating offsets took " + time + "ms");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// start 0-based, end incl.
	public String getSequence(String chr, int start, int end, boolean reverse_complement) {
		Long offset = offsets.get(chr);
		if (offset == null) {
			DebugMessageFactory.printErrorDebugMessage(ConfigReader.DEBUG_MODE, "Chromosome " + chr + " couldn't be found");
			return null;
		}
		// *lineNumber is relative to chromosome
		// "Brauchst du eigentlich code von mir..."
		// "Stuhlproben solltest du lieber deinem Arzt geben und nicht mir"
		long startLineNumber = start / lineLength, endLineNumber = end / lineLength;
		long startPosition = offset + start + startLineNumber * newLineChars; // 0-based
		long endPosition = offset + end + endLineNumber * newLineChars; // incl
		char[] cbuf = null, cbuffered = null;
		try {
			seek(startPosition);
			cbuf = new char[(int) (endPosition - startPosition) + 1];
			br.read(cbuf, 0, cbuf.length);
			cbuffered = new char[end - start + 1];
			int runningPointer = 0;
			for (char c : cbuf) {
				if (c != '\n' && c != '\r') {
					cbuffered[runningPointer++] = c;
				}
			}
			if (reverse_complement)
				return reverseComplement(new String(cbuffered));
			return new String(cbuffered);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	public String getCDNASequenceInInterval(String chrId, String geneId, String trId, int start, int end,
			boolean negativeStrand) {
		if (!chromosomesToBeAnalyzed.contains(chrId)) {
			DebugMessageFactory.printErrorDebugMessage(ConfigReader.DEBUG_MODE, "getCDNA couldn't find chrId " + chrId);
			return null;
		}
		Transcript t = ga.getChromosome(chrId).getGene(geneId).getTranscript(trId);
		if (t == null) {
			DebugMessageFactory.printErrorDebugMessage(ConfigReader.DEBUG_MODE, "couldn't find tr " + trId + " in g " + geneId + " in chr " + chrId);
			return null;
		}
		StringBuilder cdna = new StringBuilder();

		for (Exon e : t.getExons()) {
			cdna.append(getSequence(chrId, e.getStart(), e.getStop(), false));
		}
		if (negativeStrand)
			return reverseComplement(cdna.toString());
		return cdna.toString();
	}

	public String getTranscriptSequence(String geneId, String trId) {
		Gene g = ga.getGene(geneId);
		Transcript t = g.getTranscript(trId);
		return getCDNASequenceInInterval(g.getChromosome().getID(), geneId, trId, t.getStart(), t.getStop(),
				t.isOnNegativeStrand());
	}

	// set raf-pointer to new position --> new Buffer needed
	public void seek(long position) {
		try {
			raf.seek(position);
			fis = new FileInputStream(raf.getFD());
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isChromosomeToBeAnalyzed(String chrId) {
		return chromosomesToBeAnalyzed.contains(chrId);
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
				offsets.put(split[0], Long.parseLong(split[1]));
				chromosomesToBeAnalyzed.add(split[0]);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
