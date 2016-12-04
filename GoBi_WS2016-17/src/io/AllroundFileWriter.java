package io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

import debugStuff.DebugMessageFactory;
import fileFormats.FASTQElement;
import javafx.util.Pair;
import plotting.Base64Factory;

public class AllroundFileWriter {

	public static void writeVector(Vector<Object> vector, File file) {
		writeVector(vector, file, false);
	}

	public static void writeVector(Vector<Object> vector, File file, boolean append) {

		String convertedVector = "";

		for (Object d : vector) {

			if (d instanceof String) {
				convertedVector += "\"" + String.valueOf(d) + "\" ";
			} else {
				convertedVector += String.valueOf(d) + " ";
			}
		}

		if (convertedVector.length() > 0) {
			convertedVector = convertedVector.substring(0, convertedVector.length() - 1);
		}

		try {

			BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));

			if (append) {
				bw.newLine();
				bw.append(convertedVector);
			} else {
				bw.write(convertedVector);
			}

			bw.flush();
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeSimpleFile(String filepath, HashSet<String> set) {

		try {

			BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));

			for (String s : set) {
				bw.write(s);
				bw.newLine();
			}

			bw.flush();
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		DebugMessageFactory.printNormalDebugMessage(ConfigReader.DEBUG_MODE, "File written.");
	}

	public static void writeSimpleFile(String filepath, ArrayList<String> list) {

		try {

			BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));

			for (String s : list) {
				bw.write(s);
				bw.newLine();
			}

			bw.flush();
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		DebugMessageFactory.printNormalDebugMessage(ConfigReader.DEBUG_MODE, "File written.");
	}

	public static void createHTMLforPlots(String filepath, ArrayList<String> plotPaths,
			ArrayList<ArrayList<String[]>> info, boolean includeBase64) {

		try {

			BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));

			bw.write("<!DOCTYPE html>\n");
			bw.write("<html>\n");
			bw.write("<body>\n");

			int counter = 0;

			for (String fp : plotPaths) {

				bw.write("<figure>\n");

				if (includeBase64) {
					String base64 = Base64Factory.encodeByteArray64(Base64Factory.imageToByteArray(fp + ".png"));
					bw.write("<img width=\"1000\" height=\"1000\" src=\"data:image/png;base64," + base64 + "\">\n");
				} else {
					bw.write("<img src=\"" + fp + ".png\" width=\"1000\" height=\"1000\">\n");
				}

				bw.write("</figure>\n");

				if (info != null) {
					bw.write("<ul>\n");

					for (String[] list : info.get(counter)) {
						bw.write("<li>\n");
						bw.write("<a href=\"" + list[0] + "\">" + list[1] + "</a>\n");
						bw.write("</li>\n");
					}

					bw.write("</ul>\n");
				}
				counter++;
			}

			bw.write("</body>\n");
			bw.write("</html>");

			bw.flush();
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Write a FASTQ file. Used in Task 2 from Assignment 2. Doc:
	 * https://en.wikipedia.org/wiki/FASTQ_format
	 * 
	 * @param filePath
	 */
	public static void writeFASTQ(String dirPath, Vector<Pair<FASTQElement, FASTQElement>> fastqElements) {

		try {

			BufferedWriter bwForward = new BufferedWriter(new FileWriter(dirPath + "fw.fastq"));
			BufferedWriter bwReverse = new BufferedWriter(new FileWriter(dirPath + "rw.fastq"));

			for (Pair<FASTQElement, FASTQElement> entry : fastqElements) {

				/* header */
				bwForward.write(entry.getKey().getHeader());
				bwForward.newLine();
				bwReverse.write(entry.getValue().getHeader());
				bwReverse.newLine();
				/* sequence */
				bwForward.write(entry.getKey().getSequence());
				bwForward.newLine();
				bwReverse.write(entry.getValue().getSequence());
				bwReverse.newLine();
				/* separator */
				bwForward.write("+\n");
				bwReverse.write("+\n");
				/* quality scores */
				bwForward.write(entry.getKey().getQualityScores());
				bwForward.newLine();
				bwReverse.write(entry.getValue().getQualityScores());
				bwReverse.newLine();

			}

			bwForward.flush();
			bwForward.close();
			bwReverse.flush();
			bwReverse.close();

		} catch (IOException e) {
			DebugMessageFactory.printErrorDebugMessage(ConfigReader.DEBUG_MODE, "Could not write files to: " + dirPath);
			e.printStackTrace();
		}
		DebugMessageFactory.printNormalDebugMessage(ConfigReader.DEBUG_MODE,
				"FASTQ files successfully written to: " + dirPath);
	}

	public static void writeXMLForTask1(String filepath, TreeMap<String, HashMap<String, Integer>> map) {

		try {

			BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));

			bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
			bw.newLine();

			for (Entry<String, HashMap<String, Integer>> entry : map.entrySet()) {

				bw.write("<entry>");
				bw.newLine();
				bw.write("\t<biotype>" + entry.getKey() + "</biotype>");
				bw.newLine();
				bw.write("\t<subentry>");
				bw.newLine();

				for (Entry<String, Integer> entry2 : entry.getValue().entrySet()) {

					bw.write("\t\t<organism>" + entry2.getKey() + "</organism>");
					bw.newLine();
					bw.write("\t\t<amountGenes>" + entry2.getValue() + "</amountGenes>");
					bw.newLine();
				}

				bw.write("\t</subentry>");
				bw.newLine();
				bw.write("</entry>");
				bw.newLine();

			}

			bw.flush();
			bw.close();

			DebugMessageFactory.printNormalDebugMessage(ConfigReader.DEBUG_MODE,
					"FILE SUCCESSFULLY WRITTEN TO " + filepath);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
