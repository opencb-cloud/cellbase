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
	
}
