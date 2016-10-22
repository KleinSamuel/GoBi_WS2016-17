package plotting;

import java.io.File;
import java.util.*;

import debugStuff.DebugMessageFactory;
import io.AllroundFileWriter;
import io.TemporaryFile;
import javafx.util.Pair;

public class LinePlot extends Plot{

	Vector<Object> x;
	Vector<Object> y;
	
	public LinePlot(Pair<Vector<Object>,Vector<Object>> pair, String title, String xLab, String yLab){
		
		this.x = pair.getKey();
		this.y = pair.getValue();
		
		setTitle(title);
		setXLab(xLab);
		setYLab(yLab);
	}
	
	public void plot(String filename){
		RExecutor r = new RExecutor(generateCommand(filename));
		
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
	public String generateCommand(String filename) {
		
		File tmp = TemporaryFile.createTempFile("/home/sam/");
		
		AllroundFileWriter.writeVector(this.x, tmp);
		AllroundFileWriter.writeVector(this.y, tmp, true);
		
		String command = "";
		command += String.format("png(\"%s\",width=3.25,height=3.25,units=\"in\",res=400,pointsize=4);", filename);
		command += String.format("x<-scan(\"%s\",nlines=1,skip=0);", tmp);
		command += String.format("y<-scan(\"%s\",nlines=1,skip=1);", tmp);
		command += String.format("plot(x,y,ann=F);");
		command += String.format("title(main=\"%s\", xlab=\"%s\", ylab=\"%s\");", super.title, super.xLab, super.yLab);
		command += "dev.off();";
		
		return command;
		
	}
	
	public static void main(String[] args) {
		
		Vector<Object> eins = new Vector<>(Arrays.asList(new Double[]{1.0,5.0,4.0,2.0,7.0}));
		Vector<Object> zwei = new Vector<>(Arrays.asList(new Double[]{1.0,5.0,4.0,2.0,7.0}));
		
		Pair<Vector<Object>, Vector<Object>> pair = new Pair<Vector<Object>, Vector<Object>>(eins, zwei);
	
		LinePlot bp = new LinePlot(pair, "Test Title", "x-axis", "y-axis");
		
		bp.plot("/home/sam/Plot.png");
		
	}
}
