package reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

import debugStuff.DebugMessageFactory;
import genomeAnnotation.CDS;
import genomeAnnotation.CDSPart;
import genomeAnnotation.Chromosome;
import genomeAnnotation.Exon;
import genomeAnnotation.Gene;
import genomeAnnotation.GenomeAnnotation;
import genomeAnnotation.Transcript;
import io.ConfigReader;

public class GTFParser {

	static int ExonNumber = 0;

	public static GenomeAnnotation readGtfFile(String name, String gtfFilePath) {
		GenomeAnnotation ga = new GenomeAnnotation(name);

		DebugMessageFactory.printInfoDebugMessage(ConfigReader.DEBUG_MODE, "Started reading gtf-file: " + ga.getName());

		Chromosome c = null;
		Gene g = null;
		Transcript t = null;
		Exon e = null;
		boolean onNegativeStrand = false;

		String line = null, exonId = null, tempBiotype = null;
		String[] split = null, tempAttrArr = null;
		// Pattern p1 = Pattern.compile("\"; | \"");
		Pattern p2 = Pattern.compile("\t");
		HashSet<String> annotatedRegions = new HashSet<>();
		annotatedRegions.add("CDS");
		annotatedRegions.add("gene");
		annotatedRegions.add("transcript");
		annotatedRegions.add("exon");
		HashMap<String, String> tempAttributes = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(gtfFilePath)));
			while ((line = br.readLine()) != null) {

				if (line.startsWith("#"))
					continue;

				// split = p2.split(line);
				split = line.split("\t");
				if (!annotatedRegions.contains(split[2]))
					continue;

				if (c == null) {
					c = new Chromosome(split[0]);
				} else if (!split[0].equals(c.getID())) {
					ga.addChromosome(c);
					c = new Chromosome(split[0]);
				}
				tempAttributes = new HashMap<>();
				split[8] = split[8].replace("\"", "").replace(";", "");
				// tempAttrArr = p1.split(split[8].substring(0,
				// split[8].length() - 2));
				tempAttrArr = split[8].split(" ");
				for (int i = 0; i < tempAttrArr.length; i++) {
					if (i + 1 < tempAttrArr.length)
						tempAttributes.put(tempAttrArr[i], tempAttrArr[++i]);
				}

				onNegativeStrand = split[6].equals("-");

				int start = Integer.parseInt(split[3]) - 1, stop = Integer.parseInt(split[4]) - 1;
				if (start > stop) {
					int tmpStart = start;
					start = stop;
					stop = tmpStart;
				}

				switch (split[2]) {
				case "gene":
					tempBiotype = tempAttributes.get("gene_biotype");

					if (tempBiotype == null) {
						tempBiotype = tempAttributes.get("gene_type");
					}
					g = new Gene(start, stop, tempAttributes.get("gene_id"), onNegativeStrand, tempBiotype,
							tempAttributes.get("gene_name"), c);
					c.addGene(g);
					break;
				case "transcript":
					t = new Transcript(start, stop, tempAttributes.get("transcript_id"), onNegativeStrand, g);
					g.addTranscript(t);
					ga.addTranscript(t);
					break;
				case "exon":
					exonId = tempAttributes.get("exon_id");
					if (exonId == null)
						exonId = generateNextExonId();
					e = g.getExon(exonId);
					if (e == null) {
						e = new Exon(start, stop, exonId, onNegativeStrand);
						g.addExon(e);
					}
					t.addExon(e);
					break;
				case "CDS":
					if (!t.hasCDS())
						t.createCDS(new CDS(-1, -1, tempAttributes.get("ccds_id"), tempAttributes.get("protein_id"),
								onNegativeStrand));
					t.addCDSPart(new CDSPart(start, stop));
					break;
				}
			}
			if (c != null)
				ga.addChromosome(c);
			br.close();
		} catch (Exception exc) {
			exc.printStackTrace();
			System.exit(1);
		}
		return ga;
	}

	public static String generateNextExonId() {
		String exonNumber = String.valueOf(ExonNumber++);
		return "ENSE_" + "000000000".substring(exonNumber.length()) + exonNumber;
	}

}