package assignment_2.Task_4;

import java.util.ArrayList;
import java.util.Vector;

import io.ExternalFileReader;
import util.Interval;

public class SimulmappingInfoReader {

	private ExternalFileReader efr;
	private String nextLine;
	private String[] header;
	private String[] split;
	private ArrayList<Object> retArray;

	public SimulmappingInfoReader(String mappingfile) {
		efr = new ExternalFileReader();
		efr.openReader(mappingfile);
		readHeader();
	}

	public void close() {
		efr.closeReader();
	}

	public void readHeader() {
		header = efr.readNextLine().split("\t");
	}

	public ArrayList<Object> readNextLine() {
		nextLine = efr.readNextLine();
		if (nextLine == null || nextLine.isEmpty()) {
			return null;
		}
		split = nextLine.split("\t", -1);
		retArray = new ArrayList<>();
		retArray.add(Integer.parseInt(split[0])); // readId
		retArray.add(split[1]); // chrId
		retArray.add(split[2]); // geneId
		retArray.add(split[3]); // trId
		retArray.add(Interval.parseInterval(split[4], 0, -1)); // tr_fw
		retArray.add(Interval.parseInterval(split[5], 0, -1)); // tr_rw
		Vector<Interval> fw_regvec = new Vector<>(), rw_regvec = new Vector<>();
		for (String s : split[6].split("\\|")) { // genomic_fw
			fw_regvec.add(Interval.parseInterval(s, -1, -2));
		}
		retArray.add(fw_regvec);
		for (String s : split[7].split("\\|")) { // genomic_rw
			rw_regvec.add(Interval.parseInterval(s, -1, -2));
		}
		retArray.add(rw_regvec);
		Vector<Integer> fw_mut = new Vector<>(), rw_mut = new Vector<>(), ufw_mut = new Vector<>(),
				urw_mut = new Vector<>();
		if (!split[8].isEmpty()) {
			for (String s : split[8].split(",")) {
				fw_mut.add(Integer.parseInt(s));
			}
		}
		if (!split[9].isEmpty()) {
			for (String s : split[9].split(",")) {
				rw_mut.add(Integer.parseInt(s));
			}
		}
		if (!split[10].isEmpty()) {
			for (String s : split[10].split(",")) {
				ufw_mut.add(Integer.parseInt(s));
			}
		}
		if (!split[11].isEmpty()) {
			for (String s : split[11].split(",")) {
				urw_mut.add(Integer.parseInt(s));
			}
		}
		retArray.add(fw_mut);
		retArray.add(rw_mut);
		retArray.add(ufw_mut);
		retArray.add(urw_mut);
		return retArray;
	}

}