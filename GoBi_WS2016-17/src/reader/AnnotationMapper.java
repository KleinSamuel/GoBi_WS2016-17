package reader;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;


public class AnnotationMapper {

	LinkedList<AnnotationMapItem> items;

	public AnnotationMapper(String annotMapPath, String GTFpathsFilePath) {
		items = new LinkedList<>();
		HashMap<String, String> annotMap = new TSVFileReader().readSimpleTSVFile(annotMapPath, "\t", true);
		HashMap<String, String> gtfPaths = new TSVFileReader().readSimpleTSVFile(GTFpathsFilePath, "\t", false);
		
		String fullPath;
		for (Entry<String, String> annotEntry : annotMap.entrySet()) {
			fullPath = gtfPaths.get(annotEntry.getKey());
			if (fullPath == null)
				throw new RuntimeException("there is no fullPath for annotationEntry " + annotEntry.getKey() + " "
						+ annotEntry.getValue());
			else {
				AnnotationMapItem item = new AnnotationMapItem(annotEntry.getValue(), annotEntry.getKey(), fullPath);
				items.add(item);
				System.out.println("annotationMapper added " + item.toString());
			}
		}

	}

	public LinkedList<AnnotationMapItem> getAnnotations() {
		return items;
	}

	public class AnnotationMapItem {
		private String id, shortPath, fullPath;

		public AnnotationMapItem(String id, String shortPath, String fullPath) {
			this.id = id;
			this.shortPath = shortPath;
			this.fullPath = fullPath;
		}

		public String getId() {
			return id;
		}

		public String getShortPath() {
			return shortPath;
		}

		public String getFullPath() {
			return fullPath;
		}

		@Override
		public String toString() {
			return "annotationMapItem: id = " + this.getId() + " shortPath = " + this.getShortPath() + " fullPath = "
					+ this.getFullPath();
		}

	}
}
