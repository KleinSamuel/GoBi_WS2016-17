package reader;

import java.util.ArrayList;

import genomeAnnotation.GenomeAnnotation;

public class ThreadedReader implements Runnable {

	private GenomeAnnotation genomeAnnotation;
	private String threadName;
	private String FILEPATH;
	private ArrayList<String> lines;
	private int idCounter = 0;

	// public GTF_Parser(String name, ArrayList<String> lines, GenomeAnnotation
	// genomeAnnotation){
	// this.threadName = name;
	// this.lines = lines;
	// this.genomeAnnotation = genomeAnnotation;
	// }
	//
	// private void readFile(String filePath) {
	//
	// Chromosome currentChromosome = null;
	// Gene currentGene = null;
	// Transcript currentTranscript = null;
	// Exon currentExon = null;
	// CDSPart currentCDSPart = null;
	//
	// String[] tempArray;
	//
	// String line = null;
	//
	// String seqname, source, feature, start, end, score, strand, frame,
	// attribute;
	//
	// /* storing values for current IDs */
	// String tempChrID, tempGeneID, tempTranscriptID, tempExonID, tempCDSID,
	// tempBiotype;
	//
	// HashMap<String, String> tempMap;
	// String[] tempAttrArr;
	// Pattern p1 = Pattern.compile("\"; | \"");
	// Pattern p2 = Pattern.compile("\t");
	//
	// for (String s : lines) {
	//
	// line = s;
	//
	// if (line.startsWith("#")) {
	// continue;
	// }
	//
	// tempArray = line.split("\t");
	//
	// seqname = tempArray[0];
	// source = tempArray[1];
	// feature = tempArray[2];
	// start = tempArray[3];
	// end = tempArray[4];
	// score = tempArray[5];
	// strand = tempArray[6];
	// frame = tempArray[7];
	// attribute = tempArray[8];
	//
	// tempMap = new HashMap<String, String>();
	//
	// tempAttrArr = p1.split(attribute.substring(0, attribute.length() - 2));
	// for (int i = 0; i < tempAttrArr.length; i++) {
	// tempMap.put(tempAttrArr[i], tempAttrArr[++i]);
	// }
	//
	// boolean createdNewObject = false;
	// tempChrID = seqname;
	// currentChromosome = genomeAnnotation.getChromosome(tempChrID);
	// if (currentChromosome == null) {
	// currentChromosome = new Chromosome(tempChrID);
	// genomeAnnotation.addChromosome(currentChromosome);
	// createdNewObject = true;
	// }
	// tempGeneID = tempMap.get("gene_id");
	// currentGene = genomeAnnotation.getGene(tempGeneID);
	// if (createdNewObject || currentGene == null) {
	// currentGene = createDummyGene(tempGeneID, currentChromosome);
	// }
	//
	// switch (feature) {
	//
	// case "gene":
	//
	// tempBiotype = tempMap.get("gene_biotype");
	//
	// if (tempBiotype == null) {
	// tempBiotype = tempMap.get("gene_type");
	// }
	//
	// // fill dummy data or new created gene
	// currentGene.setStart(Integer.parseInt(start));
	// currentGene.setStop(Integer.parseInt(end));
	// currentGene.setOnNegativeStrand(strand.equals("-"));
	// currentGene.setBiotype(tempBiotype);
	//
	// break;
	// case "transcript":
	//
	// tempTranscriptID = tempMap.get("transcript_id");
	//
	// currentTranscript = currentGene.getTranscript(tempTranscriptID);
	//
	// // transcript didn't exist yet
	// if (createdNewObject || currentTranscript == null) {
	// currentTranscript = new Transcript(Integer.parseInt(start),
	// Integer.parseInt(end), tempTranscriptID,
	// strand.equals("-"), currentGene);
	// }
	// // dummy existed before
	// else {
	// currentTranscript.setStart(Integer.parseInt(start));
	// currentTranscript.setStop(Integer.parseInt(end));
	// currentTranscript.setOnNegativeStrand(strand.equals("-"));
	// }
	// break;
	//
	// case "exon":
	//
	// tempTranscriptID = tempMap.get("transcript_id");
	//
	// currentTranscript = currentGene.getTranscript(tempTranscriptID);
	//
	// // transcript didn't exist yet
	// if (createdNewObject || currentTranscript == null) {
	// currentTranscript = createDummyTranscript(tempTranscriptID, currentGene);
	// }
	//
	// tempExonID = tempMap.get("exon_id");
	//
	// if (tempExonID == null) {
	// tempExonID = createNewUniqueExonId();
	// }
	//
	// currentExon = new Exon(Integer.parseInt(start), Integer.parseInt(end),
	// tempExonID, strand.equals("-"));
	// currentTranscript.addExon(currentExon);
	//
	// break;
	//
	// case "CDS":
	//
	// tempTranscriptID = tempMap.get("transcript_id");
	//
	// currentTranscript = currentGene.getTranscript(tempTranscriptID);
	//
	// // transcript didn't exist yet
	// if (createdNewObject || currentTranscript == null) {
	// currentTranscript = createDummyTranscript(tempTranscriptID, currentGene);
	// }
	//
	// tempCDSID = tempMap.get("protein_id");
	// currentCDSPart = new CDSPart(Integer.parseInt(start),
	// Integer.parseInt(end), tempCDSID,
	// strand.equals("-"));
	//
	// break;
	// }
	//
	// }
	//
	// }
	//
	// private Transcript createDummyTranscript(String transID, Gene
	// currentGene) {
	// Transcript retTr = new Transcript(-1, -1, transID, false, currentGene);
	// currentGene.addTranscript(retTr);
	// return retTr;
	// }
	//
	// private Gene createDummyGene(String geneID, Chromosome currentChromosome)
	// {
	// Gene retGene = new Gene(-1, -1, geneID, false, null, null,
	// currentChromosome);
	// currentChromosome.addGene(retGene);
	// return retGene;
	// }
	//
	// public GenomeAnnotation getGenomeAnnotation() {
	// return genomeAnnotation;
	// }
	//
	// // schwachsinn! return map.get(key) macht dasselbe, da brauchts die
	// methode
	// // nicht
	// private String getValueFromAttribute(String key, HashMap<String, String>
	// map) {
	// String id = null;
	// if (map.containsKey(key)) {
	// id = map.get(key);
	// }
	// return id;
	// }
	//
	// private String createNewUniqueExonId() {
	// String idFormatted = String.valueOf(idCounter++);
	// return "ENSE_" + "000000000".substring(idFormatted.length()) +
	// idFormatted + "_" + threadName;
	//
	// }
	//
	// public String toString() {
	//
	// for (Chromosome c : genomeAnnotation.getChromosomes().values()) {
	// System.out.println("[chr] " + c.getID());
	// System.out.println("|");
	//
	// for (Gene g : c.getAllGenesSorted()) {
	// System.out.println("|__[gene] " + g.getId());
	// System.out.println("| |");
	//
	// for (Transcript tr : g.getAllTranscriptsSorted()) {
	// System.out.println("| |__[trans] " + tr.getId());
	// System.out.println("| |");
	//
	// for (Exon e : tr.getExons()) {
	// System.out.println("| |__[exon] " + e.getId());
	// }
	//
	// if (tr.get() != null) {
	// for (CDSPart entryCDS : entryTrans.getValue().getCds().getParts()) {
	// System.out.println("| |__[cds] " + entryCDS.getId());
	// }
	// }
	// }
	// }
	// }
	// return "";
	// }
	//
	@Override
	public void run() {

		System.out.println("Thread #" + this.threadName + " started.");

		// this.readFile(this.FILEPATH);

		System.out.println("Thread #" + this.threadName + " finished.");
	}

}