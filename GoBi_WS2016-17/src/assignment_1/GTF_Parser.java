package assignment_1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GTF_Parser {

	private GenomeAnnotation genomeAnnotation;
	
	public GTF_Parser(){
		
		this.genomeAnnotation = new GenomeAnnotation();
		
	}
	
	private void readFile(String filePath){
		
		boolean isStart = true;
		
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			
			String line = null;
			
			Gene currentGene = null;
			Transcript currentTranscript = null;
			Exon currentExon = null;
			CDS currentCDS = null;
			CDSPart currentCDSPart = null;
			
			String tmpID_gene = "";
			String tmpID_trans = null;
			String tmpID_exon;
			String tmpID_cds;
			
			String[] tempArray;
			
			String seqname, source, feature, start, end, score, strand, frame, attribute;
			
			HashMap<String, String> tempMap;
			String[] a1;
			
			while((line = br.readLine()) != null){
				
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
					Pattern p = Pattern.compile("(.*)? \"(.*)?\"");
					Matcher m = p.matcher(a1[i]);
					
					if(m.find()){
						tempMap.put(m.group(1), m.group(2));
					}
				}
				
				switch (feature) {
				case "gene":
					
					tmpID_gene = getValueFromAttribute("gene_id", tempMap);
					currentGene = new Gene(Integer.parseInt(start), Integer.parseInt(start), tmpID_gene, strand, seqname);
					
					/* chromosome for gene already exists */
					if(genomeAnnotation.getChromosomeList().containsKey(currentGene.getChromosomeID())){
						
						/* gene already exists but may have some entries missing */
						if(genomeAnnotation.getChromosomeList().get(currentGene.getChromosomeID()).getGenes().containsKey(currentGene.getID())){
							
							/* Gene has been added with any entries missing -> add missing entries */
							if(genomeAnnotation.getChromosomeList().get(currentGene.getChromosomeID()).getGenes().get(currentGene.getID()).getStart() == -1){
								Gene g = genomeAnnotation.getChromosomeList().get(currentGene.getChromosomeID()).getGenes().get(currentGene.getID());
								g.setStart(Integer.parseInt(start));
								g.setStop(Integer.parseInt(end));
								g.setStrand(strand);
								g.setChromosomeID(seqname);
							}
						}
						/* gene does not exists */
						else{
							genomeAnnotation.getChromosomeList().get(currentGene.getChromosomeID()).addGene(currentGene);
						}
					}
					/* chromosome does not exist -> add new dummy chromosome -> add gene */
					else{
						Chromosome tmpChrom = new Chromosome(currentGene.getChromosomeID());
						tmpChrom.addGene(currentGene);
						genomeAnnotation.getChromosomeList().put(currentGene.getChromosomeID(), tmpChrom);
					}
					
					break;
				case "transcript":
					
					tmpID_trans = getValueFromAttribute("transcript_id", tempMap);
					currentTranscript = new Transcript(Integer.parseInt(start), Integer.parseInt(end), tmpID_trans, seqname, strand);
					
					/* transcript is for current gene */
					if(tmpID_gene.equals(getValueFromAttribute("gene_id", tempMap))){
						currentGene.addTranscript(currentTranscript);
					}
					/* search for corresponding gene */
					else{
						/* chromosome already exists */
						if(genomeAnnotation.getChromosomeList().containsKey(seqname)){
							/* gene for transcript already exists */
							if(genomeAnnotation.getChromosomeList().get(seqname).getGenes().containsKey(getValueFromAttribute("gene_id", tempMap))){
								/* transcript already exists but is dummy and misses some entries */
								if(genomeAnnotation.getChromosomeList().get(seqname).getGenes().get(getValueFromAttribute("gene_id", tempMap)).getTranscripts().containsKey(currentTranscript.getID())){
									Transcript t = genomeAnnotation.getChromosomeList().get(seqname).getGenes().get(getValueFromAttribute("gene_id", tempMap)).getTranscripts().get(currentTranscript.getID());
									
									if(t.getStart() == -1){
										t.setStart(Integer.parseInt(start));
										t.setStop(Integer.parseInt(end));
										t.setStrand(strand);
										t.setChromosomeID(seqname);
									}
								}
								/* transcript does not exist */
								else{
									genomeAnnotation.getChromosomeList().get(seqname).getGenes().get(getValueFromAttribute("gene_id", tempMap)).addTranscript(currentTranscript);
								}
							}
							/* gene for transcript does not exist */
							else{
								genomeAnnotation.getChromosomeList().get(seqname).addGene(new Gene(-1, -1, getValueFromAttribute("gene_id", tempMap), "", ""));
								genomeAnnotation.getChromosomeList().get(seqname).getGenes().get(getValueFromAttribute("gene_id", tempMap)).addTranscript(currentTranscript);
							}
						}
						/* chromosome does not exist -> add new dummy chromosome -> add new dummy gene to chromosome -> add transcript*/
						else{
							genomeAnnotation.getChromosomeList().put(seqname, new Chromosome(seqname));
							genomeAnnotation.getChromosomeList().get(seqname).addGene(new Gene(-1, -1, getValueFromAttribute("gene_id", tempMap), "", ""));
							genomeAnnotation.getChromosomeList().get(seqname).getGenes().get(getValueFromAttribute("gene_id", tempMap)).addTranscript(currentTranscript);
						}
					}
					
					break;
					
				case "exon":
					
					tmpID_exon = getValueFromAttribute("exon_id", tempMap);
					currentExon = new Exon(Integer.parseInt(start), Integer.parseInt(end), tmpID_exon, strand, seqname);
					
					/* exon is for current gene */
					if(tmpID_gene.equals(getValueFromAttribute("gene_id", tempMap))){
						currentGene.addExon(currentExon);
					}
					/* search for corresponding gene */
					else{
						/* chromosome already exists */
						if(genomeAnnotation.getChromosomeList().containsKey(seqname)){
							/* gene for exon already exists */
							if(genomeAnnotation.getChromosomeList().get(seqname).getGenes().containsKey(getValueFromAttribute("gene_id", tempMap))){
								genomeAnnotation.getChromosomeList().get(seqname).getGenes().get(getValueFromAttribute("gene_id", tempMap)).addExon(currentExon);
							}
							/* gene for exon does not exist */
							else{
								genomeAnnotation.getChromosomeList().get(seqname).addGene(new Gene(-1, -1, getValueFromAttribute("gene_id", tempMap), "", ""));
								genomeAnnotation.getChromosomeList().get(seqname).getGenes().get(getValueFromAttribute("gene_id", tempMap)).addExon(currentExon);
							}
						}
						/* chromosome does not exist -> add new dummy chromosome -> add new dummy gene to chromosome -> add exon*/
						else{
							genomeAnnotation.getChromosomeList().put(seqname, new Chromosome(seqname));
							genomeAnnotation.getChromosomeList().get(seqname).addGene(new Gene(-1, -1, getValueFromAttribute("gene_id", tempMap), "", ""));
							genomeAnnotation.getChromosomeList().get(seqname).getGenes().get(getValueFromAttribute("gene_id", tempMap)).addExon(currentExon);
						}
					}
					
					/* check if exon corresponds to a transcript */
					if(getValueFromAttribute("transcript_id", tempMap) != null){
						
						/* transcript already exists */
						if(genomeAnnotation.getChromosomeList().get(seqname).getGenes().get(getValueFromAttribute("gene_id", tempMap)).getTranscripts().containsKey(getValueFromAttribute("transcript_id", tempMap))){
							genomeAnnotation.getChromosomeList().get(seqname).getGenes().get(getValueFromAttribute("gene_id", tempMap)).getTranscripts().get(getValueFromAttribute("transcript_id", tempMap)).addExon(currentExon);
						}
						/* transcript does not exist */
						else{
							Transcript t = new Transcript(-1, -1, getValueFromAttribute("transcript_id", tempMap), "", "");
							t.addExon(currentExon);
							genomeAnnotation.getChromosomeList().get(seqname).getGenes().get(getValueFromAttribute("gene_id", tempMap)).addTranscript(t);
						}
					}
					
					break;
					
				case "CDS":
					
					tmpID_cds = getValueFromAttribute("protein_id", tempMap);
					currentCDSPart = new CDSPart(Integer.parseInt(start), Integer.parseInt(end), tmpID_cds, seqname);
					
					/* cds is for current transcript */
					if(tmpID_trans.equals(tmpID_cds)){
						currentTranscript.addCds(new CDS());
						currentTranscript.getCds().addPart(currentCDSPart);
					}else{
						System.err.println("FAILED!");
					}
					
					break;
				}
				
			}
			
			
			if(genomeAnnotation.getChromosomeList().containsKey(currentGene.getChromosomeID())){
				genomeAnnotation.getChromosomeList().get(currentGene.getChromosomeID()).addGene(currentGene);
			}else{
				Chromosome tmpChrom = new Chromosome(currentGene.getChromosomeID());
				tmpChrom.addGene(currentGene);
				genomeAnnotation.getChromosomeList().put(currentGene.getChromosomeID(), tmpChrom);
			}
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public GenomeAnnotation getGenomeAnnotation() {
		return genomeAnnotation;
	}
	
	private String getValueFromAttribute(String key, HashMap<String, String> map){
		String id = null;
		if(map.containsKey(key)){
			id = map.get(key);
		}else{
			Exception e = new Exception("attributes do not contain "+key);
		}
		return id;
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
							
							System.out.println("|     |__[cds]  "+entryCDS.getID());
							
						}
					}
					
				}
				
			}
			
		}
		
		return "";
	}
	
	public static void main(String[] args) {
		
		
		GTF_Parser parser = new GTF_Parser();
		
		long start = System.currentTimeMillis();
		
		parser.readFile("/home/proj/biosoft/praktikum/genprakt-ws16/gtf/Saccharomyces_cerevisiae.R64-1-1.75.gtf");
		
		long stop = System.currentTimeMillis();
		
		System.out.println("time needed: "+(stop-start)+" milliseconds.");
		
		GenomeAnnotation ga = parser.getGenomeAnnotation();
		
		parser.toString();
		
	}
}
