package org.bioinfo.cellbase.parser;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProteinParserTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testParseUniprotToJson() {
		String uniprotFileName = "/home/imedina/Downloads/uniprot_sprot/chunks/chunk_1.xml";
		try {
			ProteinParser.parseUniprotToJson(new File(uniprotFileName), "Homo sapiens", new File("/tmp/protein.json"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
