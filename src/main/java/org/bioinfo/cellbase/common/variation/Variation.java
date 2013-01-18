package org.bioinfo.cellbase.common.variation;

import java.util.List;

public class Variation {

	@SuppressWarnings("unused")
	private String _id;
	private String name;
	private String chromosome;
	private int start;
	private int end;
	private String strand;
	
	private List<ConsequenceType> consequenceTypes;
	private List<PopulationFrequency> populationFrequencies;
	
	//Required
	private String featureId;
	
	//Optional
	private String featureAlias;
	private String dbxref;
	private String variantSeq;
	private String referenceSeq;
	private String variantReads;
	private String total_reads;
	private String zygosity;
	private String variantFreq;
	private List<String> variantEffect;
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getChromosome() {
		return chromosome;
	}
	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public String getStrand() {
		return strand;
	}
	public void setStrand(String strand) {
		this.strand = strand;
	}
	public List<ConsequenceType> getConsequenceTypes() {
		return consequenceTypes;
	}
	public void setConsequenceTypes(List<ConsequenceType> consequenceTypes) {
		this.consequenceTypes = consequenceTypes;
	}
	public List<PopulationFrequency> getPopulationFrequencies() {
		return populationFrequencies;
	}
	public void setPopulationFrequencies(List<PopulationFrequency> populationFrequencies) {
		this.populationFrequencies = populationFrequencies;
	}
	public String getFeatureId() {
		return featureId;
	}
	public void setFeatureId(String featureId) {
		this.featureId = featureId;
	}
	public String getFeatureAlias() {
		return featureAlias;
	}
	public void setFeatureAlias(String featureAlias) {
		this.featureAlias = featureAlias;
	}
	public String getDbxref() {
		return dbxref;
	}
	public void setDbxref(String dbxref) {
		this.dbxref = dbxref;
	}
	public String getVariantSeq() {
		return variantSeq;
	}
	public void setVariantSeq(String variantSeq) {
		this.variantSeq = variantSeq;
	}
	public String getReferenceSeq() {
		return referenceSeq;
	}
	public void setReferenceSeq(String referenceSeq) {
		this.referenceSeq = referenceSeq;
	}
	public String getVariantReads() {
		return variantReads;
	}
	public void setVariantReads(String variantReads) {
		this.variantReads = variantReads;
	}
	public String getTotal_reads() {
		return total_reads;
	}
	public void setTotal_reads(String total_reads) {
		this.total_reads = total_reads;
	}
	public String getZygosity() {
		return zygosity;
	}
	public void setZygosity(String zygosity) {
		this.zygosity = zygosity;
	}
	public String getVariantFreq() {
		return variantFreq;
	}
	public void setVariantFreq(String variantFreq) {
		this.variantFreq = variantFreq;
	}
	public List<String> getVariantEffect() {
		return variantEffect;
	}
	public void setVariantEffect(List<String> variantEffect) {
		this.variantEffect = variantEffect;
	}
	
}
