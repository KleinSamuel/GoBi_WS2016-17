package samfiles;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;

import debugStuff.DebugMessageFactory;
import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;
import io.AllroundFileWriter;
import io.ConfigHelper;
import io.ConfigReader;
import io.ExternalFileReader;
import io.ExternalFileWriter;
import javafx.util.Pair;
import plotting.BarPlot;

public class SamFileComparator {

	/* info for plots */
	int okMappedFW;
	int partialMappedFW;
	int wrongChrMappedFW;
	int elseMappedFW;
	int okMappedRW;
	int partialMappedRW;
	int wrongChrMappedRW;
	int elseMappedRW;
	int okMappedBOTH;
	int partialMappedBOTH;
	int wrongChrMappedBOTH;
	int elseMappedBOTH;
	
	HashMap<String,HashMap<TASK_2_CATEGORY, Integer>> mapFW;
	HashMap<String,HashMap<TASK_2_CATEGORY, Integer>> mapRW;
	HashMap<String,HashMap<TASK_2_CATEGORY, Integer>> mapBOTH;
	
	HashMap<Integer, TASK_2_CATEGORY> taskMap;
	
	ArrayList<Integer> refStartFW;
	ArrayList<Integer> refStopFW;
	ArrayList<Integer> refStartRW;
	ArrayList<Integer> refStopRW;
	
	int refNumber;
	String refChromsomeID;
	boolean refIsNegative;

	HashMap<Integer, Object[]> simulMap;

	public SamFileComparator() {
		okMappedFW = 0;
		partialMappedFW = 0;
		wrongChrMappedFW = 0;
		elseMappedFW = 0;
		okMappedRW = 0;
		partialMappedRW = 0;
		wrongChrMappedRW = 0;
		elseMappedRW = 0;
		okMappedBOTH = 0;
		partialMappedBOTH = 0;
		wrongChrMappedBOTH = 0;
		elseMappedBOTH = 0;

		this.simulMap = new HashMap<>();
		this.mapFW = new HashMap<>();
		mapFW.put("ok", new HashMap<>());
		mapFW.put("part", new HashMap<>());
		mapFW.put("wrongChr", new HashMap<>());
		mapFW.put("other", new HashMap<>());
		this.mapRW = new HashMap<>();
		mapRW.put("ok", new HashMap<>());
		mapRW.put("part", new HashMap<>());
		mapRW.put("wrongChr", new HashMap<>());
		mapRW.put("other", new HashMap<>());
		this.mapBOTH = new HashMap<>();
		mapBOTH.put("ok", new HashMap<>());
		mapBOTH.put("part", new HashMap<>());
		mapBOTH.put("wrongChr", new HashMap<>());
		mapBOTH.put("other", new HashMap<>());
		
		this.taskMap = new HashMap<>();
		taskMap.put(1, TASK_2_CATEGORY.ALL);
		taskMap.put(2, TASK_2_CATEGORY.UNSPLICED);
		taskMap.put(3, TASK_2_CATEGORY.UNSPLICED_NO_MM);
		taskMap.put(4, TASK_2_CATEGORY.SPLICED);
		taskMap.put(5, TASK_2_CATEGORY.SPLICED_NO_MM);
		taskMap.put(6, TASK_2_CATEGORY.SPLICED_NO_MM_GT5BP);
	}

	public void compareSamFileToSimulmapping(String[] args) {
		
		DebugMessageFactory.printInfoDebugMessage(ConfigReader.DEBUG_MODE, "Started comparing SAM/BAM file to simulmapping.info (may take a while)..");

		ExternalFileReader extFR = new ExternalFileReader();
		extFR.openReader(args[1]);
		String l = null;
		extFR.readNextLine();
		while ((l = extFR.readNextLine()) != null) {
			parseInfoLine(l,6,7);
		}
		extFR.closeReader();
		
		SamReaderFactory.setDefaultValidationStringency(ValidationStringency.SILENT);
		SamReader sr = SamReaderFactory.makeDefault().open(new File(args[0]));
		
		SAMRecordIterator iterator = sr.iterator();
		SAMRecord rec = null;

		int start, stop;
		int readNumber;
		String chromsomeID;

		while (iterator.hasNext()) {
			rec = iterator.next();
			
			if(!validRecord(rec)){
				rec = iterator.next();
				continue;
			}

			List<AlignmentBlock> blocks = rec.getAlignmentBlocks();
			
			readNumber = Integer.parseInt(rec.getReadName());
			chromsomeID = rec.getReferenceName();
			
			refChromsomeID = (String) simulMap.get(readNumber)[4];
			refStartFW = (ArrayList<Integer>) simulMap.get(readNumber)[0];
			refStopFW = (ArrayList<Integer>) simulMap.get(readNumber)[1];
			refStartRW = (ArrayList<Integer>) simulMap.get(readNumber)[2];
			refStopRW = (ArrayList<Integer>) simulMap.get(readNumber)[3];

			boolean okFW = false;
			boolean partFW = false;
			boolean wrongCHRFW = false;
			boolean otherFW = false;
			
			int catNum = (int)simulMap.get(readNumber)[5];
			
			if(refChromsomeID.equals(chromsomeID)){
				
				if(refStartFW.size() == blocks.size() && refStopFW.size() == blocks.size()){
					
					for (int i = 0; i < blocks.size(); i++) {
						AlignmentBlock block = blocks.get(i);
						
						start = block.getReferenceStart();
						stop = block.getReferenceStart()+block.getLength();
						
						/* mapped correct */
						if(start == refStartFW.get(i) && stop == refStopFW.get(i)){
							okFW = true;
						}
						/* mapped partially */
						else if(start >= refStartFW.get(i) && stop <= refStopFW.get(i)){
							partFW= true;
						}
						/* mapped wrongly */
						else{
							otherFW = true;
							break;
						}
					}
				}else{
					otherFW = true;
				}
				

				if(otherFW){
					elseMappedFW++;
					putInHashMapFW(catNum, "other");
				}else if(partFW){
					partialMappedFW++;
					putInHashMapFW(catNum, "part");
				}else if(okFW){
					okMappedFW++;
					putInHashMapFW(catNum, "ok");
				}
				
			}
			/* read is mapped to wrong chromosome */
			else{
				wrongChrMappedFW++;
				putInHashMapFW(catNum, "wrongChr");
				wrongCHRFW = true;
			}
			
			rec = iterator.next();
			
			blocks = rec.getAlignmentBlocks();
			
			readNumber = Integer.parseInt(rec.getReadName());
			chromsomeID = rec.getReferenceName();
			
			catNum = (int)simulMap.get(readNumber)[5];

			boolean okRW = false;
			boolean partRW = false;
			boolean wrongCHRRW = false;
			boolean otherRW = false;
			
			if(refChromsomeID.equals(chromsomeID)){
				
				if(refStartRW.size() == blocks.size() && refStopRW.size() == blocks.size()){
					
					for (int i = 0; i < blocks.size(); i++) {
						AlignmentBlock block = blocks.get(i);
						
						start = block.getReferenceStart();
						stop = block.getReferenceStart()+block.getLength();
						
						/* mapped correct */
						if(start == refStartRW.get(i) && stop == refStopRW.get(i)){
							okRW = true;
						}
						/* mapped partially */
						else if(start >= refStartRW.get(i) && stop <= refStopRW.get(i)){
							partRW = true;
						}
						/* mapped wrongly */
						else{
							otherRW = true;
							break;
						}
					}
				}else{
					otherRW = true;
				}
				
				if(otherRW){
					elseMappedRW++;
					putInHashMapRW(catNum, "other");
				}else if(partRW){
					partialMappedRW++;
					putInHashMapRW(catNum, "part");
				}else if(okRW){
					okMappedRW++;
					putInHashMapRW(catNum, "ok");
				}
				
			}
			/* read is mapped to wrong chromosome */
			else{
				wrongChrMappedRW++;
				putInHashMapRW(catNum, "wrongChr");
				wrongCHRRW = true;
			}
			
			if(otherFW && otherRW){
				elseMappedBOTH++;
			}else if(wrongCHRFW && wrongCHRRW){
				wrongChrMappedBOTH++;
			}else if(partFW && partRW || okFW && partRW || partFW && okRW){
				partialMappedBOTH++;
			}else if(okFW && okRW){
				okMappedBOTH++;
			}else{
				elseMappedBOTH++;
			}

		}
		
		DebugMessageFactory.printInfoDebugMessage(ConfigReader.DEBUG_MODE, "Finished comparing SAM/BAM file to simulmapping.info.");

		ArrayList<String> plotPaths = new ArrayList<>();
		
		ExternalFileWriter extFW = new ExternalFileWriter();
		extFW.openWriter(new ConfigHelper().getDefaultOutputPath() + "readmapper_eval.txt");

		Vector<Object> vecKey = new Vector<>();
//		vecKey.add(okMappedFW);
//		vecKey.add(partialMappedFW);
//		vecKey.add(wrongChrMappedFW);
//		vecKey.add(elseMappedFW);
		Vector<Object> vecVal = new Vector<>();
//		vecVal.add("ok");
//		vecVal.add("partial");
//		vecVal.add("wrong-chr");
//		vecVal.add("everything else");
		Pair<Vector<Object>, Vector<Object>> pairVec = new Pair<Vector<Object>, Vector<Object>>(vecKey, vecVal);
		BarPlot bp = new BarPlot(pairVec, "eval_fw", "", "Amount", false);
//		bp.filename = "eval_fw";
//		bp.plot();
		
//		plotPaths.add(new ConfigHelper().getDefaultOutputPath()+bp.filename);
		
//		vecKey = new Vector<>();
//		vecKey.add(okMappedRW);
//		vecKey.add(partialMappedRW);
//		vecKey.add(wrongChrMappedRW);
//		vecKey.add(elseMappedRW);
//		pairVec = new Pair<Vector<Object>, Vector<Object>>(vecKey, vecVal);
//		bp = new BarPlot(pairVec, "eval_rw", "", "Amount", false);
//		bp.filename = "eval_rw";
//		bp.plot();
		
//		plotPaths.add(new ConfigHelper().getDefaultOutputPath()+bp.filename);
		
		vecKey = new Vector<>();
		vecKey.add(okMappedBOTH);
		vecKey.add(partialMappedBOTH);
		vecKey.add(wrongChrMappedBOTH);
		vecKey.add(elseMappedBOTH);
		pairVec = new Pair<Vector<Object>, Vector<Object>>(vecKey, vecVal);
		bp = new BarPlot(pairVec, "eval_both", "", "Amount", false);
		bp.filename = "eval_both";
		bp.plot();
		
		plotPaths.add(new ConfigHelper().getDefaultOutputPath()+bp.filename);
		
		vecKey = new Vector<>();
		vecVal = new Vector<>();
		
		for(Entry<String, HashMap<TASK_2_CATEGORY,Integer>> entry : mapFW.entrySet()){
			for(Entry<TASK_2_CATEGORY,Integer> entry2 : entry.getValue().entrySet()){
				vecVal.add(entry.getKey()+" "+entry2.getKey().toString());
				vecKey.add(entry2.getValue());
			}
		}
		
		pairVec = new Pair<Vector<Object>, Vector<Object>>(vecKey, vecVal);
		bp = new BarPlot(pairVec, "eval_criteria_fw", "", "Amount", true);
		bp.filename = "eval_criteria_fw";
		bp.bottomMargin = 15;
		bp.plot();
		
		plotPaths.add(new ConfigHelper().getDefaultOutputPath()+bp.filename);
		
		vecKey = new Vector<>();
		vecVal = new Vector<>();
		
		for(Entry<String, HashMap<TASK_2_CATEGORY,Integer>> entry : mapRW.entrySet()){
			for(Entry<TASK_2_CATEGORY,Integer> entry2 : entry.getValue().entrySet()){
				vecVal.add(entry.getKey()+" "+entry2.getKey().toString());
				vecKey.add(entry2.getValue());
			}
		}
		
		pairVec = new Pair<Vector<Object>, Vector<Object>>(vecKey, vecVal);
		bp = new BarPlot(pairVec, "eval_criteria_rw", "", "Amount", true);
		bp.filename = "eval_criteria_rw";
		bp.bottomMargin = 15;
		bp.plot();
		
		plotPaths.add(new ConfigHelper().getDefaultOutputPath()+bp.filename);
		
		extFW.writeToWriter("" + okMappedFW);
		extFW.writeToWriter("\t" + partialMappedFW);
		extFW.writeToWriter("\t" + wrongChrMappedFW);
		extFW.writeToWriter("\t" + elseMappedFW);
		extFW.writeToWriter("\n" + okMappedRW);
		extFW.writeToWriter("\t" + partialMappedRW);
		extFW.writeToWriter("\t" + wrongChrMappedRW);
		extFW.writeToWriter("\t" + elseMappedRW);
		extFW.writeToWriter("\n" + okMappedBOTH);
		extFW.writeToWriter("\t" + partialMappedBOTH);
		extFW.writeToWriter("\t" + wrongChrMappedBOTH);
		extFW.writeToWriter("\t" + elseMappedBOTH + "\n");

		extFW.closeWriter();
		
		AllroundFileWriter.createHTMLforPlots(new ConfigHelper().getDefaultOutputPath()+"simple_eval.html", plotPaths, null, true);
	}
	
	private enum TASK_2_CATEGORY{
		ALL, UNSPLICED, UNSPLICED_NO_MM, SPLICED, SPLICED_NO_MM, SPLICED_NO_MM_GT5BP;
	}
	
	public void putInHashMapFW(int catNum, String curr){
		if(mapFW.get(curr).containsKey(TASK_2_CATEGORY.ALL)){
			mapFW.get(curr).put(TASK_2_CATEGORY.ALL, mapFW.get(curr).get(TASK_2_CATEGORY.ALL)+1);
		}else{
			mapFW.get(curr).put(TASK_2_CATEGORY.ALL, 1);
		}
		
		switch (catNum) {
		case 1:
			if(mapFW.get(curr).containsKey(TASK_2_CATEGORY.UNSPLICED)){
				mapFW.get(curr).put(TASK_2_CATEGORY.UNSPLICED, mapFW.get(curr).get(TASK_2_CATEGORY.UNSPLICED)+1);
			}else{
				mapFW.get(curr).put(TASK_2_CATEGORY.UNSPLICED, 1);
			}
			break;
		case 2:
			if(mapFW.get(curr).containsKey(TASK_2_CATEGORY.UNSPLICED)){
				mapFW.get(curr).put(TASK_2_CATEGORY.UNSPLICED, mapFW.get(curr).get(TASK_2_CATEGORY.UNSPLICED)+1);
			}else{
				mapFW.get(curr).put(TASK_2_CATEGORY.UNSPLICED, 1);
			}
			if(mapFW.get(curr).containsKey(TASK_2_CATEGORY.UNSPLICED_NO_MM)){
				mapFW.get(curr).put(TASK_2_CATEGORY.UNSPLICED_NO_MM, mapFW.get(curr).get(TASK_2_CATEGORY.UNSPLICED_NO_MM)+1);
			}else{
				mapFW.get(curr).put(TASK_2_CATEGORY.UNSPLICED_NO_MM, 1);
			}
			break;
		case 3:
			if(mapFW.get(curr).containsKey(TASK_2_CATEGORY.SPLICED)){
				mapFW.get(curr).put(TASK_2_CATEGORY.SPLICED, mapFW.get(curr).get(TASK_2_CATEGORY.SPLICED)+1);
			}else{
				mapFW.get(curr).put(TASK_2_CATEGORY.SPLICED, 1);
			}
			break;
		case 4:
			if(mapFW.get(curr).containsKey(TASK_2_CATEGORY.SPLICED)){
				mapFW.get(curr).put(TASK_2_CATEGORY.SPLICED, mapFW.get(curr).get(TASK_2_CATEGORY.SPLICED)+1);
			}else{
				mapFW.get(curr).put(TASK_2_CATEGORY.SPLICED, 1);
			}
			if(mapFW.get(curr).containsKey(TASK_2_CATEGORY.SPLICED_NO_MM)){
				mapFW.get(curr).put(TASK_2_CATEGORY.SPLICED_NO_MM, mapFW.get(curr).get(TASK_2_CATEGORY.SPLICED_NO_MM)+1);
			}else{
				mapFW.get(curr).put(TASK_2_CATEGORY.SPLICED_NO_MM, 1);
			}
			break;
		case 5:
			if(mapFW.get(curr).containsKey(TASK_2_CATEGORY.SPLICED)){
				mapFW.get(curr).put(TASK_2_CATEGORY.SPLICED, mapFW.get(curr).get(TASK_2_CATEGORY.SPLICED)+1);
			}else{
				mapFW.get(curr).put(TASK_2_CATEGORY.SPLICED, 1);
			}
			if(mapFW.get(curr).containsKey(TASK_2_CATEGORY.SPLICED_NO_MM)){
				mapFW.get(curr).put(TASK_2_CATEGORY.SPLICED_NO_MM, mapFW.get(curr).get(TASK_2_CATEGORY.SPLICED_NO_MM)+1);
			}else{
				mapFW.get(curr).put(TASK_2_CATEGORY.SPLICED_NO_MM, 1);
			}
			if(mapFW.get(curr).containsKey(TASK_2_CATEGORY.SPLICED_NO_MM_GT5BP)){
				mapFW.get(curr).put(TASK_2_CATEGORY.SPLICED_NO_MM_GT5BP, mapFW.get(curr).get(TASK_2_CATEGORY.SPLICED_NO_MM_GT5BP)+1);
			}else{
				mapFW.get(curr).put(TASK_2_CATEGORY.SPLICED_NO_MM_GT5BP, 1);
			}
			break;
		default:
			break;
		}
	}
	
	public void putInHashMapRW(int catNum, String curr){
		if(mapRW.get(curr).containsKey(TASK_2_CATEGORY.ALL)){
			mapRW.get(curr).put(TASK_2_CATEGORY.ALL, mapRW.get(curr).get(TASK_2_CATEGORY.ALL)+1);
		}else{
			mapRW.get(curr).put(TASK_2_CATEGORY.ALL, 1);
		}
		
		switch (catNum) {
		case 1:
			if(mapRW.get(curr).containsKey(TASK_2_CATEGORY.UNSPLICED)){
				mapRW.get(curr).put(TASK_2_CATEGORY.UNSPLICED, mapRW.get(curr).get(TASK_2_CATEGORY.UNSPLICED)+1);
			}else{
				mapRW.get(curr).put(TASK_2_CATEGORY.UNSPLICED, 1);
			}
			break;
		case 2:
			if(mapRW.get(curr).containsKey(TASK_2_CATEGORY.UNSPLICED)){
				mapRW.get(curr).put(TASK_2_CATEGORY.UNSPLICED, mapRW.get(curr).get(TASK_2_CATEGORY.UNSPLICED)+1);
			}else{
				mapRW.get(curr).put(TASK_2_CATEGORY.UNSPLICED, 1);
			}
			if(mapRW.get(curr).containsKey(TASK_2_CATEGORY.UNSPLICED_NO_MM)){
				mapRW.get(curr).put(TASK_2_CATEGORY.UNSPLICED_NO_MM, mapRW.get(curr).get(TASK_2_CATEGORY.UNSPLICED_NO_MM)+1);
			}else{
				mapRW.get(curr).put(TASK_2_CATEGORY.UNSPLICED_NO_MM, 1);
			}
			break;
		case 3:
			if(mapRW.get(curr).containsKey(TASK_2_CATEGORY.SPLICED)){
				mapRW.get(curr).put(TASK_2_CATEGORY.SPLICED, mapRW.get(curr).get(TASK_2_CATEGORY.SPLICED)+1);
			}else{
				mapRW.get(curr).put(TASK_2_CATEGORY.SPLICED, 1);
			}
			break;
		case 4:
			if(mapRW.get(curr).containsKey(TASK_2_CATEGORY.SPLICED)){
				mapRW.get(curr).put(TASK_2_CATEGORY.SPLICED, mapRW.get(curr).get(TASK_2_CATEGORY.SPLICED)+1);
			}else{
				mapRW.get(curr).put(TASK_2_CATEGORY.SPLICED, 1);
			}
			if(mapRW.get(curr).containsKey(TASK_2_CATEGORY.SPLICED_NO_MM)){
				mapRW.get(curr).put(TASK_2_CATEGORY.SPLICED_NO_MM, mapRW.get(curr).get(TASK_2_CATEGORY.SPLICED_NO_MM)+1);
			}else{
				mapRW.get(curr).put(TASK_2_CATEGORY.SPLICED_NO_MM, 1);
			}
			break;
		case 5:
			if(mapRW.get(curr).containsKey(TASK_2_CATEGORY.SPLICED)){
				mapRW.get(curr).put(TASK_2_CATEGORY.SPLICED, mapRW.get(curr).get(TASK_2_CATEGORY.SPLICED)+1);
			}else{
				mapRW.get(curr).put(TASK_2_CATEGORY.SPLICED, 1);
			}
			if(mapRW.get(curr).containsKey(TASK_2_CATEGORY.SPLICED_NO_MM)){
				mapRW.get(curr).put(TASK_2_CATEGORY.SPLICED_NO_MM, mapRW.get(curr).get(TASK_2_CATEGORY.SPLICED_NO_MM)+1);
			}else{
				mapRW.get(curr).put(TASK_2_CATEGORY.SPLICED_NO_MM, 1);
			}
			if(mapRW.get(curr).containsKey(TASK_2_CATEGORY.SPLICED_NO_MM_GT5BP)){
				mapRW.get(curr).put(TASK_2_CATEGORY.SPLICED_NO_MM_GT5BP, mapRW.get(curr).get(TASK_2_CATEGORY.SPLICED_NO_MM_GT5BP)+1);
			}else{
				mapRW.get(curr).put(TASK_2_CATEGORY.SPLICED_NO_MM_GT5BP, 1);
			}
			break;
		default:
			break;
		}
	}

	public void parseInfoLine(String line, int colFW, int colRW) {
		String[] lineArray = line.split("\t",-1);
		refNumber = Integer.parseInt(lineArray[0]);
		refChromsomeID = lineArray[1];
		
		refStartFW = new ArrayList<>();
		refStopFW = new ArrayList<>();
		refStartRW = new ArrayList<>();
		refStopRW = new ArrayList<>();
		
		HashSet<Integer> listeFW = new HashSet<>();
		listeFW.add(1);
		HashSet<Integer> listeRW = new HashSet<>();
		listeRW.add(1);
		int fw = 0;
		int rw = 0;
		
		if (lineArray[colFW].contains("|")) {
			String[] cuttedAtPipe = lineArray[colFW].split("\\|");
			boolean smaller5 = false;
			for (int i = 0; i < cuttedAtPipe.length; i++) {
				String[] cuttedAtMinus = cuttedAtPipe[i].split("-");
				refStartFW.add(Integer.parseInt(cuttedAtMinus[0]));
				refStopFW.add(Integer.parseInt(cuttedAtMinus[1]));
				smaller5 = (Integer.parseInt(cuttedAtMinus[1])-Integer.parseInt(cuttedAtMinus[0]) <= 5) ? true : false;
			}
			fw = 3;
			if(lineArray[8].equals("")){
				fw = 4;
				if(!smaller5){
					fw = 5;
				}
			}
		} else {
			fw = 1;
			if(lineArray[8].equals("")){
				fw = 2;
			}
			refStartFW.add(Integer.parseInt(lineArray[colFW].split("-")[0]));
			refStopFW.add(Integer.parseInt(lineArray[colFW].split("-")[1]));
		}
		if (lineArray[colRW].contains("|")) {
			String[] cuttedAtPipe = lineArray[colRW].split("\\|");
			boolean smaller5 = false;
			for (int i = 0; i < cuttedAtPipe.length; i++) {
				String[] cuttedAtMinus = cuttedAtPipe[i].split("-");
				refStartRW.add(Integer.parseInt(cuttedAtMinus[0]));
				refStopRW.add(Integer.parseInt(cuttedAtMinus[1]));
				smaller5 = (Integer.parseInt(cuttedAtMinus[1])-Integer.parseInt(cuttedAtMinus[0]) <= 5) ? true : false;
			}
			rw = 3;
			if(lineArray[9].equals("")){
				rw = 4;
				if(!smaller5){
					rw = 5;
				}
			}
		} else {
			rw = 1;
			if(lineArray[9].equals("")){
				rw = 2;
			}
			refStartRW.add(Integer.parseInt(lineArray[colRW].split("-")[0]));
			refStopRW.add(Integer.parseInt(lineArray[colRW].split("-")[1]));
		}

		this.simulMap.put(refNumber, new Object[]{refStartFW, refStopFW, refStartRW, refStopRW, refChromsomeID, fw, rw});
	}
	
	public boolean validRecord(SAMRecord s) {
		return (!s.getReadUnmappedFlag() && !s.getMateUnmappedFlag() && !s.getNotPrimaryAlignmentFlag());
	}
	
	public boolean validMate(SAMRecord sam, SAMRecord mate) {
		if (sam.getReadName().equals(mate.getReadName())) {
			if (sam.getFirstOfPairFlag() && mate.getSecondOfPairFlag()) {
				if (sam.getAlignmentStart() == mate.getMateAlignmentStart()) {
					return true;
				}
			}
		}
		return false;
	}

//	public static void main(String[] args) {
//
//		SamFileComparator sfc = new SamFileComparator();
//
////		sfc.compareSamFileToSimulmapping(new String[]{"/home/proj/biocluster/praktikum/genprakt-ws16/KleinPost/Solution2/output/star/star.bam","/home/proj/biocluster/praktikum/genprakt-ws16/KleinPost/Solution2/output/task_2/simulmapping.info"});
//		sfc.compareSamFileToSimulmapping(new String[]{"/home/proj/biosoft/praktikum/genprakt-ws16/assignment/a2/data/bams/star.bam","/home/proj/biosoft/praktikum/genprakt-ws16/assignment/a2/data/comparative_eval.mappinginfo"});
//	}
}