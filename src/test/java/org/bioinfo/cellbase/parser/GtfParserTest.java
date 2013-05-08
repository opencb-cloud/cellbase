package org.bioinfo.cellbase.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.bioinfo.commons.io.utils.IOUtils;
import org.bioinfo.formats.exception.FileFormatException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GtfParserTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testParseToJson() {
		GeneParser coreParser = new GeneParser();
		try {
			File file = new File("/home/imedina/cellbase_v3/hsapiens/hsapiens_core.json");
			file.createNewFile();
			
//			IOUtils.write("/tmp/chr_22.fa", coreParser.getSequenceByChromosome("22", Paths.get("/home/imedina/cellbase_v3/hsapiens/Homo_sapiens.GRCh37.68.fasta")));

			coreParser.parseToJson(new File("/home/imedina/cellbase_v3/hsapiens/Homo_sapiens.GRCh37.71.gtf"), new File("/home/imedina/cellbase_v3/hsapiens/gene_description.txt"), new File("/home/imedina/cellbase_v3/hsapiens/xrefs.txt"), new File("/home/imedina/cellbase_v3/hsapiens/transcript_to_tfbs.txt"), new File("/home/imedina/cellbase_v3/hsapiens/gene_to_mirna.txt"), new File("/home/imedina/cellbase_v3/hsapiens/sequence/"), file);
			
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
