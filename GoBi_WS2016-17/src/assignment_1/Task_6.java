package assignment_1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import genomeAnnotation.Gene;
import genomeAnnotation.GenomeAnnotation;
import io.AllroundFileWriter;
import io.ConfigHelper;
import io.ConfigReader;
import plotR.RScriptCaller;
import reader.GTFParser;
import task1.ExonSkippingAnalysis;
import task1.OverlappingGenes;

public class Task_6 {

	public void execute_task_6() {

		ConfigHelper ch = new ConfigHelper();

		HashMap<String, String> fileMap = ConfigReader.readFilepathConfig(ch.getDefaultConfigPath("gtf-paths.txt"),
				"\t", new String[] { "#" });

		GenomeAnnotation ga;
		OverlappingGenes og;
		LinkedList<String> arguments;
		ArrayList<String> pathsToPlots = new ArrayList<>();
		ArrayList<ArrayList<String[]>> info = new ArrayList<>();
		ArrayList<String[]> topTen;

		for (Entry<String, String> entry : fileMap.entrySet()) {

			ga = GTFParser.readGtfFile(entry.getKey(), entry.getValue());

			ExonSkippingAnalysis esa = new ExonSkippingAnalysis(ga, ch.getDefaultOutputPath());

			esa.analyseExonSkippings();

			arguments = new LinkedList<>();
			arguments.add(esa.getOutputDir());
			arguments.add(ga.getName());

			new RScriptCaller(ch.getPathToDirOutsideOfJar() + "ExonSkippingPlotter.R",
					esa.getOutputFile().getAbsolutePath(), arguments).execRScript();
			pathsToPlots.add(ch.getDefaultOutputPath() + ga.getName() + "_skipped_exons");
			topTen = new ArrayList<>();
			for (Gene g : esa.getTopTenExon()) {
				String[] infos = g.getInfoLine();
				infos[1] = infos[1] + " maxSkippedExons: " + esa.getTotalMaxExonSkipped(g) + " maxSkippedBases: "
						+ esa.getTotalMaxBasesSkipped(g);
				topTen.add(infos);
			}
			info.add(topTen);

			pathsToPlots.add(ch.getDefaultOutputPath() + ga.getName() + "_skipped_bases");
			topTen = new ArrayList<>();
			for (Gene g : esa.getTopTenBases()) {
				String[] infos = g.getInfoLine();
				infos[1] = infos[1] + " maxSkippedExons: " + esa.getTotalMaxExonSkipped(g) + " maxSkippedBases: "
						+ esa.getTotalMaxBasesSkipped(g);
				topTen.add(infos);
			}
			info.add(topTen);

		}

		AllroundFileWriter.createHTMLforPlots(ch.getDefaultOutputPath() + "exon_skipping.html", pathsToPlots, info,
				true);

	}
	
}
