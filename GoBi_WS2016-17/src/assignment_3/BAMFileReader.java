package assignment_3;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import debugStuff.DebugMessageFactory;
import genomeAnnotation.GenomeAnnotation;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;
import io.ConfigReader;
import reader.GTFParser;

public class BAMFileReader {

	// readId, waitingRead
	private HashMap<String, SAMRecord> waitingRecords;
	private String bamFile;
	public static GenomeAnnotation ga;

	public BAMFileReader(String bamPath, String gtfPath) {
		bamFile = bamPath;
		waitingRecords = new HashMap<>();
		ga = GTFParser.readGtfFile("h.ens.75", gtfPath);
	}

	public void readBAMFile() {
		SamReaderFactory.setDefaultValidationStringency(ValidationStringency.SILENT);
		SamReader sr = SamReaderFactory.makeDefault().open(new File(bamFile));
		Iterator<SAMRecord> it = sr.iterator();
		SAMRecord sam = null, possibleMate = null;
		// reads are sorted by start --> so if new chromosome clear map
		String chromId = null;
		ReadPair rp = null;
		int validRecords = 0, validPairs = 0, invalidRecords = 0, nonValidPairs = 0, checkedRecords = 0;
		int splitInconsistent = 0, wrong = 0;
		while (it.hasNext()) {
			sam = it.next();
			checkedRecords++;
			if (validRecord(sam)) {
				validRecords++;
				// check if new chromosome
				if (chromId == null) {
					chromId = sam.getReferenceName();
				} else {
					if (!sam.getReferenceName().equals(chromId)) {
						waitingRecords = new HashMap<>();
					}
				}
				// look for waiting record in map
				possibleMate = waitingRecords.get(sam.getReadName());
				if (possibleMate == null) {
					waitingRecords.put(sam.getReadName(), sam);
				} else {
					// check if valid pair --> but what to do if not --> both
					// reads valid?? possible??
					rp = validPair(sam, possibleMate);
					if (rp == null) {
						rp = validPair(possibleMate, sam);
					}
					if (rp == null) {
						nonValidPairs++;
						continue;
					} else {
						waitingRecords.remove(sam.getReadName());
						validPairs++;
						// if (rp.getSplitCount() < 0) {
						// splitInconsistent++;
						// } else if (!rp.checkIfOutputEqualsRef()) {
						// wrong++;
						// }
					}
				}
			} else {
				invalidRecords++;
			}
			if (checkedRecords % 100000 == 0) {
				DebugMessageFactory.printInfoDebugMessage(ConfigReader.DEBUG_MODE,
						"checkedRecords: " + checkedRecords + "\tvalidRecords: " + validRecords + "\tinvalidRecords: "
								+ invalidRecords + "\tvalidPairs: " + validPairs + "\tnonValidPairs: " + nonValidPairs
								+ "\tinconsistent: " + splitInconsistent + "\twrong: " + wrong);
			}
		}
		DebugMessageFactory.printInfoDebugMessage(ConfigReader.DEBUG_MODE,
				"checkedRecords: " + checkedRecords + "\tvalidRecords: " + validRecords + "\tinvalidRecords: "
						+ invalidRecords + "\tvalidPairs: " + validPairs + "\tnonValidPairs: " + nonValidPairs
						+ "\tinconsistent: " + splitInconsistent + "\twrong: " + wrong);
		DebugMessageFactory.printInfoDebugMessage(ConfigReader.DEBUG_MODE, "Finished reading");

	}

	public boolean validRecord(SAMRecord sam) {
		return (!sam.getReadUnmappedFlag() && !sam.getMateUnmappedFlag() && !sam.getNotPrimaryAlignmentFlag()
				&& sam.getReferenceName().equals(sam.getMateReferenceName())
				&& sam.getReadNegativeStrandFlag() != sam.getMateNegativeStrandFlag());
	}

	public ReadPair validPair(SAMRecord first, SAMRecord second) {
		if (!first.getReferenceName().equals(second.getReferenceName()))
			return null;
		if (first.getFirstOfPairFlag() && second.getSecondOfPairFlag()
				&& first.getAlignmentStart() == second.getMateAlignmentStart()
				&& first.getMateAlignmentStart() == second.getAlignmentStart()) {
			return new ReadPair(first, second, false);
		}
		return null;
	}

}
