package reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

import genomeAnnotation.CDS;
import genomeAnnotation.Chromosome;
import genomeAnnotation.Exon;
import genomeAnnotation.Gene;
import genomeAnnotation.GenomeAnnotation;
import genomeAnnotation.Transcript;

public class GTFParser {

	static int ExonNumber = 0;

	public static GenomeAnnotation readGtfFile(String name, String gtfFilePath) {
		GenomeAnnotation ga = new GenomeAnnotation(name);

		Chromosome c = null;
		Gene g = null;
		Transcript t = null;
		Exon e = null;
		CDS cds = null;
		boolean onNegativeStrand = false;

		String line = null, exonId = null;
		String[] split = null, tempAttrArr = null;
		Pattern p1 = Pattern.compile("\"; | \"");
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

				split = p2.split(line);

				if (!annotatedRegions.contains(split[2]))
					continue;

				if (c == null) {
					c = new Chromosome(split[0]);
				} else if (!split[0].equals(c.getID())) {
					ga.addChromosome(c);
					c = new Chromosome(split[0]);
				}
				tempAttributes = new HashMap<>();
				tempAttrArr = p1.split(split[8].substring(0, split[8].length() - 2));
				for (int i = 0; i < tempAttrArr.length; i++) {
					tempAttributes.put(tempAttrArr[i], tempAttrArr[++i]);
				}

				onNegativeStrand = split[6].equals("-");

				switch (split[2]) {
				case "gene":
					g = new Gene(Integer.parseInt(split[3]) - 1, Integer.parseInt(split[4]) - 1,
							tempAttributes.get("gene_id"), onNegativeStrand, tempAttributes.get("biotype"),
							tempAttributes.get("gene_name"));
					c.addGene(g);
					break;
				case "transcript":
					t = new Transcript(Integer.parseInt(split[3]) - 1, Integer.parseInt(split[4]) - 1,
							tempAttributes.get("transcript_id"), onNegativeStrand, g);
					g.addTranscript(t);
					break;
				case "exon":
					exonId = tempAttributes.get("exon_id");
					if (exonId == null)
						exonId = generateNextExonId();
					e = new Exon(Integer.parseInt(split[3]) - 1, Integer.parseInt(split[4]) - 1, exonId,
							onNegativeStrand);
					g.addExon(e);
					t.addExon(e);
					break;
				case "CDS":
					cds = new CDS(Integer.parseInt(split[3]) - 1, Integer.parseInt(split[4]) - 1,
							tempAttributes.get("protein_id"), onNegativeStrand);
					t.addCDS(cds);
					break;
				}
			}
			if (c != null)
				ga.addChromosome(c);
			br.close();
		} catch (

		Exception exc) {
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
