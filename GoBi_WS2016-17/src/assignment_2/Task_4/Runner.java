package assignment_2.Task_4;

public class Runner {

	public static void main(String[] args) {
		MultiBAMFileReader multi = new MultiBAMFileReader(args[0], args[1], args[2], args[3]);
		// String[] bams = new String[] { "gsnap", "tophat2", "star",
		// "star2pass", "hisat", "localbt2", "contextmap",
		// "localtransbt2" };
		// for (String s : bams) {
		// BamFileReader bfr = new BamFileReader(
		// "/home/proj/biosoft/praktikum/genprakt-ws16/assignment/a2/data/bams/"
		// + s + ".bam", s, false,
		// false);
		// int counter = 0;
		// while (bfr.getNextPair() != null) {
		// counter++;
		// bfr.calcNextPair();
		// }
		// System.out.println("mapper: " + bfr.getName() + "\treadpairs: " +
		// counter);
		// }
	}

}
