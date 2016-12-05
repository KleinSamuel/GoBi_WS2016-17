package plotting;

import java.io.File;
import java.util.Vector;

import debugStuff.DebugMessageFactory;
import io.AllroundFileWriter;
import io.ConfigReader;
import io.TemporaryFile;
import javafx.util.Pair;

public class BarPlot extends Plot{

	Pair<Vector<Object>,Vector<Object>> pair;
	public String filename;
	public int minY = 1;
	public int maxY = 0;
	public int bottomMargin = 10;
	public int leftMargin = 5;
	public boolean logScaleY;
	
	public BarPlot(Pair<Vector<Object>,Vector<Object>> pair, String title, String xLab, String yLab, boolean logScaleY) {
		
		this.logScaleY = logScaleY;
		setYLab(yLab);
		this.pair = pair;
		setTitle(title);
		setXLab(xLab);
		
		for(Object o : pair.getKey()){
			maxY = Math.max((int)o,maxY);
		}
//		maxY *= 10;
	}
	
	public void plot(){
		RExecutor r = new RExecutor(generateCommand(ConfigReader.readConfig().get("output_directory")+this.filename+".png"));
		
		Thread t = new Thread(r);
		t.start();
		
		try {
			
			DebugMessageFactory.printNormalDebugMessage(ConfigReader.DEBUG_MODE, "Wait for R to plot..");
			t.join();
			DebugMessageFactory.printNormalDebugMessage(ConfigReader.DEBUG_MODE, "("+this.filename+".png) R thread terminated.");
			
		} catch (InterruptedException e) {
			throw new RuntimeException("R did not exit properly!");
		}
	}
	
	public Vector<Object> logScaleY(Vector<Object> in){
		
		Vector<Object> tmp = new Vector<>();
		double max = 0;
		
		for(Object v : in){
			double x = Math.log10((double)((int)v+1.0));
			tmp.add(x);
			max = Math.max(max, x);
		}
		
		this.maxY = (int)(max+1);
		return tmp;
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
		command += String.format("op<-par(mar=c("+bottomMargin+","+leftMargin+",4,2)+0.1);");
//		if(logScaleY){
			command += String.format("options(scipen=10);");
//		}
		command += String.format("barplot(x,names.arg=y,col=rainbow(\"%s\"),las=2"+(logScaleY ? ",log=\"y\"" : "")+",ylim=c("+1+","+maxY+"));", this.pair.getKey().size());
		command += String.format("par(op);");
		command += String.format("title(main=\"%s\", xlab=\"%s\", ylab=\"%s\");", super.title, super.xLab, super.yLab);
		command += "dev.off();";
		
		return command;
	}
}
