package debugStuff;

import dateNtimeStuff.DateFactory;

/**
 * Containing debug stuff as printing out messages and statuses.
 * 
 * @author Samuel Klein
 */
public class DebugMessageFactory {
	
	/**
	 * Print out a normal debug message containing date and time.
	 * 
	 * @param message to be printed out
	 */
	public static void printNormalDebugMessage(boolean debugMode, String message){
		if(!debugMode){
			return;
		}
		System.out.println("[  "+"OK"+"   ] "+"("+DateFactory.getDateAsString()+"):\t"+message);
	}
	
	/**
	 * Print out a error debug message containing date and time.
	 * 
	 * @param message to be printed out
	 */
	public static void printErrorDebugMessage(boolean debugMode, String message){
		if(!debugMode){
			return;
		}
		System.out.println("[ "+"ERROR"+" ] "+"("+DateFactory.getDateAsString()+"):\t"+message);
	}
	
	/**
	 * Print out a information debug message containing date and time.
	 * 
	 * @param message to be printed out
	 */
	public static void printInfoDebugMessage(boolean debugMode, String message){
		if(!debugMode){
			return;
		}
		System.out.println("[ "+"INFO"+"  ] "+"("+DateFactory.getDateAsString()+"):\t"+message);
	}
	
	/**
	 * Print out a exit debug message containing date and time.
	 * 
	 * @param message to be printed out
	 */
	public static void printExitDebugMessage(boolean debugMode, String message){
		if(!debugMode){
			return;
		}
		System.out.println("[ "+"EXIT"+"  ] "+"("+DateFactory.getDateAsString()+"):\t"+message);
	}
	
}
