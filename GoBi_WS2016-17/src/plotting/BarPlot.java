package plotting;

import java.io.File;
import java.util.Arrays;
import java.util.Vector;

import debugStuff.DebugMessageFactory;
import io.AllroundFileWriter;
import io.ConfigReader;
import io.TemporaryFile;
import javafx.util.Pair;

public class BarPlot extends Plot{

	Pair<Vector<Object>,Vector<Object>> pair;
	
	public BarPlot(Pair<Vector<Object>,Vector<Object>> pair, String title, String xLab, String yLab) {
		this.pair = pair;
		setTitle(title);
		setXLab(xLab);
		setYLab(yLab);
	}
	
	public void plot(){
		RExecutor r = new RExecutor(generateCommand(ConfigReader.readConfig().get("output_directory")+this.title+".png"));
		
		Thread t = new Thread(r);
		t.start();
		
		try {
			
			DebugMessageFactory.printNormalDebugMessage(true, "Wait for R to plot..");
			t.join();
			DebugMessageFactory.printNormalDebugMessage(true, "R thread terminated.");
			
		} catch (InterruptedException e) {
			throw new RuntimeException("R did not exit properly!");
		}
	}
	
	@Override
	String generateCommand(String filename) {
		
		File tmp = TemporaryFile.createTempFile();
		
		AllroundFileWriter.writeVector(this.pair.getKey(), tmp);
		AllroundFileWriter.writeVector(this.pair.getValue(), tmp, true);
		
		String command = "";
		command += String.format("png(\"%s\",width=3.25,height=3.25,units=\"in\",res=400,pointsize=4);", filename);
		command += String.format("x<-scan(\"%s\",nlines=1,skip=0);", tmp);
		command += String.format("y<-scan(\"%s\",nlines=1,skip=1,what=character());", tmp);
		command += String.format("barplot(x,names.arg=y);");
		command += String.format("title(main=\"%s\", xlab=\"%s\", ylab=\"%s\");", super.title, super.xLab, super.yLab);
		command += "dev.off();";
		
		return command;
	}

	
	public static void main(String[] args) {
		
		Vector<Object> eins = new Vector<>(Arrays.asList(new Double[]{1.0,5.0,4.0,2.0,7.0}));
		Vector<Object> zwei = new Vector<>(Arrays.asList(new String[]{"eins","zwei","drei","vier","fünf"}));
		
		Pair<Vector<Object>, Vector<Object>> pair = new Pair<Vector<Object>, Vector<Object>>(eins, zwei);
	
		BarPlot bp = new BarPlot(pair, "Test Barplot", "x-axis", "y-axis");
		bp.plot();
	}
}
