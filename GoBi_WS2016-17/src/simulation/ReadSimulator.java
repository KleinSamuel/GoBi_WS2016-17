package simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.distribution.NormalDistribution;

import assignment_2.GenomeSequenceExtractor;
import debugStuff.DebugMessageFactory;
import fileFormats.FASTQElement;
import fileFormats.FASTQQualityScores;
import genomeAnnotation.GenomeAnnotation;
import html.HTML_File;
import io.AllroundFileReader;
import io.ConfigHelper;
import io.ConfigReader;
import io.ExternalFileWriter;
import javafx.util.Pair;
import plotting.LinePlot;
import reader.GTFParser;
import sequenceModifier.DNAOperations;

public class ReadSimulator {

	private int readLength;
	private double mean;
	private double standardDeviation;
	private double mutationRate;
	
	private NormalDistribution normalDistr;
	private Random rand;
	private GenomeAnnotation ga;
	GenomeSequenceExtractor gse;
	
	private ConfigHelper ch;
	
	private ArrayList<String[]> readCountList;
	/* contains fragment length as key and amount of fragments having this length as value */
	private TreeMap<Integer, Integer> fragLengthMap;
	/* contains amount mutations as key and amount of reads having this many mutations as value */
	private TreeMap<Integer, Integer> mutationMap;
	
	public ReadSimulator(int readLength, double mean, double standardDeviation, double mutationRate, String pathToReadcounts){
		this.readLength = readLength;
		this.mean = mean;
		this.standardDeviation = standardDeviation;
		this.mutationRate = mutationRate;
		
		this.ch = new ConfigHelper();
		this.rand = new Random();
		this.fragLengthMap = new TreeMap<>();
		this.mutationMap = new TreeMap<>();
		this.normalDistr = new NormalDistribution(this.mean, this.standardDeviation);
		this.readCountList = AllroundFileReader.readReadcounts(pathToReadcounts);
		
		DebugMessageFactory.printInfoDebugMessage(ConfigReader.DEBUG_MODE, "Read in GenomeAnnotation.");
		ga = GTFParser.readGtfFile("h.ens.75", "/home/proj/biosoft/praktikum/genprakt-ws16/gtf/Homo_sapiens.GRCh37.75.gtf");
		
		gse = new GenomeSequenceExtractor(ch.getDefaultOutputPath()+"offsets.txt", "/home/proj/biosoft/praktikum/genprakt-ws16/assignment/a2/data/Homo_sapiens.GRCh37.75.dna.toplevel.fa", ga);
	}
	
	private NormalDistribution getNormalDistribution(double mean, double deviation){
		return new NormalDistribution(mean, deviation);
	}

	private void simulateReads(){
		
		int headerCount = 0;
		int fin = 1;
		
		ExternalFileWriter forwardWriter = new ExternalFileWriter();
		forwardWriter.openWriter(ch.getDefaultOutputPath()+"fw.fastq");
		ExternalFileWriter reverseWriter = new ExternalFileWriter();
		reverseWriter.openWriter(ch.getDefaultOutputPath()+"rw.fastq");
		
		ExternalFileWriter simulMappingWriter = new ExternalFileWriter();
		simulMappingWriter.openWriter(ch.getDefaultOutputPath()+"simulmapping.info");
		
		simulMappingWriter.writeToWriter("READ_ID\tCHR\tGENE_ID\tTRANSCRIPT_ID\tFW_REGVEC\tRW_REGVEC\tFW_REGVEC_TRANSCRIPT\tRW_REGVEC_TRANSCRIPT\tFW_MUT\tRW_MUT\n");
		
		/* iterate over all transcripts for readCounts file */
		for(String[] array : readCountList){
			String geneId = array[0];
			String transcriptId = array[1];
			int count = Integer.parseInt(array[2]);
			int amount = (int)getNormalDistribution(count, 0.1*count).sample();
			
			String sequence = gse.getTranscriptSequence(geneId, transcriptId);
			int transcriptLength = sequence.length();
			
			/* iterate over generated amount of reads */
			for (int i = 0; i < amount; i++) {
				
				int fragmentLength;
				int start;
				int stop;
				Vector<Integer> fwMut = new Vector<>();
				Vector<Integer> rwMut = new Vector<>();
				
				/* create a normal distributed value for the fragment length */
				do{
					fragmentLength = (int)normalDistr.sample();
				} while(fragmentLength <= readLength || fragmentLength > (transcriptLength-readLength) || fragmentLength < 0);
				
				if(fragLengthMap.containsKey(fragmentLength)){
					fragLengthMap.put(fragmentLength, fragLengthMap.get(fragmentLength)+1);
				}else{
					fragLengthMap.put(fragmentLength, 1);
				}
				
				int max = transcriptLength-fragmentLength-readLength;
				int min = 0;
				start = rand.nextInt((max-min) + 1) + min;
				stop = start+fragmentLength;
				
				/* create read */
				String forwardRead = sequence.substring(start, start+readLength);
				String reverseRead = DNAOperations.getReverseComplement(sequence.substring(stop-readLength,stop));
				
				/* mutate read */
				for (int j = 0; j < forwardRead.length(); j++) {
					if(rand.nextInt(100/(int)(mutationRate*100)) == 0){
						StringBuilder sb = new StringBuilder(forwardRead);
						sb.setCharAt(j, DNAOperations.mutateAminoAcid(forwardRead.charAt(j)));
						forwardRead = sb.toString();
						fwMut.add(j);
					}
					if(rand.nextInt(100/(int)(mutationRate*100)) == 0){
						StringBuilder sb = new StringBuilder(reverseRead);
						sb.setCharAt(j, DNAOperations.mutateAminoAcid(reverseRead.charAt(j)));
						reverseRead = sb.toString();
						rwMut.add(j);
					}
				}
				
				if(mutationMap.containsKey(fwMut.size())){
					mutationMap.put(fwMut.size(), mutationMap.get(fwMut.size())+1);
				}else{
					mutationMap.put(fwMut.size(), 1);
				}
				if(mutationMap.containsKey(rwMut.size())){
					mutationMap.put(rwMut.size(), mutationMap.get(rwMut.size())+1);
				}else{
					mutationMap.put(rwMut.size(), 1);
				}
				
				/* create scores */
				String buff = "";
				for (int j = 0; j < readLength; j++) {
					buff += (FASTQQualityScores.QUALITY_SCORES_ARRAY[FASTQQualityScores.QUALITY_SCORES_ARRAY.length-1]);
				}
				String forwardScore = buff;
				String reverseScore = buff;
				
				/* create forward and reverse read pair */
				FASTQElement forward = new FASTQElement("@_"+String.valueOf(headerCount)+"_forward", forwardRead, forwardScore);
				FASTQElement reverse = new FASTQElement("@_"+String.valueOf(headerCount)+"_reverse", reverseRead, reverseScore);
				
				forwardWriter.writeToWriter(forward.getHeader()+"\n"+forward.getSequence()+"\n+\n"+forward.getQualityScores()+"\n\n");
				reverseWriter.writeToWriter(reverse.getHeader()+"\n"+reverse.getSequence()+"\n+\n"+reverse.getQualityScores()+"\n\n");
				
				simulMappingWriter.writeToWriter(
						headerCount+"\t"+
						ga.getGene(geneId).getChromosome().getID()+"\t"+
						geneId+"\t"+
						transcriptId+"\t"+
						"0-0\t"+
						"0-0\t"+
						start+"-"+(start+readLength)+"\t"+
						(stop-readLength)+"-"+stop+"\t"+
						"["+StringUtils.join(fwMut, ", ")+"]\t"+
						"["+StringUtils.join(rwMut, ", ")+"]\t"+
						"\n");
				
				headerCount++;
			}
			
			DebugMessageFactory.printInfoDebugMessage(ConfigReader.DEBUG_MODE, "Finished "+fin+"/"+readCountList.size());
			fin++;
		}
		
		forwardWriter.closeWriter();
		reverseWriter.closeWriter();
		simulMappingWriter.closeWriter();
		
		/* create plots */
		
		int minX = fragLengthMap.firstEntry().getKey();
		int maxX = fragLengthMap.lastEntry().getKey();
		int minY = fragLengthMap.firstEntry().getValue();
		int maxY = fragLengthMap.lastEntry().getValue();
		
		Pair<Vector<Vector<Object>>,Vector<Vector<Object>>> pair;
		
		Vector<Vector<Object>> vecVec1 = new Vector<>();
		Vector<Vector<Object>> vecVec2 = new Vector<>();
		
		Vector<Object> vecX = new Vector<>();
		Vector<Object> vecY = new Vector<>();
		
		for(Entry<Integer, Integer> entry : fragLengthMap.entrySet()){
			vecX.add(entry.getValue());
			vecY.add(entry.getKey());
		}
		vecVec1.add(vecX);
		vecVec2.add(vecY);
		
		pair = new Pair<Vector<Vector<Object>>, Vector<Vector<Object>>>(vecVec1, vecVec2);
		
		LinePlot lp = new LinePlot(pair, "Fragment Length Distribution", "Amount fragments", "Fragment length", minX, minY, maxX, maxY, false);
		lp.showLegend = false;
		lp.filename = "fragLengthDistrib";
		lp.plot();
		
	}
	
	public int getReadLength() {
		return readLength;
	}

	public void setReadLength(int readLength) {
		this.readLength = readLength;
	}

	public double getStandardDeviation() {
		return standardDeviation;
	}

	public void setStandardDeviation(int standardDeviation) {
		this.standardDeviation = standardDeviation;
	}

	public double getMean() {
		return mean;
	}

	public void setMean(int mean) {
		this.mean = mean;
	}

	public double getMutationRate() {
		return mutationRate;
	}

	public void setMutationRate(double mutationRate) {
		this.mutationRate = mutationRate;
	}
	
	
	public static void main(String[] args) {
		
		ReadSimulator rsim = new ReadSimulator(75, 200.0, 80.0, 0.01, "/home/proj/biosoft/praktikum/genprakt-ws16/assignment/a2/data/readcounts.simulation");
		
		rsim.simulateReads();
		
	}
}
