package assignment_2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import debugStuff.DebugMessageFactory;
import genomeAnnotation.GenomeAnnotation;
import io.ConfigHelper;
import io.ConfigReader;

public class SequenceExtractionComparator {

	private String referenceFileGZIP, fasta, errorLogFile;
	private GenomeSequenceExtractor gse;

	public SequenceExtractionComparator(String referenceFileGZIP, String fasta, String offsetFile, String errorLogFile,
			GenomeAnnotation ga) {
		this.referenceFileGZIP = referenceFileGZIP;
		this.fasta = fasta;
		this.errorLogFile = errorLogFile;
		gse = new GenomeSequenceExtractor(offsetFile, fasta, ga);
	}

	public int compareToRef() {
		BufferedReader referenceReader = null;
		FileWriter errorLog = null;
		int errors = 0, comparisonCounter = 0;
		try {
			referenceReader = new BufferedReader(
					new InputStreamReader(new GZIPInputStream(new FileInputStream(new File(referenceFileGZIP)))));
			errorLog = new FileWriter(new File(errorLogFile));
			String line = null, extractedSeq = null;
			StringBuilder referenceSeq = null;
			CDNAHeaderObject header = null;

			while ((line = referenceReader.readLine()) != null) {
				if (line.startsWith(">")) {
					if (header == null) {
						header = new CDNAHeaderObject(line);
						referenceSeq = new StringBuilder();
						if (!gse.isChromosomeToBeAnalyzed(header.getChrId())) {
							header = null;
						}
						continue;
					}
					extractedSeq = gse.getCDNASequenceInInterval(header.getChrId(), header.getGeneId(),
							header.getTrId(), (int) header.getStart(), (int) header.getStop(),
							header.isOnNegativeStrand());
					if (extractedSeq == null) {
						DebugMessageFactory.printInfoDebugMessage(ConfigReader.DEBUG_MODE, "extracted seq == null!");
						System.exit(1);
					}
					if (!extractedSeq.equals(referenceSeq.toString())) {
						errors++;
						errorLog.write(line + "\n");
						errorLog.write("ref:" + referenceSeq.toString() + "\n");
						errorLog.write("gse:" + extractedSeq + "\n");
					}
					comparisonCounter++;
					if (comparisonCounter % 1000 == 0) {
						System.out.println("compared " + comparisonCounter + " so far! errors: " + errors);
					}
					header = new CDNAHeaderObject(line);
					referenceSeq = new StringBuilder();
					if (!gse.isChromosomeToBeAnalyzed(header.getChrId()))
						header = null;
				} else {
					if (header != null)
						referenceSeq.append(line);
				}
			}
			if (header != null) {
				extractedSeq = gse.getCDNASequenceInInterval(header.getChrId(), header.getGeneId(), header.getTrId(),
						(int) header.getStart(), (int) header.getStop(), header.isOnNegativeStrand());
				if (!extractedSeq.equals(referenceSeq.toString())) {
					errors++;
					errorLog.write(line + "\n");
					errorLog.write("ref:" + referenceSeq.toString() + "\n");
					errorLog.write("gse:" + extractedSeq + "\n");
				}
			}
			return errors;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				referenceReader.close();
				errorLog.flush();
				errorLog.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return errors;
	}

}
