package org.bioinfo.cellbase.parser;

import java.io.File;

import org.junit.Test;

public class VariationParserTest {

	@Test
	public void test() {

		VariationParser vp = new VariationParser();
		
		vp.parseGvfToJson(new File("/home/echirivella/Downloads/Homo_sapiens.gvf"), new File("/home/echirivella/Downloads/Homo_sapiens.Json"));
	}

}
