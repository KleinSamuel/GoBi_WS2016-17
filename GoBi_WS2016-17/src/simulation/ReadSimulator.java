package simulation;

import java.util.ArrayList;
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
import genomeAnnotation.Transcript;
import io.AllroundFileReader;
import io.AllroundFileWriter;
import io.ConfigHelper;
import io.ConfigReader;
import io.ExternalFileWriter;
import javafx.util.Pair;
import plotting.BarPlot;
import plotting.LinePlot;
import reader.GTFParser;
import sequenceModifier.DNAOperations;
import util.Interval;

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
		
////		9	ENSG00000241978 ENST00000374525 2680-2755       2755-2830
//		Vector<Pair<Integer, Integer>> v = ga.getChromosome("9").getGene("ENSG00000241978").getTranscript("ENST00000374525").getGenomicRegionVector(2680, 2754);
//		Vector<Pair<Integer, Integer>> v2 = ga.getChromosome("9").getGene("ENSG00000241978").getTranscript("ENST00000374525").getGenomicRegionVector(2755, 2829);
//		for(Pair<Integer, Integer> p : v){
//			System.out.print(p.getKey()+"-"+p.getValue()+"|");
//		}
//		System.out.println();
//		for(Pair<Integer, Integer> p : v2){
//			System.out.print(p.getKey()+"-"+p.getValue()+"|");
//		}
//		System.out.println();
		
		gse = new GenomeSequenceExtractor(ch.getDefaultOutputPath()+"offsets.txt", "/home/proj/biosoft/praktikum/genprakt-ws16/assignment/a2/data/Homo_sapiens.GRCh37.75.dna.toplevel.fa", ga);
	}
	
	private NormalDistribution getNormalDistribution(double mean, double deviation){
		return new NormalDistribution(mean, deviation);
	}

	public void simulateReads(){
		
		int headerCount = 0;
		int fin = 1;
		
		ExternalFileWriter forwardWriter = new ExternalFileWriter();
		forwardWriter.openWriter(ch.getDefaultOutputPath()+"fw.fastq");
		ExternalFileWriter reverseWriter = new ExternalFileWriter();
		reverseWriter.openWriter(ch.getDefaultOutputPath()+"rw.fastq");
		
		ExternalFileWriter simulMappingWriter = new ExternalFileWriter();
		simulMappingWriter.openWriter(ch.getDefaultOutputPath()+"simulmapping.info");
		
		simulMappingWriter.writeToWriter("READ_ID\tCHR\tGENE_ID\tTRANSCRIPT_ID\tFW_REGVEC\tRW_REGVEC\tFW_REGVEC_TRANSCRIPT\tRW_REGVEC_TRANSCRIPT\tFW_MUT\tRW_MUT\n");
		
		int amountReads = 0;
		int amountNonSplicedReads = 0;
		int amountNonSplicedReadsWithoutMismatches = 0;
		int amountSplicedReads = 0;
		int amountSplicedReadsWitoutMismatches = 0;
		int amountSplicedReadsWithoutMismatchesGT5BPs = 0;
		
		/* iterate over all transcripts for readCounts file */
		for(String[] array : readCountList){
			String geneId = array[0];
			String transcriptId = array[1];
			int count = Integer.parseInt(array[2]);
			int amount = (int)getNormalDistribution(count, 0.1*count).sample();
			
			String sequence = gse.getTranscriptSequence(geneId, transcriptId);
			int transcriptLength = sequence.length();
			
			Transcript currentTranscript = ga.getTranscript(transcriptId);
			
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
				FASTQElement forward = new FASTQElement("@"+String.valueOf(headerCount), forwardRead, forwardScore);
				FASTQElement reverse = new FASTQElement("@"+String.valueOf(headerCount), reverseRead, reverseScore);
				
				forwardWriter.writeToWriter(forward.getHeader()+"\n"+forward.getSequence()+"\n+\n"+forward.getQualityScores()+"\n");
				reverseWriter.writeToWriter(reverse.getHeader()+"\n"+reverse.getSequence()+"\n+\n"+reverse.getQualityScores()+"\n");
				
				int startInTranscriptFW = start;
				int stopInTranscriptFW = (start+readLength-1);
				int startInTranscriptRW = stop-readLength+1;
				int stopInTranscriptRW = stop;
				
				Vector<Interval> genVecFW = currentTranscript.getGenomicRegionVector(startInTranscriptFW, stopInTranscriptFW);
				Vector<Interval> genVecRW = currentTranscript.getGenomicRegionVector(startInTranscriptRW, stopInTranscriptRW);
				
				String genVecFWString = "";
				int cnt = 1;
				for(Interval pair : genVecFW){
					genVecFWString += pair.getStart()+"-"+pair.getStop();
					genVecFWString += (cnt < genVecFW.size()) ? "|" : "";
					cnt++;
				}
				String genVecRWString = "";
				cnt = 1;
				for(Interval pair : genVecRW){
					genVecRWString += pair.getStart()+"-"+pair.getStop();
					genVecRWString += (cnt < genVecRW.size()) ? "|" : "";
					cnt++;
				}
				
				simulMappingWriter.writeToWriter(
						headerCount+"\t"+
						ga.getGene(geneId).getChromosome().getID()+"\t"+
						geneId+"\t"+
						transcriptId+"\t"+
						genVecFWString+"\t"+
						genVecRWString+"\t"+
						startInTranscriptFW+"-"+stopInTranscriptFW+"\t"+
						startInTranscriptRW+"-"+stopInTranscriptRW+"\t"+
						StringUtils.join(fwMut, ", ")+"\t"+
						StringUtils.join(rwMut, ", ")+"\t"+
						"\n");
				
				headerCount++;
				
				/* compute numbers for barplot */
				amountReads++;
				if(genVecFW.size() == 1 && genVecRW.size() == 1){
					if(fwMut.size() == 0 && rwMut.size() == 0){
						amountNonSplicedReadsWithoutMismatches++;
					}
					amountNonSplicedReads++;
				}else{
					amountSplicedReads++;
					if(fwMut.size() == 0 && rwMut.size() == 0){
						amountSplicedReadsWitoutMismatches++;
						
						boolean shorterThan5BP = false;
						for(Interval pair : genVecFW){
							if(pair.getStop()-pair.getStart() < 5){
								shorterThan5BP = true;
							}
						}
						for(Interval pair : genVecRW){
							if(pair.getStop()-pair.getStart() < 5){
								shorterThan5BP = true;
							}
						}
						if(!shorterThan5BP){
							amountSplicedReadsWithoutMismatchesGT5BPs++;
						}
					}
				}
			}
			
			DebugMessageFactory.printInfoDebugMessage(ConfigReader.DEBUG_MODE, "Finished "+fin+"/"+readCountList.size());
			fin++;
		}
		
		forwardWriter.closeWriter();
		reverseWriter.closeWriter();
		simulMappingWriter.closeWriter();
		
		/* create plots */
		
		ArrayList<String> pathList = new ArrayList<>();
		
		/* fragment length distribution */
		int minX = fragLengthMap.firstEntry().getKey();
		int maxX = fragLengthMap.lastEntry().getKey();
		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;
		
		Pair<Vector<Vector<Object>>,Vector<Vector<Object>>> pair;
		
		Vector<Vector<Object>> vecVec1 = new Vector<>();
		Vector<Vector<Object>> vecVec2 = new Vector<>();
		
		Vector<Object> vecX = new Vector<>();
		Vector<Object> vecY = new Vector<>();
		
		for(Entry<Integer, Integer> entry : fragLengthMap.entrySet()){
			vecY.add(entry.getValue());
			vecX.add(entry.getKey());
			minY = Math.min(minY, entry.getValue());
			maxY = Math.max(maxY, entry.getValue());
		}
		vecVec1.add(vecX);
		vecVec2.add(vecY);
		
		pair = new Pair<Vector<Vector<Object>>, Vector<Vector<Object>>>(vecVec1, vecVec2);
		
		LinePlot lp = new LinePlot(pair, "Fragment Length Distribution", "Amount fragments", "Fragment length", minX, minY, maxX, maxY, false, false);
		lp.showLegend = false;
		lp.filename = "fragLengthDistrib";
		lp.plot();
		pathList.add(ch.getDefaultOutputPath()+lp.filename);
		
		/* mutation distribution */
		minX = mutationMap.firstEntry().getKey();
		maxX = mutationMap.lastEntry().getKey();
		minY = Integer.MAX_VALUE;
		maxY = Integer.MIN_VALUE;
		
		vecVec1 = new Vector<>();
		vecVec2 = new Vector<>();
		
		vecX = new Vector<>();
		vecY = new Vector<>();
		
		for(Entry<Integer, Integer> entry : mutationMap.entrySet()){
			vecY.add(entry.getValue());
			vecX.add(entry.getKey());
			minY = Math.min(minY, entry.getValue());
			maxY = Math.max(maxY, entry.getValue());
		}
		vecVec1.add(vecX);
		vecVec2.add(vecY);
		
		pair = new Pair<Vector<Vector<Object>>, Vector<Vector<Object>>>(vecVec1, vecVec2);
		
		lp = new LinePlot(pair, "Mutation Distribution", "Amount mutations", "Amount reads", minX, 0, maxX, maxY, false, true);
		lp.showLegend = false;
		lp.filename = "mutationDistrib";
		lp.plot();
		pathList.add(ch.getDefaultOutputPath()+lp.filename);
		
		Vector<Object> vecKey = new Vector<>();
		Vector<Object> vecValue = new Vector<>();
		
		vecKey.add(amountReads);
		vecValue.add("Total reads");
		vecKey.add(amountNonSplicedReads);
		vecValue.add("Unspliced reads");
		vecKey.add(amountNonSplicedReadsWithoutMismatches);
		vecValue.add("Unspliced reads w/o mismatches");
		vecKey.add(amountSplicedReads);
		vecValue.add("Spliced reads");
		vecKey.add(amountSplicedReadsWitoutMismatches);
		vecValue.add("Spliced reads w/o mismatches");
		vecKey.add(amountSplicedReadsWithoutMismatchesGT5BPs);
		vecValue.add("Spliced reads w/o mismatches > 5 bp");
		
		Pair<Vector<Object>, Vector<Object>> pairBP = new Pair<Vector<Object>, Vector<Object>>(vecKey, vecValue);
		
		BarPlot bp = new BarPlot(pairBP, "Simulated Reads", "", "Amount", true);
		bp.filename = "simReadInfo";
		bp.bottomMargin = 15;
		bp.leftMargin = 7;
		bp.plot();
		pathList.add(ch.getDefaultOutputPath()+bp.filename);
		
		AllroundFileWriter.createHTMLforPlots(ch.getDefaultOutputPath() + "simulation.html", pathList, null, true);
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
	
//	public static void main(String[] args) {
//		
//		ReadSimulator rsim = new ReadSimulator(75, 200.0, 80.0, 0.01, "/home/proj/biosoft/praktikum/genprakt-ws16/assignment/a2/data/readcounts.simulation");
//		rsim.simulateReads();
//		
//	}
}
