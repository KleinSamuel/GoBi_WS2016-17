package assignment_1;

import java.util.HashMap;
import java.util.Map.Entry;

import genomeAnnotation.GenomeAnnotation;
import io.ConfigHelper;
import io.ConfigReader;
import reader.GTFParser;
import task1.OverlappingGenes;

public class Task_4 {

	public void execute_task_4() {

		ConfigHelper ch = new ConfigHelper();

		HashMap<String, String> fileMap = ConfigReader.readFilepathConfig(ch.getDefaultConfigPath("gtf-paths.txt"),
				"\t", new String[] { "#" });

		GenomeAnnotation ga;
		OverlappingGenes og;
		for (Entry<String, String> entry : fileMap.entrySet()) {

			ga = GTFParser.readGtfFile(entry.getKey(), entry.getValue());
			og = new OverlappingGenes(ga, ch.getDefaultOutputPath());
			og.writeOverlappingGenesToFile();
			og.writeOverlapsPerBiotypeToFile(ch.getPathToDirOutsideOfJar());
		}
	}
	
}
