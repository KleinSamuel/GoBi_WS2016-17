package io;

import java.util.HashMap;
import java.util.Scanner;

import assignment_1.Task_2;
import assignment_1.Task_3;
import assignment_1.Task_4;
import assignment_1.Task_5;
import assignment_1.Task_6;
import assignment_1.Task_7;
import assignment_2.Task_4.MultiBAMFileReader;
import samfiles.SamFileComparator;
import simulation.ReadSimulator;

public class CommandlineHandler {
	
	public static String createInfo(){
		String s = "";
		
		s += "###########################################################\n";
		s += "\t\t  _____  ____  ____ _____\n";
		s += "\t\t / ____|/ __ \\|  _ \\_   _|\n";
		s += "\t\t| |  __| |  | | |_) || |\n";
		s += "\t\t| | |_ | |  | |  _ < | |\n"; 
		s += "\t\t| |__| | |__| | |_) || |_\n";
		s += "\t\t \\_____|\\____/|____/_____|\n\n";
		s += "___________________________________________________________\n\n";
		s += "by\tSAMUEL KLEIN\t     and\tDENNIS POST\n";
		s += "___________________________________________________________\n";
		s += "###########################################################\n";
		s += "FLAGS\t\tHINTS\n\n";
		s += "-i x\t\tPrint information about specific task x.\n\n";
		s += "-t 12\t\tExecute assignment 1 task #2.\n";
		s += "-t 13\t\tExecute assignment 1 task #3.\n";
		s += "-t 14\t\tExecute assignment 1 task #4.\n";
		s += "-t 15\t\tExecute assignment 1 task #5.\n";
		s += "-t 16\t\tExecute assignment 1 task #6.\n";
		s += "-t 17\t\tExecute assignment 1 task #7.\n\n";		
		s += "-t 22\t\tExecute assignment 2 task #2.\n";
		s += "-t 23\t\tExecute assignment 2 task #3.\n";
		s += "-t 24\t\tExecute assignment 2 task #4.\n\n";
		s += "-q\t\tQuiet mode (no print statements)\n\n";
		s += "###########################################################\n";
		s += "\nTHESE DIRECTORIES NEED TO BE NEXT TO THE JAR-FILE:\n\n";
		s += "config\n";
		s += "\t\tannot.map\n";
		s += "\t\tconfiguration.txt\n";
		s += "\t\tgtf-paths.txt\n";
		s += "output\n";
		s += "tempfiles\n\n";
		s += "\nTHESE FILES NEED TO BE NEXT TO THE JAR-FILE:\n\n";
		s += "ExonSkippingPlotter.R\nOverlapPlotter.R\nUnionTranscriptPlotter.R\n";
		s += "###########################################################\n";
		
		return s;
	}
	
	public static String createError(){
		String s = "";
		s += "\n\n###############################################################################\n";
		s += "# YOU MISSED SOME FLAGS! PLEASE PROVIDE THE NEEDED PARAM FOR EVERY USED FLAG! #\n";
		s += "###############################################################################\n\n";
		s += createInfo();
		return s;
	}
	
	public static String printInfo(int n){
		String s = "";
		switch (n) {
		case 1:
			s = info1();
			break;
		case 2:
			s = info2();
			break;
		case 3:
			s = info3();
			break;
		case 4:
			s = info4();
			break;
		case 5:
			s = info5();
			break;
		case 6:
			s = info6();
			break;
		case 7:
			s = info7();
			break;

		default:
			break;
		}
		return s;
	}
	
	public static String info1(){
		return null;
	}
	
	public static String info2(){
		String s = "";
		s += "\nTask 2 (Compare number of annotated genes):\n\n";
		s += "You find 6 GTF files, and a file named annot.map in\n";
		s += "/home/proj/biosoft/praktikum/genprakt/assignment/a1/data.\n";
		s += "Use your parser from Task 1 to compare the number of annotated genes per biotype in the different\n";
		s += "GTF files. Write a table to a file named genetypes.txt in your solution directory containing for\n";
		s += "every biotype and every gtf-name (first column in annot.map) the number of annotated genes per\n";
		s += "biotype in the corresponding gtf. The biotypes should be ordered by their total occurrence in all GTF files.\n";
		s += "In addition, create barplots for each biotype and add them to a html file genetypes.html in this order.\n";
		return s;
	}
	
	public static String info3(){
		String s = "";
		s += "\nTask 3 (Compare number of transcripts in annotated genes):\n\n";
		s += "Extract the number of transcripts per gene using the same files as in Task 2. Create an html\n";
		s += "file showing the cumulative distributions of the number of transcripts for each biotype in the different\n";
		s += "GTF files. Write the biotypes sorted by the total number of genes having multiple transcripts.\n";
		s += "The GTF file with the key h.ens.86 refers to the current ENSEMBL annotation. Supply for every\n";
		s += "biotype a list of information (see below) and a link to the gene on the current ENSEMBL website\n";
		s += "for 10 genes with the most transcripts in this GTF.\n";
		s += "Provide the information: id, symbol, biotype, chromosome, strand, start, end, number of transcripts,\n";
		s += "number of CDS-s. The url for a given gene on the ENSEMBL website is given by:\n";
		s += "http://www.ensembl.org/Homo_sapiens/Gene/Summary?db=core;g=<geneid>\n";
		return s;
	}
	
	public static String info4(){
		String s = "";
		s += "\nTask 4 (Compare number of overlapping genes):\n\n";
		s += "Extract the list of pairs of overlapping genes for every input GTF file. As genes may be located on\n";
		s += "the plus or minus strand, there are three different types of overlap: (i) overlapping genes disregarding\n";
		s += "their strands, (ii) overlapping genes on the same strand, (iii) overlapping genes on different strands.\n";
		s += "Write overlapping gene pairs of all three types into the file ${gtfname}.overlaps.tsv into your\n";
		s += "solution directory (for each of the gtf files. These tab separated files must have the header: geneid1,\n";
		s += "geneid2, strand1, strand2, biotype1, biotype2, num overlapping bases.\n";
		s += "Create an html file showing the cumulative distribution of overlapping genes for all three overlap\n";
		s += "types per biotype-pairs, showing the biotype-pairs ordered (descending) by their total number (over\n";
		s += "all GTF files) of overlapping genes.\n";
		s += "Similar to Task 3 add a list to the html output for each biotype pair with the ten most overlapping\n";
		s += "genes in the current ENSEMBL version with links to the corresponding genes.\n";
		return s;
	}
	
	public static String info5(){
		String s = "";
		s += "\nTask 5 (Analyze the transcribed lengths of genes):\n\n";
		s += "Implement a method that calculates for a gene the “union transcript” i.e. the genomic region vector\n";
		s += "that covers all transcripts annotated by the gene (and nothing more).\n";
		s += "Calculate for every gene the proportion “length of longest transcript”/“length of union transcript”\n";
		s += "in every gtf, plot their cumulative distributions and collect those plots in the html file\n";
		s += "transcript_lengths.html.\n";
		return s;
	}
	
	public static String info6(){
		String s = "";
		s += "\nTask 6 (Analyze exon skippings):\n\n";
		s += "One form of alternative splicing is exon skipping. An exon-skipping splicing event is a tuple (gene,\n";
		s += "intron-start, intron-end) and is defined by (at least) two transcripts: wildtype (WT) and spliced\n";
		s += "variant (SV) of the same gene, and an intron in SV with start and end corresponding to an exon\n";
		s += "end and exon start in WT, respectively, and the SV-intron spans at least one exon in WT.\n";
		s += "For any exon-skip event there may be several WT-s and several SV-s, and there may be several\n";
		s += "sets of skipped exons (see figure below; this is one exon-skipping event).\n";
		return s;
	}
	
	public static String info7(){
		String s = "";
		
		s += "\nTask 7 (Compare genome versions):\n\n";
		s += "While the human genome was 90% completed already in the year 2000 and announced to be complete\n";
		s += "in 2003, there are still regularly changes both in the overall assembly and the gene annotation.\n";
		s += "The GTF-s with key h.gc.10 and h.gc.23 are based on different assemblies provided by GENCODE.\n";
		s += "Compare the set of genes annotated in both versions (gene id up to the dot):\n";
		s += "• For how many genes has the chromosome been changed?\n";
		s += "• For all others, create a cumulative plot on the absolute chromosomal distance (minimum of\n";
		s += "start / end differences) of the genes (chrdist.jpg)\n";
		s += "• Create a cumulative plot on the gene length differences (glengthdiff.jpg)\n";
		s += "• Create a cumulative plot for the distribution of differences of the number of annotated transcripts\n";
		s += "/ CDS-s (two curves) (andiff.jpg)\n";
		s += "Collect these plots in the html file genome_versions.html.\n";
		
		return s;
	}
	
	public static HashMap<String, String> parseArguments(String[] args){
		
		HashMap<String, String> map = new HashMap<>();
		
		for (int i = 0; i < args.length-1; i++) {
			map.put(args[i], args[i+1]);
		}
		
		return map;
	}
	
	public static void main(String[] args) {
		
		if(args.length == 0){
			System.out.println(createInfo());
			System.exit(0);
		}else if(args.length%2 != 0){
			System.out.println(createError());
		}else{
			
			HashMap<String, String> argsNew = parseArguments(args);
			
			ConfigReader.DEBUG_MODE = argsNew.containsKey("-q") ? false : true;
			
			if(argsNew.containsKey("-i")){
				switch (parseArguments(args).get("-i")) {
				case "12":
					System.out.println(printInfo(2));
					break;
				case "13":
					System.out.println(printInfo(3));
					break;
				case "14":
					System.out.println(printInfo(4));
					break;
				case "15":
					System.out.println(printInfo(5));
					break;
				case "16":
					System.out.println(printInfo(6));
					break;
				case "17":
					System.out.println(printInfo(7));
					break;
					
				default:
					System.out.println("THERE IS NO INFO.");
					break;
				}
				
				System.exit(0);
			}
			
			if(argsNew.containsKey("-t")){
				switch (parseArguments(args).get("-t")) {
				case "12":
					new Task_2().execute_task_2();
					break;
				case "13":
					new Task_3().execute_task_3();
					break;
				case "14":
					new Task_4().execute_task_4();
					break;
				case "15":
					new Task_5().execute_task_5();
					break;
				case "16":
					new Task_6().execute_task_6();
					break;
				case "17":
					new Task_7().execute_task_7();
					break;
				case "22":
					String[] tmp22 = ConfigReader.readConfig().get("read_sim_params").split(",");
					ReadSimulator rsim = new ReadSimulator(Integer.parseInt(tmp22[0]), Double.parseDouble(tmp22[1]), Double.parseDouble(tmp22[2]), Double.parseDouble(tmp22[3]), tmp22[4]);
					rsim.simulateReads();
					break;
				case "23":
					SamFileComparator sfc = new SamFileComparator();
					String[] tmp23 = ConfigReader.readConfig().get("bam_comp_params").split(",");
					sfc.compareSamFileToSimulmapping(new String[]{tmp23[0],tmp23[1]});
					break;
				case "24":
					String[] tmp24 = ConfigReader.readConfig().get("bam_multi_comp_params").split(",");
					MultiBAMFileReader multi = new MultiBAMFileReader(tmp24[0], tmp24[1], tmp24[2], tmp24[3]);
					break;
					
				default:
					System.out.println("THERE IS NO SUCH TASK.");
					break;
				}
			}
			
		}
		
		
	}
	
	
}
