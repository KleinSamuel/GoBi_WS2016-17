package gtf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import debugStuff.DebugMessageFactory;
import io.ConfigReader;

public class GTF_Parser implements Runnable {
		
	private GenomeAnnotation genomeAnnotation;
	private String threadName;
	private String FILEPATH;
	private ArrayList<String> lines;
	private int idCounter = 0;
	
	public GTF_Parser(String name, ArrayList<String> lines, GenomeAnnotation genomeAnnotation){
		this.threadName = name;
		this.lines = lines;
		this.genomeAnnotation = genomeAnnotation;
	}
	
	private void readFile(String filePath){
			
		Gene currentGene = null;
		Transcript currentTranscript = null;
		Exon currentExon = null;
		CDSPart currentCDSPart = null;
		
		String tmpID_gene = "";
		String tmpID_trans = null;
		String tmpID_exon;
		String tmpID_cds;
		
		String[] tempArray;
		
		String line = "";
		
		String seqname, source, feature, start, end, score, strand, frame, attribute;
		
		/* storing values for current IDs */
		String tempChrID, tempGeneID, tempTranscriptID, tempBiotype;
		
		HashMap<String, String> tempMap;
		String[] a1;
		Pattern p = Pattern.compile("(.*)? \"(.*)?\"");
		
		for(String s : lines){
			
			line = s;
			
			if(line.startsWith("#")){
				continue;
			}
			
			tempArray = line.split("\t");
			
			seqname = tempArray[0];
			source = tempArray[1];
			feature = tempArray[2];
			start = tempArray[3];
			end = tempArray[4];
			score = tempArray[5];
			strand = tempArray[6];
			frame = tempArray[7];
			attribute = tempArray[8];
			
			tempMap = new HashMap<String, String>();
			
			a1 = attribute.split("; ");
			

			for (int i = 0; i < a1.length; i++) {
				Matcher m = p.matcher(a1[i]);
				
				if(m.find()){
					tempMap.put(m.group(1), m.group(2));
				}
			}
			
			switch (feature) {
			
			case "gene":
				
				tempChrID = seqname;
				tempBiotype = getValueFromAttribute("gene_biotype", tempMap);
				
				if(tempBiotype == null){
					tempBiotype = getValueFromAttribute("gene_type", tempMap);
				}
				
				tmpID_gene = getValueFromAttribute("gene_id", tempMap);
				currentGene = new Gene(tmpID_gene, getValueFromAttribute("gene_name", tempMap), Integer.parseInt(start), Integer.parseInt(end), strand, tempBiotype, null, tempChrID);
				
				/* chromosome for gene already exists */
				if(genomeAnnotation.getChromosomeList().containsKey(tempChrID)){
					
					Chromosome tmpCHR = genomeAnnotation.getChromosomeList().get(tempChrID);
					
					/* gene already exists but may have some entries missing */
					if(tmpCHR.getGenes().containsKey(tmpID_gene)){
						
						Gene g = tmpCHR.getGenes().get(tmpID_gene);
						
						if(g.getStart() == -1){
							g.setStart(Integer.parseInt(start));
							g.setStop(Integer.parseInt(end));
							g.setOnNegativeStrand(strand);
							g.setBioType(tempBiotype);
							g.setChromosome(genomeAnnotation.getChromosomeList().get(tempChrID));
							g.setSymbol(getValueFromAttribute("gene_name", tempMap));
							g.setChromosomeID(tempChrID);
						}
					}
					/* gene does not exists */
					else{
						tmpCHR.addGene(currentGene);
					}
				}
				/* chromosome does not exist -> add new dummy chromosome -> add gene */
				else{
					genomeAnnotation.addChromosome(createDummyChromosome(tempChrID).addGene(currentGene));
				}
				
				break;
			case "transcript":
				
				tmpID_trans = getValueFromAttribute("transcript_id", tempMap);
				
				currentTranscript = new Transcript(tmpID_trans, Integer.parseInt(start), Integer.parseInt(end), strand, null);
				
				tempChrID = seqname;
				tempGeneID = getValueFromAttribute("gene_id", tempMap);
				
					/* transcript is for current gene */
					if(tmpID_gene != null && tmpID_gene.equals(tempGeneID)){
						currentGene.addTranscript(currentTranscript);
					}
					/* search for corresponding gene */
					else{
					/* chromosome already exists */
					if(genomeAnnotation.getChromosomeList().containsKey(tempChrID)){
						
						Chromosome tmpCHR = genomeAnnotation.getChromosomeList().get(tempChrID);
						
						/* gene for transcript already exists */
						if(tmpCHR.getGenes().containsKey(tempGeneID)){
							
							/* transcript already exists but is dummy and misses some entries */
							if(tmpCHR.getGenes().get(tempGeneID).getTranscripts().containsKey(tmpID_trans)){
								
								Transcript t = tmpCHR.getGenes().get(tempGeneID).getTranscripts().get(tmpID_trans);
								
								if(t.getStart() == -1){
									t.setStart(Integer.parseInt(start));
									t.setStop(Integer.parseInt(end));
									t.setOnNegativeStrand(strand);
									t.setGene(genomeAnnotation.getGene(tempGeneID));
								}
							}
							/* transcript does not exist */
							else{
								tmpCHR.getGenes().get(tempGeneID).addTranscript(currentTranscript);
							}
						}
						/* gene for transcript does not exist -> create dummy gene*/
						else{
							tmpCHR.addGene(createDummyGene(tempGeneID).addTranscript(currentTranscript));
						}
					}
					/* chromosome does not exist -> add new dummy chromosome -> add new dummy gene to chromosome -> add transcript*/
					else{
						genomeAnnotation.addChromosome(createDummyChromosome(tempChrID).addGene(createDummyGene(tempGeneID).addTranscript(currentTranscript)));
					}
					}
				
				break;
				
			case "exon":
				
				tmpID_exon = getValueFromAttribute("exon_id", tempMap);
				
				if(tmpID_exon == null){
					tmpID_exon = createNewUniqueExonId();
				}
				
				currentExon = new Exon(tmpID_exon, Integer.parseInt(start), Integer.parseInt(end), strand);
				
				tempChrID = seqname;
				tempGeneID = getValueFromAttribute("gene_id", tempMap);
				tempTranscriptID = getValueFromAttribute("transcript_id", tempMap);
				
					/* exon is for current gene */
					if(tmpID_gene != null && tmpID_gene.equals(tempGeneID)){
						currentGene.addExon(currentExon);
					}
					/* search for corresponding gene */
					else{
					/* chromosome already exists */
					if(genomeAnnotation.getChromosomeList().containsKey(tempChrID)){
						
						Chromosome tmpCHR = genomeAnnotation.getChromosomeList().get(tempChrID);
						
						/* gene for exon already exists */
						if(tmpCHR.getGenes().containsKey(tempGeneID)){
							
							/*exon already exists but is dummy and misses some entries */
							if(tmpCHR.getGenes().get(tempGeneID).getExons().containsKey(tmpID_exon)){
								
								Exon e = tmpCHR.getGenes().get(tempGeneID).getExons().get(tmpID_exon);
								
								if(e.getStart() == -1){
									e.setStart(Integer.parseInt(start));
									e.setStop(Integer.parseInt(end));
								}
								
							}
							/* exon does not exist */
							else{
								tmpCHR.getGenes().get(tempGeneID).addExon(currentExon);
							}
						}
						/* gene for exon does not exist -> create dummy gene*/
						else{
							tmpCHR.addGene(createDummyGene(tempGeneID).addExon(currentExon));
						}
					}
					/* chromosome does not exist -> add new dummy chromosome -> add new dummy gene to chromosome -> add exon*/
					else{
						genomeAnnotation.addChromosome(createDummyChromosome(tempChrID).addGene(createDummyGene(tempGeneID).addExon(currentExon)));
					}
					}
				
				/* check if exon corresponds to a transcript */
				if(tempTranscriptID != null){
					
					/* transcript already exists */
					if(genomeAnnotation.getChromosomeList().get(seqname).getGenes().get(tempGeneID).getTranscripts().containsKey(tempTranscriptID)){
						genomeAnnotation.getChromosomeList().get(seqname).getGenes().get(tempGeneID).getTranscripts().get(tempTranscriptID).addExon(currentExon);
					}
					/* transcript does not exist */
					else{
						genomeAnnotation.getChromosomeList().get(tempChrID).getGenes().get(tempGeneID).addTranscript(createDummyTranscript(tempTranscriptID).addExon(currentExon));
					}
				}
				
				break;
				
			case "CDS":
				
				tmpID_cds = getValueFromAttribute("protein_id", tempMap);
				currentCDSPart = new CDSPart(tmpID_cds, Integer.parseInt(start), Integer.parseInt(end), strand);
				
				tempChrID = seqname;
				tempGeneID = getValueFromAttribute("gene_id", tempMap);
				tempTranscriptID = getValueFromAttribute("transcript_id", tempMap);
				
					/* cds is for current transcript */
					if(tmpID_trans != null && tmpID_trans.equals(tmpID_cds)){
						currentTranscript.getCds().addPart(currentCDSPart);
					}
					/* search for corresponding gene */
					else{
					/* chromosome already exists */
					if(genomeAnnotation.getChromosomeList().containsKey(tempChrID)){
						
						Chromosome tmpCHR1 = genomeAnnotation.getChromosomeList().get(seqname);
						Transcript dummyTrans = createDummyTranscript(tempTranscriptID);
						
						/* gene for transcript already exists */
						if(tmpCHR1.getGenes().containsKey(tempGeneID)){
							
							Gene tmpGENE1 = tmpCHR1.getGenes().get(tempGeneID);
							
							/* transcript for cds already exists */
							if(tmpGENE1.getTranscripts().containsKey(tempTranscriptID)){
								tmpGENE1.getTranscripts().get(tempTranscriptID).getCds().addPart(currentCDSPart);
							}
							/* transcript for cds does not exist */
							else{
								dummyTrans.getCds().addPart(currentCDSPart);
								tmpGENE1.addTranscript(dummyTrans);
							}
						}
						/* gene for transcript does not exist */
						else{
							dummyTrans.getCds().addPart(currentCDSPart);
							tmpCHR1.addGene(createDummyGene(tempGeneID).addTranscript(dummyTrans));
						}
						
					}
					/* chromosome does not exist -> add new dummy chromosome -> add new dummy gene to chromosome -> add new dummy transcript -> add cds*/
					else{
						Transcript dummyTrans = createDummyTranscript(tempTranscriptID);
						dummyTrans.getCds().addPart(currentCDSPart);
						genomeAnnotation.addChromosome(createDummyChromosome(tempChrID).addGene(createDummyGene(tempGeneID).addTranscript(dummyTrans)));
					}
					}
				
				break;
			}
			
		}
			
	}
	
	private Transcript createDummyTranscript(String transID){
		return new Transcript(transID, -1, -1, "", null);
	}
	
	private Gene createDummyGene(String geneID){
		return new Gene(geneID, "", -1, -1, "", "", null, "");
	}
	
	private Chromosome createDummyChromosome(String chrID){
		return new Chromosome(chrID);
	}

	public GenomeAnnotation getGenomeAnnotation() {
		return genomeAnnotation;
	}
	
	private String getValueFromAttribute(String key, HashMap<String, String> map){
		String id = null;
		if(map.containsKey(key)){
			id = map.get(key);
		}
		return id;
	}
	
	private String createNewUniqueExonId(){
		String idFormatted = String.valueOf(idCounter++);
		return "ENSE_"+"000000000".substring(idFormatted.length())+idFormatted+"_"+threadName;
		
	}
	
	public String toString(){
		
		for(Entry<String, Chromosome> entryChrom : genomeAnnotation.getChromosomeList().entrySet()){
			System.out.println("[chr] "+entryChrom.getKey());
			System.out.println("|");
			
			for(Entry<String, Gene> entryGene : entryChrom.getValue().getGenes().entrySet()){
				System.out.println("|__[gene] "+entryGene.getKey());
				System.out.println("|  |");
				
				for(Entry<String, Exon> entryExon : entryGene.getValue().getExons().entrySet()){
					System.out.println("|  |__[exon]  "+entryExon.getKey());
					System.out.println("|  |");
				}
				
				for(Entry<String, Transcript> entryTrans : entryGene.getValue().getTranscripts().entrySet()){
					System.out.println("|  |__[trans] "+entryTrans.getKey());
					System.out.println("|     |");
					
					for(Entry<String, Exon> entryExon : entryTrans.getValue().getExons().entrySet()){
						System.out.println("|     |__[exon] "+entryExon.getKey());
					}
					
					if(entryTrans.getValue().getCds() != null){
						for(CDSPart entryCDS : entryTrans.getValue().getCds().getParts()){
							System.out.println("|     |__[cds]  "+entryCDS.getId());
						}
					}
				}
			}
		}
		return "";
	}
	
	@Override
	public void run() {
		
		DebugMessageFactory.printInfoDebugMessage(ConfigReader.DEBUG_MODE, "Thread #"+this.threadName+" started.");
		
		this.readFile(this.FILEPATH);
		
		DebugMessageFactory.printInfoDebugMessage(ConfigReader.DEBUG_MODE, "Thread #"+this.threadName+" finished.");
	}

}
