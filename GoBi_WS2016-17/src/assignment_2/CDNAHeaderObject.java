package assignment_2;

public class CDNAHeaderObject {

	private String chrId, geneId, trId;
	private boolean onNegativeStrand;
	private long start, stop; // 0-based; incl
	// >ENST00000415118 cdna:known chromosome:GRCh37:14:22907539:22907546:1
	// gene:ENSG00000223997 gene_biotype:TR_D_gene transcript_biotype:TR_D_gene
	// GAAATAGT

	public CDNAHeaderObject(String headerLine) {
		String[] split = headerLine.split("\\s+");
		trId = split[0].substring(1);
		String[] startStop = split[2].split(":");
		chrId = startStop[2];
		start = Long.parseLong(startStop[3]) - 1;
		stop = Long.parseLong(startStop[4]) - 1;
		geneId = split[3].split(":")[1];
		onNegativeStrand = startStop[5].equals("-1");
	}

	public String getChrId() {
		return chrId;
	}

	public String getGeneId() {
		return geneId;
	}

	public String getTrId() {
		return trId;
	}

	public long getStart() {
		return start;
	}

	public long getStop() {
		return stop;
	}

	public boolean isOnNegativeStrand() {
		return onNegativeStrand;
	}

}
