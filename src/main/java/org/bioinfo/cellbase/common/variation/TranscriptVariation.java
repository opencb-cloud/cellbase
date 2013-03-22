package org.bioinfo.cellbase.common.variation;

public class TranscriptVariation {

	private String soAccession;
	private String soName;
	private String trancriptId;
	private String allele;
	private String featureType;
	
	public TranscriptVariation(String consequenceType, String trancriptId, String allele, String featureType) {
		this.soName = consequenceType;
		this.trancriptId = trancriptId;
		this.allele = allele;
		this.featureType = featureType;
	}


	public String getSoAccession() {
		return soAccession;
	}

	public void setSoAccession(String soAccession) {
		this.soAccession = soAccession;
	}
	
	
	public String getConsequenceType() {
		return soName;
	}

	public void setConsequenceType(String consequenceType) {
		this.soName = consequenceType;
	}

	
	public String getTrancriptId() {
		return trancriptId;
	}

	public void setTrancriptId(String trancriptId) {
		this.trancriptId = trancriptId;
	}
	

	public String getAllele() {
		return allele;
	}

	public void setAllele(String allele) {
		this.allele = allele;
	}
	

	public String getFeatureType() {
		return featureType;
	}

	public void setFeatureType(String featureType) {
		this.featureType = featureType;
	}	
	
}
