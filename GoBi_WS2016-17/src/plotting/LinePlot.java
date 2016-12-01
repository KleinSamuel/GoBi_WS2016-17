package plotting;

import java.io.File;
import java.util.Vector;

import debugStuff.DebugMessageFactory;
import io.AllroundFileWriter;
import io.ConfigReader;
import io.TemporaryFile;
import javafx.util.Pair;

public class LinePlot extends Plot{

	Vector<Vector<Object>> x;
	Vector<Vector<Object>> y;
	
	Vector<Object> legendLabels;
	
	int maxX, maxY;
	int minX = 0, minY = 0;
	
	public boolean showLegend = true;
	
	public String filename;
	
	public LinePlot(Pair<Vector<Vector<Object>>,Vector<Vector<Object>>> pair, String title, String xLab, String yLab, int maxX, int maxY, boolean logScaleX, boolean logScaleY){
		
		this.maxX = maxX;
		this.maxY = maxY;
		
		if(logScaleX){
			this.x = logScaleX(pair.getKey());
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}else{
			this.x = pair.getKey();
		}
		
		if(logScaleY){
			this.y = logScaleY(pair.getValue());
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}else{
			this.y = pair.getValue();
		}
		
		setTitle(title);
		filename = title;
		setXLab(logScaleX ? xLab+" (log10)" : xLab);
		setYLab(logScaleY ? yLab+" (log10)" : yLab);
		
		legendLabels = new Vector<>();
		
		for (int i = 0; i < x.size(); i++) {
			legendLabels.add(String.valueOf(i));
		}
	}
	
	public LinePlot(Pair<Vector<Vector<Object>>,Vector<Vector<Object>>> pair, String title, String xLab, String yLab, int minX, int minY, int maxX, int maxY, boolean logScaleX, boolean logScaleY){
		
		this(pair, title, xLab, yLab, maxX, maxY, logScaleX, logScaleY);
		
		this.minX = minX;
		this.minY = minY;
	}
	
	public Vector<Vector<Object>> logScaleX(Vector<Vector<Object>> in){
		
		Vector<Vector<Object>> tmp = new Vector<>();
		double max = 0;
		
		for(Vector<Object> v : in){
			Vector<Object> tmp2 = new Vector<>();
			for(Object v2 : v){
				double x = Math.log10((double)((int)v2+1.0));
				tmp2.add(x);
				max = Math.max(max, x);
			}
			tmp.add(tmp2);
		}
		
		this.maxX = (int)(max+1);
		return tmp;
	}
	
	public Vector<Vector<Object>> logScaleY(Vector<Vector<Object>> in){
		
		Vector<Vector<Object>> tmp = new Vector<>();
		double max = 0;
		
		for(Vector<Object> v : in){
			Vector<Object> tmp2 = new Vector<>();
			for(Object v2 : v){
				double x = Math.log10((double)((int)v2+1.0));
				tmp2.add(x);
				max = Math.max(max, x);
			}
			tmp.add(tmp2);
		}
		
		this.maxY = (int)(max+1);
		return tmp;
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
	
	public void addLegendVector(Vector<Object> vector){
		this.legendLabels = vector;
	}
	
	@Override
	public String generateCommand(String filename) {
		
		File tmp = TemporaryFile.createTempFile();
		
		AllroundFileWriter.writeVector(x.get(0), tmp);
		AllroundFileWriter.writeVector(y.get(0), tmp, true);
		
		for (int i = 1; i < x.size(); i++) {
			AllroundFileWriter.writeVector(x.get(i), tmp, true);
			AllroundFileWriter.writeVector(y.get(i), tmp, true);
		}
		
		AllroundFileWriter.writeVector(this.legendLabels, tmp, true);
		
		String command = "";
		command += String.format("png(\"%s\",width=3.25,height=3.25,units=\"in\",res=400,pointsize=4);", filename);
		command += String.format("x<-scan(\"%s\",nlines=1,skip=0);", tmp);
		command += String.format("y<-scan(\"%s\",nlines=1,skip=1);", tmp);
		
		command += String.format("plot(x,y,ann=F,type=\"l\",xlim=range("+minX+":"+maxX+"),ylim=range("+minY+":"+maxY+"),col=1);");
		
		int counter = 2;
		
		for (int i = 1; i < x.size(); i++) {
			command += String.format("x"+i+"<-scan(\"%s\",nlines=1,skip="+(counter)+");", tmp);
			counter += 1;
			command += String.format("y"+i+"<-scan(\"%s\",nlines=1,skip="+(counter)+");", tmp);
			counter += 1;
			command += String.format("lines(x"+i+",y"+i+",type=\"l\",col="+(i+1)+");", tmp);
			
		}
		
		command += String.format("ll<-scan(\"%s\",nlines=1,skip="+(counter)+",what=character());", tmp);
		if(showLegend){
			command += String.format("legend(\"bottomright\", legend=ll, col=1:"+x.size()+", lty=c(1:1));");
		}
		command += String.format("title(main=\"%s\", xlab=\"%s\", ylab=\"%s\");", super.title, super.xLab, super.yLab);
		command += "dev.off();";
		
		return command;
		
	}
}
