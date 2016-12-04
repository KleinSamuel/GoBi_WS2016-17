package samfiles;

import java.io.File;
import java.util.HashMap;

import debugStuff.DebugMessageFactory;
import htsjdk.samtools.SAMFileReader;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import htsjdk.samtools.ValidationStringency;
import io.ConfigHelper;
import io.ExternalFileReader;
import io.ExternalFileWriter;

@SuppressWarnings("deprecation")
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
	
	int refStartFW;
	int refStopFW;
	int refStartRW;
	int refStopRW;
	int refNumber;
	String refChromsomeID;
	boolean refIsNegative;
	
	HashMap<Integer, Object[]> simulMap;
	
	public SamFileComparator(){
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
	}
	
	public void compareSamFileToSimulmapping(String[] args){
		
		ExternalFileReader extFR = new ExternalFileReader();
		extFR.openReader(args[1]);
		String l = null;
		extFR.readNextLine();
		while((l = extFR.readNextLine()) != null){
			parseInfoLine(l);
		}
		extFR.closeReader();
		
		SAMFileReader fr = new SAMFileReader(new File(args[0]));
		fr.setValidationStringency(ValidationStringency.SILENT);
		
		SAMRecordIterator iterator = fr.iterator();
		SAMRecord rec = null;
		
		int start, stop;
		int readNumber;
		String chromsomeID;
		
		while(iterator.hasNext()){
			rec = iterator.next();
			
			readNumber = Integer.parseInt(rec.getReadName());
			start = rec.getAlignmentStart()-1;
			stop = rec.getAlignmentEnd()-1;
			chromsomeID = rec.getReferenceName();
			
			refChromsomeID = (String)simulMap.get(readNumber)[4];
			refStartFW = (int)simulMap.get(readNumber)[0];
			refStopFW = (int)simulMap.get(readNumber)[1];
			refStartRW = (int)simulMap.get(readNumber)[2];
			refStopRW = (int)simulMap.get(readNumber)[3];
			
			boolean ok = false;
			boolean part = false;
			boolean wrongCHR = false;
			boolean other = false;
			
			if(refChromsomeID.equals(chromsomeID)){
				/* mapped correct */
				if(refStartFW == start && refStopFW == stop){
					okMappedFW++;
					ok = true;
				}
				/* mapped partially correct */
				else if(refStartFW <= start && refStopFW >= stop){
					partialMappedFW++;
					part = true;
				}
				/* wrongly mapped */
				else{
					elseMappedFW++;
					other = true;
				}
			}else{
				wrongChrMappedFW++;
				wrongCHR = true;
			}
			
			rec = iterator.next();
			
			readNumber = Integer.parseInt(rec.getReadName());
			start = rec.getAlignmentStart();
			stop = rec.getAlignmentEnd();
			chromsomeID = rec.getReferenceName();
			
			if(refChromsomeID == chromsomeID){
				/* mapped correct */
				if(refStartRW == start && refStopRW == stop){
					okMappedRW++;
					if(ok){
						okMappedBOTH++;
					}
				}
				/* mapped partially correct */
				else if(refStartRW <= start && refStopRW >= stop){
					partialMappedRW++;
					if(part){
						partialMappedBOTH++;
					}
				}
				/* wrongly mapped */
				else{
					elseMappedRW++;
					if(other){
						elseMappedBOTH++;
					}
				}
			}else{
				wrongChrMappedRW++;
				if(wrongCHR){
					wrongChrMappedBOTH++;
				}
			}
			
		}
		
		ExternalFileWriter extFW = new ExternalFileWriter();
		extFW.openWriter(new ConfigHelper().getDefaultOutputPath()+"readmapper_eval.txt");
		
		extFW.writeToWriter(""+okMappedFW);
		extFW.writeToWriter("\t"+partialMappedFW);
		extFW.writeToWriter("\t"+wrongChrMappedFW);
		extFW.writeToWriter("\t"+elseMappedFW);
		extFW.writeToWriter("\n"+okMappedRW);
		extFW.writeToWriter("\t"+partialMappedRW);
		extFW.writeToWriter("\t"+wrongChrMappedRW);
		extFW.writeToWriter("\t"+elseMappedRW);
		extFW.writeToWriter("\n"+okMappedBOTH);
		extFW.writeToWriter("\t"+partialMappedBOTH);
		extFW.writeToWriter("\t"+wrongChrMappedBOTH);
		extFW.writeToWriter("\t"+elseMappedBOTH+"\n");
		
		extFW.closeWriter();
	}
	
	public void parseInfoLine(String line){
		String[] lineArray = line.split("\t");
		refNumber = Integer.parseInt(lineArray[0]);
		refChromsomeID = lineArray[1];
		if(lineArray[4].contains("|")){
			refStartFW = Integer.parseInt(lineArray[4].split("\\|")[0].split("-")[0]);	
			refStopFW = Integer.parseInt(lineArray[4].split("\\|")[0].split("-")[1]);
		}else{
			refStartFW = Integer.parseInt(lineArray[4].split("-")[0]);
			refStopFW = Integer.parseInt(lineArray[4].split("-")[1]);
		}
		if(lineArray[5].contains("|")){
			refStartRW = Integer.parseInt(lineArray[5].split("\\|")[0].split("-")[0]);
			refStopRW = Integer.parseInt(lineArray[5].split("\\|")[0].split("-")[1]);
		}else{
			refStartRW = Integer.parseInt(lineArray[5].split("-")[0]);
			refStopRW = Integer.parseInt(lineArray[5].split("-")[1]);
		}
		
		this.simulMap.put(refNumber, new Object[]{refStartFW,refStopFW,refStartRW,refStopRW,refChromsomeID});
	}
	
	public static void main(String[] args) {
		
		SamFileComparator sfc = new SamFileComparator();
		
		sfc.compareSamFileToSimulmapping(args);
		
	}
}
