package plotting;

import java.io.File;
import java.util.Vector;

import debugStuff.DebugMessageFactory;
import io.AllroundFileWriter;
import io.ConfigReader;
import io.TemporaryFile;

public class CumDistPlot extends Plot{
	
	Vector<Object> vector;
	
	public CumDistPlot(Vector<Object> vector, String title, String xLab, String yLab) {
		this.vector = vector;
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
		
		AllroundFileWriter.writeVector(this.vector, tmp);
		
		String command = "";
		command += String.format("png(\"%s\",width=3.25,height=3.25,units=\"in\",res=400,pointsize=4);", filename);
		command += String.format("x<-scan(\"%s\",nlines=1,skip=0);", tmp);
		command += String.format("op<-par(mar=c(10,5,4,2)+0.1);");
		command += String.format("plot(ecdf(x),ann=F);");
		command += String.format("par(op);");
		command += String.format("title(main=\"%s\", xlab=\"%s\", ylab=\"%s\");", super.title, super.xLab, super.yLab);
		command += "dev.off();";
		
		return command;
	}

}