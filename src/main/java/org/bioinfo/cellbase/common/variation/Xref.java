package org.bioinfo.cellbase.common.variation;

public class Xref {

	private String dataBase;
	private String crossReference;

	public Xref(String dataBase, String crossReference) {
		this.dataBase = dataBase;
		this.crossReference = crossReference;
	}

	public String getDataBase() {
		return dataBase;
	}

	public void setDataBase(String dataBase) {
		this.dataBase = dataBase;
	}

	public String getCrossReference() {
		return crossReference;
	}

	public void setCrossReference(String crossReference) {
		this.crossReference = crossReference;
	}
	
	

}
