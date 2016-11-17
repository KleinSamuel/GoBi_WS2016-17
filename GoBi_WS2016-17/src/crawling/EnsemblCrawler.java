package crawling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import debugStuff.DebugMessageFactory;
import io.ConfigReader;

/**
 * Used to crawl some information from the ensembl website
 * 
 * TODO: Find other ways because this is slow as FUCK!!!!
 * 
 * @author Samuel Klein
 */
public class EnsemblCrawler {

	/**
	 * URL to ensembl website without gene id
	 * 
	 * Sample gene id : ENSG00000104529
	 */
	private static final String baseURL = "http://www.ensembl.org/Homo_sapiens/Gene/Summary?db=core;g=";
	
	public static String getUrlForGeneID(String geneID){
		return baseURL+geneID;
	}
	
	/**
	 * Get information for Assignment 1 Task 3
	 * - gene name
	 * - number of transcripts
	 * - number of proteins
	 * - location in genome
	 * 
	 * @param geneID
	 * @return String 
	 */
	public String[] getGeneInfo(String geneID){
		
		DebugMessageFactory.printInfoDebugMessage(true, "Started crawling...");
		
		String geneName = "";
        String fromTo = "";
        int numTrans = 0;
        int numProts = 0;
		
        URL url = null;
        
		try {
			
		url = new URL(baseURL+geneID);
		
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        String inputLine;
        Pattern geneNameP = Pattern.compile("<title>Gene: (.*?) .*<\\/title>");
        Pattern pNumTrans = Pattern.compile(".*?This gene has (.*?) transcripts.*");
        Pattern pLocation = Pattern.compile(".*?\"\\/Homo_sapiens\\/Location\\/View\\?db=core;g="+geneID+";r=(.*?)\"");
        Pattern pNumProt = Pattern.compile(".*?title=\"View protein\">(.*?)<\\/a>.*?");
        Matcher matcher;
        
        while ((inputLine = in.readLine()) != null){
        	
        	if(geneName.equals("")){
        		matcher = geneNameP.matcher(inputLine);
        		if(matcher.find()){
        			geneName = matcher.group(1);
        		}        		
        	}
        	if(numTrans == 0){
        		matcher = pNumTrans.matcher(inputLine);
        		if(matcher.find()){
        			numTrans = Integer.parseInt(matcher.group(1));
        		}        		
        	}
        	if(fromTo.equals("")){
        		matcher = pLocation.matcher(inputLine);
        		if(matcher.find()){
        			fromTo = matcher.group(1);
        		}        		
        	}
        	matcher = pNumProt.matcher(inputLine);
        	while(matcher.find()){
        		numProts++;
        	}
        }
       
        in.close();
        
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		numProts = numProts/2;
		
		DebugMessageFactory.printInfoDebugMessage(ConfigReader.DEBUG_MODE, "Finished crawling from "+url.toString());
		
		return new String[]{url.toString(), geneName+" "+geneID+"("+fromTo+") num transcripts: "+numTrans+" num proteins: "+numProts};
	}
}
