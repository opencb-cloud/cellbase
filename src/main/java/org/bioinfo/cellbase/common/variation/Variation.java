package org.bioinfo.cellbase.common.variation;

import java.util.ArrayList;
import java.util.List;

import javax.swing.plaf.synth.Region;

public class Variation {

	// private String _id;
	private String name;
	private String chromosome;
	private int start;
	private int end;
	private String strand;
	private String type;
	private String reference;
	private String alternate;

	private String species;
	private String assembly;
	private String source;
	private String version;

	private List<SampleGenotype> samples = new ArrayList<SampleGenotype>();

	private List<ConsequenceType> consequenceTypes = new ArrayList<ConsequenceType>();
	private List<PopulationFrequency> populationFrequencies;

	private List<Xref> xrefs = new ArrayList<Xref>();
	
	// Required
	private String featureId;

	// Optional
	private String featureAlias;
	// private String variantSeSeq;
	// private String variantReads;
	private String variantFreq;
	private String validationStates;

	// private List<String> variantEffect

	public Variation() {
	}

	public Variation(String name, String chromosome, String type, int start, int end, String strand, String reference,
			String alternate, String species, String assembly, String source, String version,
			List<SampleGenotype> samples, List<ConsequenceType> consequenceTypes,
			List<PopulationFrequency> populationFrequencies, List<Xref> xrefs, String featureId, String featureAlias,
			String variantFreq, String validationStates) {
		this.name = name;
		this.chromosome = chromosome;
		this.type = type;
		this.start = start;
		this.end = end;
		this.strand = strand;
		this.reference = reference;
		this.alternate = alternate;
		this.species = species;
		this.assembly = assembly;
		this.source = source;
		this.version = version;
		this.samples = samples;
		this.consequenceTypes = consequenceTypes;
		this.populationFrequencies = populationFrequencies;
		this.xrefs = xrefs;
		this.featureId = featureId;
		this.featureAlias = featureAlias;
		this.variantFreq = variantFreq;
		this.validationStates = validationStates;
	}

	public String getSpecies() {
		return species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public String getAssembly() {
		return assembly;
	}

	public void setAssembly(String assembly) {
		this.assembly = assembly;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<SampleGenotype> getSamples() {
		return samples;
	}

	public void setSamples(List<SampleGenotype> samples) {
		this.samples = samples;
	}

	public void setConsequenceTypes(List<ConsequenceType> consequenceTypes) {
		this.consequenceTypes = consequenceTypes;
	}

	public void setXrefs(List<Xref> xrefs) {
		this.xrefs = xrefs;
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
