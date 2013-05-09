package org.bioinfo.cellbase.parser;

import com.google.gson.Gson;
import com.mongodb.BasicDBList;
import org.bioinfo.cellbase.lib.common.GenericFeature;
import org.bioinfo.cellbase.lib.common.GenericFeatureChunk;
import org.bioinfo.cellbase.lib.common.core.ConservedRegionChunk;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class GenomeSequenceFastaParserTest {
    static int CHUNKSIZE = 2000;
    String USER_HOME = System.getProperty("user.home");
    Gson gson = new Gson();

    @Test
    public void testParseToJson() {
        GenomeSequenceFastaParser genomeSequenceFastaParser = new GenomeSequenceFastaParser();
//        genomeSequenceFastaParser.parseToJson(new File(USER_HOME + "/cellbase_v3/hsapiens/Homo_sapiens.GRCh37.71.fasta"), new File(USER_HOME + "/cellbase_v3/hsapiens/hsapiens_genome_sequence.json"));
        
        
        genomeSequenceFastaParser.parseFastaGzipFilesToJson(new File(USER_HOME + "/cellbase_v3/hsapiens/sequence/"), new File(USER_HOME + "/cellbase_v3/hsapiens/hsapiens_genome_sequence_new.json"));
//		fail("Not yet implemented");
    }

//    @Test
//    public void testParseFastaWithConservedRegions() {
//        String USER_HOME = System.getProperty("user.home");
//        Path phastConsFolderPath  = Paths.get(USER_HOME + "/cellbase_v3/hsapiens/conserved_regions/phastCons/");
//        Path phylopConsFolderPath  = Paths.get(USER_HOME + "/cellbase_v3/hsapiens/conserved_regions/phylop/");
//
//        File fasta = new File(USER_HOME + "/cellbase_v3/hsapiens/Homo_sapiens.GRCh37.68.fasta");
//        File json = new File(USER_HOME + "/cellbase_v3/hsapiens/hsapiens_genome_sequence.json");
//        GenomeSequenceFastaParser genomeSequenceFastaParser = new GenomeSequenceFastaParser();
//        genomeSequenceFastaParser.parseToJsonWithConservedRegions(fasta, json,phastConsFolderPath,phylopConsFolderPath);
//    }

////    @Test
//    public void testOrangeParseToJson() {
//        GenomeSequenceFastaParser genomeSequenceFastaParser = new GenomeSequenceFastaParser();
//        genomeSequenceFastaParser.parseToJson(new File(USER_HOME + "/cellbase_v3/cclementina/Cclementina_v1.0_scaffolds.fasta"), new File(USER_HOME + "/cellbase_v3/cclementina/cclementina_genome_sequence.json"));
//    }
//
//
//
////    @Test
//    public void testConservedRegionsSQLite() throws IOException, SQLException, ClassNotFoundException,SQLException {
//        Path conservedRegionFolderPath = Paths.get(USER_HOME + "/cellbase_v3/hsapiens/conserved_regions/phastCons/");
//
//        GenomeSequenceFastaParser genomeSequenceFastaParser = new GenomeSequenceFastaParser();
//        genomeSequenceFastaParser.conservedRegionsSQLite(conservedRegionFolderPath, "phastCons", "22");
//    }
//
////    @Test
//    public void testQueryConservedRegions() throws IOException, SQLException {
//        Path conservedRegionFolderPath = Paths.get(USER_HOME + "/cellbase_v3/hsapiens/conserved_regions/phastCons/");
//        GenomeSequenceFastaParser genomeSequenceFastaParser = new GenomeSequenceFastaParser();
//        genomeSequenceFastaParser.queryConservedRegions(conservedRegionFolderPath, "phastCons", "22", 16050001, 16051999);
//    }



}
