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
	@Test
	public void testOrangeParseToJson() {
		GenomeSequenceFastaParser genomeSequenceFastaParser = new GenomeSequenceFastaParser();
		
		genomeSequenceFastaParser.parseToJson(new File("/home/fsalavert/orangeParser/Cclementina_v1.0_scaffolds.fasta"), new File("/home/fsalavert/orangeParser/cclementina_genome_sequence.json"));
//		fail("Not yet implemented");
	}

}
