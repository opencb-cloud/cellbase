package org.bioinfo.cellbase.parser;

import java.io.File;

import org.junit.Test;

public class VariationParserTest {

	@Test
	public void test() {

		VariationParser vp = new VariationParser();
		
		vp.parseGvfToJson(new File("/home/echirivella/appl/cellbase/installation-dir/example/Homo_sapiens_incl_consequences.gvf"), new File("/media/data/Homo_sapiens_incl_consequences.Json"));
	}

}
