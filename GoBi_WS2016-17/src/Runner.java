import java.util.LinkedList;

import genomeAnnotation.Chromosome;
import genomeAnnotation.GenomeAnnotation;
import plotR.RScriptCaller;
import reader.AnnotationMapper;
import reader.AnnotationMapper.AnnotationMapItem;
import reader.GTFParser;
import task1.OverlappingGenes;
import task1.UnionTranscripts;

public class Runner {

	public static void main(String[] args) {
		// GenomeAnnotation ga = new GenomeAnnotation("ga1");
		// ga.addChromosome(new Chromosome("chr1"));
		// Gene g = new Gene(1, 100, "test", true, "", "", null);
		// Transcript tr1 = new Transcript(10, 49, "testTr1", true, g);
		// Transcript tr2 = new Transcript(10, 70, "testTr2", true, g);
		// Exon e1 = new Exon(10, 20, "e1", false);
		// Exon e2 = new Exon(30, 40, "e2", false);
		// Exon e3 = new Exon(38, 49, "e3", false);
		// Exon e4 = new Exon(52, 70, "e4", false);
		// g.addTranscript(tr1);
		// g.addTranscript(tr2);
		// g.addExon(e1);
		// g.addExon(e2);
		// g.addExon(e3);
		// g.addExon(e4);
		// tr1.addExon(e1);
		// tr1.addExon(e2);
		// tr1.addExon(e3);
		// tr2.addExon(e1);
		// tr2.addExon(e3);
		// tr2.addExon(e4);
		// ga.getChromosome("chr1").addGene(g);
		// UnionTranscript unionTrs = new UnionTranscript(g);
		// System.out.println(unionTrs.getExonicLength());
		// for (Interval i : unionTrs.getCombinedExons()) {
		// System.out.print(i.getStart() + "-" + i.getStop() + "|");
		// }
		// System.out.println();
		// System.out.println(g.getLongestTranscriptLength());

		AnnotationMapper annotationMapper = new AnnotationMapper(args[0], args[1]);
		LinkedList<AnnotationMapItem> anntoationsToRun = annotationMapper.getAnnotations();

		for (AnnotationMapItem item : anntoationsToRun) {

			long time = System.currentTimeMillis();
			GenomeAnnotation ga = GTFParser.readGtfFile(item.getId(), item.getFullPath());
			time = System.currentTimeMillis() - time;
			System.out.println(ga.getName() + "\ttime needed: " + time / 1000 + "s");
			for (Chromosome c : ga.getChromosomes().values())
				System.out.println("chromosome " + c.getID() + "\t#genes: " + c.getGenes().size());

			UnionTranscripts unionTrs = new UnionTranscripts(ga, ga.getClass().getProtectionDomain().getCodeSource()
					.getLocation().toExternalForm().substring(5).replace("Runner.jar", "") + "output/");
			unionTrs.writeOccurencesToFile(unionTrs.calculateUnionTranscriptDistribution());
			System.out.println("plotting unionTranscriptDistribution");
			new RScriptCaller(ga.getClass().getProtectionDomain().getCodeSource().getLocation().toExternalForm()
					.substring(5).replace("Runner.jar", "") + "UnionTranscriptPlotter.R", unionTrs.getOutputFile())
							.execRScript();
			new OverlappingGenes(ga, ga.getClass().getProtectionDomain().getCodeSource().getLocation().toExternalForm()
					.substring(5).replace("Runner.jar", "") + "output/").writeOverlappingGenesToFile();
		}

	}

}
