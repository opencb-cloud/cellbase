package org.bioinfo.cellbase.parser;

import java.io.File;
import java.io.IOException;

import org.bioinfo.formats.exception.FileFormatException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Gff3ParserTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testParseToJson() throws SecurityException, NoSuchMethodException, IOException, FileFormatException {
		GeneParser geneParser = new GeneParser();
		File outJsonFile = new File("/home/fsalavert/orangeParser/cclementina_core.json");
		// if(outJsonFile.exists()){
		File Gff3File = new File("/home/fsalavert/orangeParser/Cclementina_v1.0_gene.gff3");
		File geneDescriptionFile = null;
		File xrefsFile = null;
		geneParser.parseGff3ToJson(Gff3File, geneDescriptionFile, xrefsFile, outJsonFile);
		// }
	}

}
