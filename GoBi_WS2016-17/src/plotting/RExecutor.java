package plotting;

import java.io.IOException;

/**
 * Create an external instance of R as a thread.
 * 
 * @author Samuel Klein
 */
public class RExecutor implements Runnable{

	/**
	 * R commands as string.
	 */
	String rCommand;
	
	/**
	 * Create an R instance.
	 * 
	 * @param command concatenated string representing R commands
	 */
	public RExecutor(String command) {
		this.rCommand = command;
	}

	@Override
	public void run() {
		
		/* TODO */
		String r_binary_location = "/usr/bin/R";
		
		try {
			
			@SuppressWarnings("unused")
			Process rInstance = new ProcessBuilder(r_binary_location, "-e", rCommand).start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
