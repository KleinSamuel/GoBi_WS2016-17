package assignment_1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import genomeAnnotation.GenomeAnnotation;
import io.AllroundFileWriter;
import io.ConfigHelper;
import io.ConfigReader;
import plotR.RScriptCaller;
import reader.GTFParser;
import task1.OverlappingGenes;
import task1.UnionTranscripts;

public class Task_5 {

	public void execute_task_5() {

		ConfigHelper ch = new ConfigHelper();

		HashMap<String, String> fileMap = ConfigReader.readFilepathConfig(ch.getDefaultConfigPath("gtf-paths.txt"), "\t", new String[] { "#" });

		GenomeAnnotation ga;
		OverlappingGenes og;
		ArrayList<String> pngPaths = new ArrayList<>();

		for (Entry<String, String> entry : fileMap.entrySet()) {

			ga = GTFParser.readGtfFile(entry.getKey(), entry.getValue());

			UnionTranscripts unionTrs = new UnionTranscripts(ga, ch.getDefaultOutputPath());

			unionTrs.writeOccurencesToFile(unionTrs.calculateUnionTranscriptDistribution());

			System.out.println("plotting unionTranscriptDistribution");

			LinkedList<String> arguments = new LinkedList<>();
			arguments.add(unionTrs.getOutputFile().replace(".tsv", ".png"));
			pngPaths.add(unionTrs.getOutputFile().replace(".tsv", ""));
			new RScriptCaller(ch.getPathToDirOutsideOfJar() + "UnionTranscriptPlotter.R", unionTrs.getOutputFile(), arguments).execRScript();
		}

		AllroundFileWriter.createHTMLforPlots(ch.getDefaultOutputPath() + "transcript_lengths.html", pngPaths, null, false);

	}
	
}
