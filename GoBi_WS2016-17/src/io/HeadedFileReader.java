package io;

import java.util.ArrayList;
import java.util.HashMap;

import debugStuff.DebugMessageFactory;

public class HeadedFileReader {

	private ArrayList<LineObject> lineObjects;
	private String delimiter;
	private String filepath;
	private ArrayList<String> header = null;

	public HeadedFileReader(String filepath, String delimiter) {
		this.filepath = filepath;
		this.delimiter = delimiter;
	}

	/**
	 * sets the list of lineObjects of headedFileReader one lineObject per line
	 * 
	 * @param filepath
	 * @param delimiter
	 * @return
	 */
	public void readHeadedFile() {
		lineObjects = new ArrayList<>();
		String nextLine = null;
		String[] header = null;
		ExternalFileReader efr = new ExternalFileReader();
		efr.openReader(filepath);
		while ((nextLine = efr.readNextLine()) != null && !nextLine.isEmpty()) {
			if (header == null) {
				header = nextLine.split(delimiter);
				this.header = new ArrayList<>(header.length);
				for (String s : header) {
					this.header.add(s);
				}
			} else {
				lineObjects.add(new LineObject(header, nextLine.split(delimiter)));
			}
		}
		efr.closeReader();
	}

	public ArrayList<LineObject> getLineObjects() {
		return lineObjects;
	}

	public ArrayList<String> getHeader() {
		return header;
	}

	public String getFilepath() {
		return filepath;
	}

	public String getDelimiter() {
		return delimiter;
	}

	/**
	 * contains a hashmap<String, String> containing the value for every key of
	 * the header
	 * 
	 * @author Dennis
	 *
	 */
	public static class LineObject {

		HashMap<String, String> keyValuePairs;

		public LineObject(String[] header, String[] line) {
			if (header.length != line.length) {
				DebugMessageFactory.printErrorDebugMessage(ConfigReader.DEBUG_MODE,
						"Error in LineObject: headerLength != lineLength");
			}
			keyValuePairs = new HashMap<>();
			for (int i = 0; i < header.length; i++) {
				keyValuePairs.put(header[i], line[i]);
			}
		}

		public String getValue(String key) {
			return keyValuePairs.get(key);
		}

		public HashMap<String, String> getPairs() {
			return keyValuePairs;
		}

	}

}
