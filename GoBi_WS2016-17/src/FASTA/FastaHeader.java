package FASTA;

public class FastaHeader {

	/*
	 * Header information of different databases
	 */
	private String[] entries_GenBank = new String[]{"gi","gi-number","gb","accession","locus"};
	private String[] entries_GEMBL = new String[]{"gi","gi-number","emb","accession","locus"};
	private String[] entries_DDJB = new String[]{"gi","gi-number","dbj","accession","locus"};
	private String[] entries_NBRF = new String[]{"pir","entry"};
	private String[] entries_SWISSPROT = new String[]{"sp","accession","name"};
	private String[] entries_TrEMBL = new String[]{"tr","accession","name"};
	private String[] entries_NCBI = new String[]{"ref","accession","locus"};
	private String[] entries_LOCAL = new String[]{"lcl","identifier"};
	
	/*
	 * Temporary header
	 * TODO: split into information
	 */
	private String temporaryHeader;
	
	/**
	 * Set header information
	 * @param tmpHeader String
	 */
	public void setTemporaryHeader(String tmpHeader){
		this.temporaryHeader = tmpHeader;
	}
	
	/**
	 * Get header information
	 * @return header String
	 */
	public String getTemporaryHeader(){
		return this.temporaryHeader;
	}
}