package assignment_3;

public class Task_1 {

	public static void main(String[] args) {
		new BAMFileReader("D:/Dennis/Uni/GoBi/TestBAMs/contextmap.bam", "D:/Dennis/Uni/GoBi/Homo_sapiens.GRCh37.75.gtf")
				.readBAMFile();
	}
}