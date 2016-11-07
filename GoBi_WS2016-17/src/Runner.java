import java.util.LinkedList;

import genomeAnnotation.Chromosome;
import genomeAnnotation.GenomeAnnotation;
import reader.AnnotationMapper;
import reader.AnnotationMapper.AnnotationMapItem;
import reader.GTFParser;
import task1.UnionTranscripts;

public class Runner {

	public static void main(String[] args) {

		AnnotationMapper annotationMapper = new AnnotationMapper(args[0], args[1]);
		LinkedList<AnnotationMapItem> anntoationsToRun = annotationMapper.getAnnotations();

		for (AnnotationMapItem item : anntoationsToRun) {

			long time = System.currentTimeMillis();
			GenomeAnnotation ga = GTFParser.readGtfFile(item.getId(), item.getFullPath());
			time = System.currentTimeMillis() - time;
			System.out.println(ga.getName() + "\ttime needed: " + time / 1000 + " s");
			for (Chromosome c : ga.getChromosomes().values())
				System.out.println("chromosome " + c.getID() + "\t#genes: " + c.getGenes().size());

			UnionTranscripts unionTrs = new UnionTranscripts(ga, ga.getClass().getProtectionDomain().getCodeSource()
					.getLocation().toExternalForm().substring(5).replace("bin", "src") + "output/");
			unionTrs.writeOccurencesToFile(unionTrs.calculateUnionTranscriptDistribution());

		}

	}

}
