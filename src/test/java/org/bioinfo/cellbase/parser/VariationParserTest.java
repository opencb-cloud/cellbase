package org.bioinfo.cellbase.parser;

import java.io.File;

import org.junit.Test;

public class VariationParserTest {

	@Test
	public void test() {

		VariationParser_old vp = new VariationParser_old();
		
//		vp.parseGvfToJson(new File("/home/echirivella/Downloads/Homo_sapiens.gvf"), new File("/home/echirivella/Downloads/Homo_sapiens.Json"));
		vp.parseGvfToJson(new File("/home/imedina/cellbase_v3/hsapiens/Homo_sapiens_incl_consequences.gvf.gz"), new File("/home/echirivella/Downloads/Homo_sapiens.Json"));
	}

}
