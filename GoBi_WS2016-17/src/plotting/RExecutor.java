package plotting;

import java.io.IOException;

import io.ConfigReader;

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
		
		String r_binary_location = ConfigReader.readConfig().get("r_binary");
		
		try {
			
			@SuppressWarnings("unused")
			Process rInstance = new ProcessBuilder(r_binary_location, "-e", rCommand).inheritIO().start();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
	}

}
