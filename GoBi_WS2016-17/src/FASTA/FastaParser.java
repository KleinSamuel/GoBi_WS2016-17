package FASTA;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import debugStuff.DebugMessageFactory;

public class FastaParser {

	private HashMap<FastaHeader, FastaSequence> fastaMap;
	private boolean debugMode;
	
	public FastaParser(boolean debugMode){
		this.debugMode = debugMode;
		resetFastaMap();
	}
	
	/**
	 * Read in a FASTA file and put content into a hashmap
	 * 
	 * @param filePath String
	 */
	public void readFastaFile(String filePath){
		
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			
			DebugMessageFactory.printNormalDebugMessage(debugMode, "Opened BufferedWriter");
			
			String line = null;
			String oldHeader = "";
			String oldSequence = "";
			
			while((line = br.readLine()) != null){
				if(line.startsWith(">")){
					if(!oldHeader.equals("")){
						FastaHeader header = new FastaHeader();
						header.setTemporaryHeader(oldHeader);
						
						FastaSequence sequence= new FastaSequence();
						sequence.setSequence(oldSequence);
						
						fastaMap.put(header, sequence);
						DebugMessageFactory.printNormalDebugMessage(debugMode, "Put entry in Fasta Map");
					}
					oldHeader = line;
				}else{
					oldSequence += line;
				}
			}
			
			FastaHeader header = new FastaHeader();
			header.setTemporaryHeader(oldHeader);
			FastaSequence sequence= new FastaSequence();
			sequence.setSequence(oldSequence);
			
			fastaMap.put(header, sequence);
			DebugMessageFactory.printNormalDebugMessage(debugMode, "Put entry in Fasta Map");
			
			br.close();
			DebugMessageFactory.printNormalDebugMessage(debugMode, "Closed BufferedWriter");
			
		} catch (FileNotFoundException e) {
			DebugMessageFactory.printErrorDebugMessage(true, this.getClass().getName()+" | File not found: "+filePath);
			DebugMessageFactory.printExitDebugMessage(true, "Exited Program.");
			System.exit(1);
		} catch (IOException e) {
			DebugMessageFactory.printErrorDebugMessage(true, this.getClass().getName()+" | IOException");
			DebugMessageFactory.printExitDebugMessage(true, "Exited Program.");
		}
	}
	
	/**
	 * Clear the fasta map
	 */
	public void resetFastaMap(){
		this.fastaMap = new HashMap<FastaHeader, FastaSequence>();
		DebugMessageFactory.printNormalDebugMessage(debugMode, "Reset Fasta Map");
	}
	
	/**
	 * Print the fasta map content
	 */
	public void printFastaMap(){
		for(Entry<FastaHeader, FastaSequence> entry : fastaMap.entrySet()){
			System.out.println(entry.getKey().getTemporaryHeader());
			System.out.println(entry.getValue().getSequence());
		}
	}
	
	/**
	 * Print information about the map
	 * TODO: add more information
	 */
	public void printInformationAboutMap(){
		DebugMessageFactory.printInfoDebugMessage(true, "Size of fasta map: "+fastaMap.size());
	}

}
