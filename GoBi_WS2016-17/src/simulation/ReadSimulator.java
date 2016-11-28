package simulation;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import org.apache.commons.math3.distribution.NormalDistribution;

import fileFormats.FASTQElement;
import io.AllroundFileReader;
import javafx.util.Pair;

public class ReadSimulator {

	private int readLength;
	private double mean;
	private double standardDeviation;
	private double mutationRate;
	
	private NormalDistribution normalDistr;
	private Random rand;
	
	private ArrayList<String[]> readCountList;
	
	public ReadSimulator(int readLength, double mean, double standardDeviation, double mutationRate, String pathToReadcounts){
		this.readLength = readLength;
		this.mean = mean;
		this.standardDeviation = standardDeviation;
		this.mutationRate = mutationRate;
		
		this.rand = new Random();
		this.normalDistr = new NormalDistribution(this.mean, this.standardDeviation);
		this.readCountList = AllroundFileReader.readReadcounts(pathToReadcounts);
	}
	
	private NormalDistribution getNormalDistribution(double mean, double deviation){
		return new NormalDistribution(mean, deviation);
	}

	private void iterateTranscripts(){
		
		int headerCount = 0;
		
		Vector<Pair<FASTQElement,FASTQElement>> fastqVector = new Vector<>();
		
		/* iterate over all transcripts for readCounts file */
		for(String[] array : readCountList){
			String geneId = array[0];
			String transcriptId = array[1];
			int count = Integer.parseInt(array[2]);
			int amount = (int)getNormalDistribution(count, 0.1*count).sample();
			
			int transcriptLength = 100;
			
			/* iterate over generated amount of reads */
			for (int i = 0; i < amount; i++) {
				
				int fragmentLength;
				int start;
				int stop;
				
				/* create a normal distributed value for the fragment length */
				do{
					fragmentLength = (int)normalDistr.sample();
				} while(fragmentLength <= readLength && fragmentLength > (transcriptLength-fragmentLength));
				
				start = rand.nextInt((transcriptLength-fragmentLength+1));
				stop = start+fragmentLength;
				
				/* create forward and reverse read pair */
				
				
				/* add forward and reverse read pair to vector */
//				fastqVector.addElement(new Pair<FASTQElement, FASTQElement>(key, value));
				
			}
			headerCount++;
		}
		
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
		
		rsim.iterateTranscripts();
		
	}
}
