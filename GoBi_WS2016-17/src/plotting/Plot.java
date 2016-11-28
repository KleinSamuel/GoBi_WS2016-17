package plotting;

/**
 * Abstract class for every plot class.
 * 
 * @author Samuel Klein
 */
public abstract class Plot {

	/**
	 * x axis label.
	 */
	String xLab;
	
	/**
	 * y axis label.
	 */
	String yLab;
	
	/**
	 * plot title
	 */
	String title;
	
	/**
	 * Set the plot title.
	 * 
	 * @param t String title
	 */
	void setTitle(String t){
		this.title = t;
	}
	
	/**
	 * Set the x axis label.
	 * 
	 * @param xLab String x axis label
	 */
	void setXLab(String xLab){
		this.xLab = xLab;
	}
	
	/**
	 * Set the y axis label.
	 * 
	 * @param yLab String y axis label
	 */
	void setYLab(String yLab){
		this.yLab = yLab;
	}
	
	/**
	 * Generate a R script.
	 * 
	 * @param filename String plot output filepath
	 * @return command String
	 */
	abstract String generateCommand(String filename);
	
	/**
	 * Create the respective plot.
	 * 
	 * @param filename String plot output filepath
	 */
	abstract void plot();
}
