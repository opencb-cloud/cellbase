package org.bioinfo.cellbase.common.variation;

import java.util.ArrayList;
import java.util.List;

public class Variation {

	@SuppressWarnings("unused")
	// private String _id;
	private String name;
	private String chromosome;
	private String type;
	private int start;
	private int end;
	private String strand;
	private String reference;
	private String alternate;

	private List<ConsequenceType> consequenceTypes = new ArrayList<ConsequenceType>();
	private List<PopulationFrequency> populationFrequencies;
	private List<Xref> xrefs = new ArrayList<Xref>();

	// Required
	private String featureId;

	// Optional
	private String featureAlias;
	// private String variantSeSeq;
	// private String variantReads;
	private String total_reads;
	private String zygosity;
	private String variantFreq;
	private String validationStates;

	// private List<String> variantEffect

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

	public void setConsequenceTypes(ConsequenceType consequenceTypes) {
		this.consequenceTypes.add(consequenceTypes);
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

	public String getValidationStates() {
		return validationStates;
	}

	public void setValidationStates(String validationStates) {
		this.validationStates = validationStates;
	}

	public String getReference() {
		return reference;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getAlternate() {
		return alternate;
	}

	public void setAlternate(String alternate) {
		this.alternate = alternate;
	}

	public List<Xref> getXrefs() {
		return xrefs;
	}

	public void setXrefs(Xref xrefs) {
		this.xrefs.add(xrefs);
	}

}
