package simulation;

import org.apache.commons.math3.distribution.NormalDistribution;

public class ReadSimulator {

	private int readLength;
	private double mean;
	private double standardDeviation;
	private double mutationRate;
	
	private NormalDistribution normalDistr;
	
	public ReadSimulator(int readLength, double mean, double standardDeviation, double mutationRate){
		this.readLength = readLength;
		this.mean = mean;
		this.standardDeviation = standardDeviation;
		this.mutationRate = mutationRate;
		
		this.normalDistr = new NormalDistribution(this.mean, this.standardDeviation);
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
	
}
