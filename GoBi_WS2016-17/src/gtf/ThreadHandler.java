package gtf;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import debugStuff.DebugMessageFactory;
import io.ConfigReader;

public class ThreadHandler {

	private int THREAD_AMOUNT;
	private GenomeAnnotation genomeAnnotation;
	private ArrayList<Thread> threadList;
	
	public ThreadHandler(int amountThreads){
		this.genomeAnnotation = new GenomeAnnotation();
		this.threadList = new ArrayList<>();
		this.THREAD_AMOUNT = amountThreads;
	}
	
	public ThreadHandler(){
		this(Runtime.getRuntime().availableProcessors());
	}
	
	public void startThreads(String filepath){
		
		DebugMessageFactory.printInfoDebugMessage(ConfigReader.DEBUG_MODE, "PARSING FILE : "+filepath);
		
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			
			int amountLines = countLines(filepath);
			
			int part = amountLines/THREAD_AMOUNT;
			
			String line = null;
			ArrayList<String> currentLines = new ArrayList<>();
			int countLines = 0;
			
			while((line = br.readLine()) != null){
				
				countLines += 1;
				
				if(countLines == part){
					
					countLines = 0;
					
					addAndStartThread(currentLines);
					
					currentLines = new ArrayList<>();
				}
				
				currentLines.add(line);
			}
			
			br.close();
			
			waitForThreads();
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	private void addAndStartThread(ArrayList<String> lines){
		this.threadList.add(new Thread(new GTF_Parser(this.threadList.size()+1+"", lines, this.genomeAnnotation)));
		this.threadList.get(this.threadList.size()-1).start();
	}
	
	private void printCurrentReadInProgress(long currentByte, long amountBytes){
		double currentProgress = (double)currentByte/amountBytes*100;
		double roundedProgress = Math.round(currentProgress*100.0)/100.0;
		
		DebugMessageFactory.printInfoDebugMessage(ConfigReader.DEBUG_MODE, "READ IN PROGRESS: "+roundedProgress+" %");
	}
	
	public static int countLines(String filename) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        is.close();
	    }
	}
	
	public void waitForThreads(){
		for (Thread t : this.threadList){
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public GenomeAnnotation getGenomeAnnotation(){
		return this.genomeAnnotation;
	}
}
