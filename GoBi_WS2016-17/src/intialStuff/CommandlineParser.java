package intialStuff;

import java.util.HashMap;
import java.util.Map.Entry;

import debugStuff.DebugMessageFactory;

/**
 * This is used to parse command line arguments.
 * 
 * @author Samuel Klein
 */
public class CommandlineParser {

	private HashMap<String, Object> argumentMap;
	private boolean debugMode = false;
	
	/**
	 * 1) Add argument classifier with addExpectedArgument()
	 * 2) Extract the argument list  with extractArguments()
	 * 
	 * @param debugMode
	 */
	public CommandlineParser(boolean debugMode){
		resetParameterMap();
		this.debugMode = debugMode;
	}
	
	/**
	 * Method containing all needed methods. Used to parse commandline
	 * @param expectedArguments
	 * @param arguments
	 * @param exitIfArgumentIsMissing
	 * @return HashMap<String, Object> containing argument specifier and value
	 */
	public HashMap<String, Object> parseCommandline(String[] expectedArguments, String[] arguments, boolean exitIfArgumentIsMissing){
		resetParameterMap();
		addExpectedArgument(expectedArguments);
		extractArguments(arguments);
		
		if(exitIfArgumentIsMissing && checkForMissingArguments()){
			DebugMessageFactory.printExitDebugMessage(true, "Missing argument(s).");
			return null;
		}
		return getArgumentMap();
	}
	
	/**
	 * Clear the parameter map
	 */
	public void resetParameterMap(){
		this.argumentMap = new HashMap<String, Object>();
	}
	
	/**
	 * Add argument classifier into argument map
	 * @param argAbbreviations String[] of argument abbreviations
	 */
	public void addExpectedArgument(String[] argAbbreviations){
		for (int i = 0; i < argAbbreviations.length; i++) {
			addExpectedArgument(argAbbreviations[i]);
		}
	}
	
	/**
	 * Add argument classifier into argument map
	 * @param argAbbreviation String argument abbreviation
	 */
	public void addExpectedArgument(String argAbbreviation){
		if(!argumentMap.containsKey(argAbbreviation)){
			argumentMap.put(argAbbreviation, null);
			DebugMessageFactory.printNormalDebugMessage(debugMode, "Added "+argAbbreviation+" to map");
		}else{
			DebugMessageFactory.printNormalDebugMessage(debugMode, argAbbreviation+" already exists in map");
		}
	}
	
	/**
	 * Traverse the original argument map and put respective arguments into map
	 * @param argumentList String[] of original arguments
	 */
	public void extractArguments(String[] argumentList){
		for (int i = 0; i < argumentList.length; i+=2) {
			if(argumentMap.containsKey(argumentList[i])){
				argumentMap.put(argumentList[i], argumentList[i+1]);
			}
		}
	}
	
	/**
	 * Check if all expected arguments were received
	 * @return false if an argument is missing
	 */
	public boolean checkForMissingArguments(){
		boolean returnValue = false;
		for(Entry<String, Object> entry : argumentMap.entrySet()){
			if(entry.getValue() == null){
				DebugMessageFactory.printInfoDebugMessage(true, "Missing argument for: "+entry.getKey());
				returnValue = true;
			}
		}
		return returnValue;
	}
	
	/**
	 * Print the argument map
	 */
	public void printMap(){
		System.out.println("MAP SIZE: "+argumentMap.size());
		for(Entry<String, Object> entry : argumentMap.entrySet()){
			System.out.println("[  KEY  ]"+entry.getKey());
			System.out.println("[ VALUE ]"+entry.getValue());
		}
	}
	
	/**
	 * Get argument map
	 * @return argumentMap HashMap<String, Object>
	 */
	public HashMap<String, Object> getArgumentMap(){
		return this.argumentMap;
	}
	
	/**
	 * Set debugmode
	 * @param debugMode
	 */
	public void setDebugMode(boolean debugMode){
		this.debugMode = debugMode;
	}
	
	/**
	 * Get debugmode
	 * @return boolean if debugmode is enabled
	 */
	public boolean isDebugMode(){
		return this.debugMode;
	}
	
}
