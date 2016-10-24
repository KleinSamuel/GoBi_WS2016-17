import genomeAnnotation.Chromosome;
import genomeAnnotation.GenomeAnnotation;
import reader.GTFParser;

public class Runner {

	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		GenomeAnnotation ga = GTFParser.readGtfFile(args[0], args[1]);
		time = System.currentTimeMillis() - time;
		System.out.println("time needed: " + time / 1000 + " s");
		for (Chromosome c : ga.getChromosomes())
			System.out.println("chromosome " + c.getID() + "\t#genes: " + c.getGenes().size());
	}

}
