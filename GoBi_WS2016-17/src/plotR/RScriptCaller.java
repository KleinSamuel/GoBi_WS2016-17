package plotR;

import java.io.IOException;

public class RScriptCaller {

	private String pathToR = "/home/proj/biosoft/software/R/R-3.3.0/bin/R";
	private String outputDir = this.getClass().getProtectionDomain().getCodeSource().getLocation().toExternalForm()
			.substring(5).replace("bin", "src") + "output/";
	private String outputFileName, scriptName;

	/*
	 * default constructor (uses default outputDir = output-package R =
	 * /home/proj/biosoft/software/R/R-3.3.0/bin/R)
	 */
	public RScriptCaller(String fileName, String scriptName) {
		outputFileName = fileName;
		this.scriptName = scriptName;
	}

	/*
	 * constructor: if pathToR == null | outputDirectory == null their default
	 * values are taken
	 */
	public RScriptCaller(String fileName, String scriptName, String pathToR, String outputDirectory) {
		outputFileName = fileName;
		this.scriptName = scriptName;
		if (pathToR != null && !pathToR.equals("null"))
			this.pathToR = pathToR;
		if (outputDirectory != null && !outputDirectory.equals("null"))
			this.outputDir = outputDirectory;
	}

	public void execRScript() {
		try {
			@SuppressWarnings("unused")
			Process proc = new ProcessBuilder(pathToR, scriptName, outputDir + "/" + outputFileName).start();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

}
