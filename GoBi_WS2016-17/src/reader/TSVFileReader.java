package reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import gnu.trove.map.hash.THashMap;

public class TSVFileReader {

	public THashMap<String, String> readSimpleTSVFile(String filePath, String separator, boolean valueKey) {
		THashMap<String, String> ret = new THashMap<>();
		try {
			String line = null;
			String[] split = null;
			BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
			while ((line = br.readLine()) != null) {
				if (line.startsWith("#"))
					continue;
				split = line.split(separator);
				if (valueKey)
					ret.put(split[1], split[0]);
				else
					ret.put(split[0], split[1]);
			}
			br.close();
		} catch (IOException e) {
			System.out.println("something went wrong while reading file: " + filePath);
			e.printStackTrace();
		}
		return ret;
	}

}
