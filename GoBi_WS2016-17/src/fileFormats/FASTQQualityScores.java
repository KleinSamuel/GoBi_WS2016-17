package fileFormats;

/**
 * Quality scores for fastQ files.
 * lowest < highest
 * 
 * @author Samuel Klein
 */
public class FASTQQualityScores {
	
	public static final String QUALITY_SCORES = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
	public static final char[] QUALITY_SCORES_ARRAY = QUALITY_SCORES.toCharArray();
	
}
