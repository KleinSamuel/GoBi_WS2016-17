package plotR;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class RScriptCaller {

	private String pathToR = "/home/proj/biosoft/software/R/R-3.3.0/bin/Rscript";
	private String outputDir = this.getClass().getProtectionDomain().getCodeSource().getLocation().toExternalForm()
			.substring(5)/* .replace("bin", "") */ + "output/";
	private String inputFileName, scriptName;
	private List<String> arguments;

	/*
	 * default constructor (uses default outputDir = output-package R =
	 * /home/proj/biosoft/software/R/R-3.3.0/bin/R)
	 */
	public RScriptCaller(String scriptName, String inputFileName, List<String> args) {
		this.inputFileName = inputFileName;
		this.scriptName = scriptName;
		arguments = new LinkedList<>();
		arguments.add(pathToR);
		arguments.add(scriptName);
		arguments.add(inputFileName);
		arguments.addAll(args);
	}

	/*
	 * constructor: if pathToR == null | outputDirectory == null their default
	 * values are taken
	 */
	public RScriptCaller(String scriptName, String inputFileName, String pathToR, String outputDirectory,
			List<String> args) {
		this.inputFileName = inputFileName;
		this.scriptName = scriptName;
		arguments = new LinkedList<>();
		arguments.add(pathToR);
		arguments.add(scriptName);
		arguments.add(inputFileName);
		arguments.addAll(args);
		if (pathToR != null && !pathToR.equals("null"))
			this.pathToR = pathToR;
		if (outputDirectory != null && !outputDirectory.equals("null"))
			this.outputDir = outputDirectory;
	}

	public void execRScript() {
		try {

			if (this.arguments.get(1).contains("bin")) {
				this.arguments.set(1, this.arguments.get(1).replace("bin", ""));
			} else {
				this.arguments.set(1,
						this.arguments.get(1).replace("UnionTranscriptPlotter.R", "/UnionTranscriptPlotter.R"));
				this.arguments.set(1, this.arguments.get(1).replace("ExonSkippingPlotter.R", "/ExonSkippingPlotter.R"));
				this.arguments.set(1, this.arguments.get(1).replace("OverlapPlotter.R", "/OverlapPlotter.R"));
			}

			System.out.println(Arrays.toString(this.arguments.toArray()));

			@SuppressWarnings("unused")
			// Process proc = new ProcessBuilder(pathToR, scriptName,
			// inputFileName, inputFileName.replace("tsv", "jpg"),
			// args).start();
			Process proc = new ProcessBuilder(arguments).start();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

}
