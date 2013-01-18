package org.bioinfo.cellbase.parser;

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

public class GenomeSequenceFastaParserTest {


	@Test
	public void testParseToJson() {
		GenomeSequenceFastaParser genomeSequenceFastaParser = new GenomeSequenceFastaParser();
		
		genomeSequenceFastaParser.parseToJson(new File("/home/imedina/cellbase_v3/hsapiens/Homo_sapiens.GRCh37.68.fasta"), new File("/home/imedina/cellbase_v3/hsapiens/hsapiens_genome_sequence.json"));
//		fail("Not yet implemented");
	}

}
