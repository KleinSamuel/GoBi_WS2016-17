package io;

import java.util.HashMap;

import assignment_1.Task_2;
import assignment_1.Task_3;
import assignment_1.Task_7;
import tasks.Assignment1;

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
		s += "-t 2\t\tExecute task #2.\n";
		s += "-t 3\t\tExecute task #3.\n";
		s += "-t 4\t\tExecute task #4.\n";
		s += "-t 5\t\tExecute task #5.\n";
		s += "-t 6\t\tExecute task #6.\n";
		s += "-t 7\t\tExecute task #7.\n\n";
		s += "###########################################################\n";
		s += "\nYOU NEED THESE DIRECTORIES NEXT TO THE JAR-FILE:\n\n";
		s += "-config\n";
		s += "\t\tannot.map\n";
		s += "\t\tconfiguration.txt\n";
		s += "\t\tgtf-paths.txt\n";
		s += "-output\n";
		s += "-tempfiles\n\n";
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
		
		s += "INFO NOT AVAILABLE YET.";
		
		return s;
	}
	
	public static String info5(){
		String s = "";
		
		s += "INFO NOT AVAILABLE YET.";
		
		return s;
	}
	
	public static String info6(){
		String s = "";
		
		s += "INFO NOT AVAILABLE YET.";
		
		return s;
	}
	
	public static String info7(){
		String s = "";
		
		s += "INFO NOT AVAILABLE YET.";
		
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
			
			if(argsNew.containsKey("-i")){
				switch (parseArguments(args).get("-i")) {
				case "2":
					System.out.println(printInfo(2));
					break;
				case "3":
					
					break;
				case "7":
					
					break;
					
				default:
					System.out.println("THERE IS NO SUCH TASK.");
					break;
				}
				
				System.exit(0);
			}
			
			
			if(argsNew.containsKey("-t")){
				switch (parseArguments(args).get("-t")) {
				case "2":
					new Task_2().execute_task_2();
					break;
				case "3":
					new Task_3().execute_task_3();
					break;
				case "7":
					new Task_7().execute_task_7();
					break;
					
				default:
					System.out.println("THERE IS NO SUCH TASK.");
					break;
				}
			}
			
		}
		
		
	}
	
	
}
