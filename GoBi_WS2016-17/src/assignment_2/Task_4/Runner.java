package assignment_2.Task_4;

public class Runner {

	public static void main(String[] args) {
		// bamsListFile mainDirectory mappingInfo GTF
		MultiBAMFileReader multi = new MultiBAMFileReader(args[0], args[1], args[2], args[3]);
	}

}
