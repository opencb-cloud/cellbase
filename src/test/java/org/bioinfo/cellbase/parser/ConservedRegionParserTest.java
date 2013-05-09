package org.bioinfo.cellbase.parser;

import com.google.gson.Gson;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

public class ConservedRegionParserTest {
    String USER_HOME = System.getProperty("user.home");

    @Test
    public void testConservedRegions() throws IOException {
        Path indir = Paths.get(USER_HOME + "/cellbase_v3","hsapiens","conservation");
        Path outdir = Paths.get(USER_HOME + "/cellbase_v3","hsapiens","conservation");
        ConservedRegionParser.parseConservedRegionFilesToJson(indir,outdir,2000);
    }

}
