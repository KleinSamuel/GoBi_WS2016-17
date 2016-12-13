package sequenceModifier;

import java.util.Random;

import debugStuff.DebugMessageFactory;
import io.ConfigReader;

/**
 * Contains operations which can be applied to a DNA sequence.
 * 
 * @author Samuel Klein
 */
public class DNAOperations {

	/**
	 * Check if a char is (A|T|G|C).
	 * 
	 * @param base char amino acid
	 * @return true if char is (A|T|G|C)
	 */
	public static boolean isAminoAcid(char base){
		return "ATGC".contains(String.valueOf(base));
	}
	
	/**
	 * Check if a sequence is a DNA sequence.
	 * 
	 * @param sequence String
	 * @return true if DNA sequence
	 */
	public static boolean isDNASequence(String sequence){
		return (sequence.replaceAll("[ATGC]", "").length() == 0);
	}
	
	/**
	 * Get the reverse complement for a DNA sequence.
	 * 
	 * @param sequence String
	 * @return reverse complement sequence String
	 */
	public static String getReverseComplement(String sequence){
		String out = "";
		char[] tmp = getReverse(sequence).toCharArray();
		for (int i = 0; i < tmp.length; i++) {
			out += getComplementBase(tmp[i]);
		}
		return out;
	}
	
	/**
	 * Reverse a input sequence.
	 * 
	 * @param sequence String
	 * @return reversed sequence String
	 */
	public static String getReverse(String sequence){
		return new StringBuilder(sequence).reverse().toString();
	}
	
	/**
	 * Get the complement base for a amino acid
	 * 
	 * @param base char
	 * @return complement char
	 */
	public static char getComplementBase(char base){
		switch (base) {
		case 'A':
			return 'T';
		case 'T':
			return 'A';
		case 'G':
			return 'C';
		case 'C':
			return 'G';
		default:
			break;
		}
		return 'X';
	}
	
	/**
	 * Returns for a amino acid another amino acid which is not the same.
	 * 
	 * @param origin base amino acis
	 * @return char mutated amino acid
	 */
	public static char mutateAminoAcid(char origin){
		Random rand = new Random();
		char out;
		char[] alph = new char[]{'A','G','C','T'};
		do{
			out = alph[rand.nextInt(4)];
		} while (out == origin);
		return out;
	}
	
	/**
	 * Mutate a amino acid sequence with a mutation rate [0,1].
	 * 
	 * @param sequence String
	 * @param mutationrate double between 0 and 1
	 * @return mutated sequence String
	 */
	public static String mutateSequence(String seq, double mutationrate){
		if(mutationrate < 0 || mutationrate > 1){
			DebugMessageFactory.printErrorDebugMessage(ConfigReader.DEBUG_MODE, "Mutation rate must be in range 0 to 1.");
			return null;
		}
		Random rand = new Random();
		for (int j = 0; j < seq.length(); j++) {
			if(rand.nextInt(100/(int)(mutationrate*100)) == 0){
				StringBuilder sb = new StringBuilder(seq);
				sb.setCharAt(j, DNAOperations.mutateAminoAcid(seq.charAt(j)));
				seq = sb.toString();
			}
		}
		return seq;
	}
	
}
