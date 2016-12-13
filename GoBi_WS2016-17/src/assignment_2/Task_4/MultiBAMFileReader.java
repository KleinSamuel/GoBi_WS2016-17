package assignment_2.Task_4;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

import debugStuff.DebugMessageFactory;
import genomeAnnotation.GenomeAnnotation;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;
import io.ConfigReader;
import io.ExternalFileWriter;
import io.HeadedFileReader;
import io.HeadedFileReader.LineObject;
import javafx.util.Pair;
import plotting.BarPlot;
import reader.GTFParser;

public class MultiBAMFileReader {

	private SimulmappingInfoReader sminfoReader;
	// contains one stat object per bamfile
	private ArrayList<BAMFileStats> bamsStats;
	// stores the next readPair for each bam file
	private ArrayList<BamFileReader> samReaders;
	private GenomeAnnotation ga;

	public MultiBAMFileReader(String bamsListFile, String mainDirectory, String mappingFile, String gtfpath) {
		ga = GTFParser.readGtfFile("h.ens.75", gtfpath);
		HeadedFileReader hfr = new HeadedFileReader(bamsListFile, "\t");
		hfr.readHeadedFile();
		bamsStats = new ArrayList<>(hfr.getLineObjects().size());
		samReaders = new ArrayList<>(hfr.getLineObjects().size());

		// read and parse bams.list
		for (LineObject line : hfr.getLineObjects()) {
			bamsStats.add(new BAMFileStats(line.getValue("name")));
			samReaders.add(new BamFileReader(mainDirectory + line.getValue("relative_path"), line.getValue("name"),
					line.getValue("transcriptome").equals("true"), line.getValue("convert_to_genomic").equals("true")));
		}

		ArrayList<Object> nextSimulmappingLine = null;
		sminfoReader = new SimulmappingInfoReader(mappingFile);
		int readIdOfInterest, currentId, counter = 0;
		ReadPair nextPair = null;
		DebugMessageFactory.printInfoDebugMessage(ConfigReader.DEBUG_MODE, "currentReadId\treadPairsFound");
		while ((nextSimulmappingLine = sminfoReader.readNextLine()) != null) {
			readIdOfInterest = (Integer) nextSimulmappingLine.get(0);
			if (readIdOfInterest % 100000 == 0) {
				DebugMessageFactory.printInfoDebugMessage(ConfigReader.DEBUG_MODE, readIdOfInterest + "\t" + counter);
			}
			for (int i = 0; i < samReaders.size(); i++) {
				nextPair = samReaders.get(i).getNextPair();
				if (nextPair != null) {
					currentId = nextPair.getId();
					if (currentId == readIdOfInterest) {
						nextPair.getOKstat(nextSimulmappingLine, samReaders.get(i).mappedToTranscriptome(),
								samReaders.get(i).convert_to_genomic, ga);
						bamsStats.get(i).addStatsOf(nextPair);
						samReaders.get(i).calcNextPair();
						counter++;
					} else {
						if (currentId < readIdOfInterest) {
							DebugMessageFactory.printErrorDebugMessage(ConfigReader.DEBUG_MODE,
									samReaders.get(i).getName() + ":" + currentId + "-" + readIdOfInterest);
							System.exit(1);
						}
					}
				}
			}
		}
		sminfoReader.close();

		ExternalFileWriter efw = new ExternalFileWriter();
		efw.openWriter(ConfigReader.readConfig().get("output_directory") + "bamFileStats.table");
		efw.writeToWriter(BAMFileStats.getHeader() + "\n");
		for (BAMFileStats bamStat : bamsStats) {

			efw.writeToWriter(bamStat.getStatsAsString() + "\n");

			Vector<Object> key = new Vector<>(); // h�he
			key.add(bamStat.getOk());
			key.add(bamStat.getPartial());
			key.add(bamStat.getEverthingElse());
			key.add(bamStat.getWrongChr());
			Vector<Object> value = new Vector<>(); // yname
			value.add("ok");
			value.add("partial");
			value.add("else");
			value.add("wrong-chr");
			Pair<Vector<Object>, Vector<Object>> pair = new Pair<Vector<Object>, Vector<Object>>(key, value);
			BarPlot bp = new BarPlot(pair, bamStat.getName(), "mappings", "readpairs", false);
			bp.filename = bamStat.getName() + "_mappingInfos";
			bp.plot();
		}
		efw.closeWriter();
	}

	public static class BamFileReader {
		private SamReader sr = null;
		private SAMRecordIterator it;
		private ReadPair nextReadPair;
		private String name;
		private boolean transcriptome, convert_to_genomic;
		private SAMRecord lastRecord = null;

		public BamFileReader(String BAMfilepath, String name, boolean transcriptome, boolean convert_to_genomic) {
			SamReaderFactory.setDefaultValidationStringency(ValidationStringency.SILENT);
			sr = SamReaderFactory.makeDefault().open(new File(BAMfilepath));
			it = sr.iterator();
			this.name = name;
			this.transcriptome = transcriptome;
			this.convert_to_genomic = convert_to_genomic;
			calcNextPair();
		}

		LinkedList<SAMRecord> recordsWithSameId;

		public void calcNextPair() {
			if (!it.hasNext()) {
				nextReadPair = null;
				lastRecord = null;
				return;
			}
			SAMRecord next = null;
			ReadPair rp = null;

			recordsWithSameId = new LinkedList<>();
			if (lastRecord != null) {
				recordsWithSameId.add(lastRecord);
			}
			while (it.hasNext()) {
				next = it.next();
				if (lastRecord == null) {
					lastRecord = next;
				}
				if (next.getReadName().equals(lastRecord.getReadName())) {
					recordsWithSameId.add(next);
				} else {
					lastRecord = next;
					break;
				}
			}
			rp = getValidPair(recordsWithSameId);
			nextReadPair = rp;
			if (rp == null) {
				calcNextPair();
			}
		}

		public ReadPair getValidPair(LinkedList<SAMRecord> records) {
			if (records.size() < 2)
				return null;
			for (SAMRecord s1 : records) {
				if (validRecord(s1)) {
					for (SAMRecord s2 : records) {
						if (validRecord(s2)) {
							if (validMate(s1, s2)) {
								return new ReadPair(s1, s2);
							}
							if (validMate(s2, s1)) {
								return new ReadPair(s2, s1);
							}
						}
					}
				}
			}
			return null;
		}

		/**
		 * calculates nextPair while nextPairId < readIdOfInterest returns
		 * current readPair if readIdOfInterest == -1
		 * 
		 * @param readIdOfInterest
		 * @return
		 */
		public ReadPair getNextPair() {
			return nextReadPair;
			// if (readIdOfInterest == -1) {
			// return nextReadPair;
			// } else {
			// while (Integer.parseInt(nextReadPair.getId()) < readIdOfInterest)
			// {
			// calcNextPair();
			// if (nextReadPair == null) {
			// return null;
			// }
			// }
			// }
			// return nextReadPair;
		}

		public String getName() {
			return name;
		}

		public boolean validRecord(SAMRecord s) {
			return (!s.getReadUnmappedFlag() && !s.getMateUnmappedFlag() && !s.getNotPrimaryAlignmentFlag());
		}

		public boolean validMate(SAMRecord sam, SAMRecord mate) {
			if (sam.getFirstOfPairFlag() && mate.getSecondOfPairFlag()) {
				if (sam.getAlignmentStart() == mate.getMateAlignmentStart()) {
					return true;
				}
			}
			return false;
		}

		public boolean mappedToTranscriptome() {
			return transcriptome;
		}

		public boolean convert_to_genomic() {
			return convert_to_genomic;
		}

	}

}