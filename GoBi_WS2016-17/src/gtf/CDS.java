package gtf;

import java.util.ArrayList;

public class CDS {

	private ArrayList<CDSPart> parts;
	
	public CDS(){
		parts = new ArrayList<CDSPart>();
	}
	
	public ArrayList<CDSPart> getParts() {
		return parts;
	}

	public void addPart(CDSPart part) {
		this.parts.add(part);
	}
}
