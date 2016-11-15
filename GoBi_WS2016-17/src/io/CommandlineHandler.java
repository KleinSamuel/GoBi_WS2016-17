package io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import tasks.Assignment1;

public class CommandlineHandler {

	public static ArrayList<String> flags = new ArrayList<String>(Arrays.asList(new String[]{"t"}));
	
	public static String createInfo(){
		String s = "";
		
		s += "###########################################################\n";
		s += "\t\t  _____  ____  ____ _____\n";
		s += "\t\t / ____|/ __ \\|  _ \\_   _|\n";
		s += "\t\t| |  __| |  | | |_) || |\n";
		s += "\t\t| | |_ | |  | |  _ < | |\n"; 
		s += "\t\t| |__| | |__| | |_) || |_\n";
		s += "\t\t \\_____|\\____/|____/_____|\n\n";
		s += "###########################################################\n";
		s += "FLAGS\t\tHINTS\n\n";
		s += "-i x\t\tPrint information about specific task x.\n\n";
		s += "-t 1\t\tExecute task #1.\n";
		s += "-t 2\t\tExecute task #2.\n";
		s += "-t 3\t\tExecute task #3.\n";
		s += "-t 4\t\tExecute task #4.\n";
		s += "-t 5\t\tExecute task #5.\n";
		s += "-t 6\t\tExecute task #6.\n";
		s += "-t 7\t\tExecute task #7.\n\n";
		s += "-o \"path\"\tOutputdirectory, if not set the default \n\t\tdirectory will be used.\n\n";
		s += "-f \"path\"\tPath to directory with gtf files.\n";
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
		return null;
	}
	
	public static String info3(){
		return null;
	}
	
	public static String info4(){
		return null;
	}
	
	public static String info5(){
		return null;
	}
	
	public static String info6(){
		return null;
	}
	
	public static String info7(){
		return null;
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
			
			Assignment1 as = new Assignment1();
			
			switch (parseArguments(args).get("-t")) {
			case "1":
				as.task_1();
				break;
			case "2":
				as.task_2();
				break;
			case "3":
				as.task_3();
				break;
			case "7":
				as.task_7();
				break;
				
			default:
				break;
			}
			
		}
		
		
	}
	
	
}
