package fileFormats;

/**
 * Element for FastQ files.
 * 
 * @author Samuel Klein
 */
public class FASTQElement {

	private String header;
	private String sequence;
	private String qualityScores;
	
	public FASTQElement(String header, String sequence, String scores){
		this.header = header;
		this.sequence = sequence;
		this.qualityScores = scores;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public String getQualityScores() {
		return qualityScores;
	}

	public void setQualityScores(String qualityScores) {
		this.qualityScores = qualityScores;
	}
	
}
