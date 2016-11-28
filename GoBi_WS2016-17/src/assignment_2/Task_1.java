package assignment_2;

import reader.GTFParser;

public class Task_1 {

	public static void main(String[] args) {

		if (args == null || args.length < 5) {
			System.out.println("Usage: .jar referenceFileGZIP fasta offsetFile errorLogFile gtfPath");
			System.exit(1);
		}
		System.out.println("total errors: " + new SequenceExtractionComparator(args[0], args[1], args[2], args[3],
				GTFParser.readGtfFile("h.ens.75", args[4])).compareToRef());
	}

}
