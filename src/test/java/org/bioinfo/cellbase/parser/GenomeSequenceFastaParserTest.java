package org.bioinfo.cellbase.parser;

import com.mongodb.BasicDBList;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class GenomeSequenceFastaParserTest {
    String USER_HOME = System.getProperty("user.home");

    @Test
    public void testParseToJson() {
        GenomeSequenceFastaParser genomeSequenceFastaParser = new GenomeSequenceFastaParser();
        genomeSequenceFastaParser.parseToJson(new File(USER_HOME + "/cellbase_v3/hsapiens/Homo_sapiens.GRCh37.68.fasta"), new File(USER_HOME + "/cellbase_v3/hsapiens/hsapiens_genome_sequence.json"));
//		fail("Not yet implemented");
    }

    @Test
    public void testParseFastaWithConservedRegions() {
        String USER_HOME = System.getProperty("user.home");
        Path phastConsFolderPath  = Paths.get(USER_HOME + "/cellbase_v3/hsapiens/conserved_regions/phastCons/");
        Path phylopConsFolderPath  = Paths.get(USER_HOME + "/cellbase_v3/hsapiens/conserved_regions/phylop/");

        File fasta = new File(USER_HOME + "/cellbase_v3/hsapiens/Homo_sapiens.GRCh37.68.fasta");
        File json = new File(USER_HOME + "/cellbase_v3/hsapiens/hsapiens_genome_sequence.json");
        GenomeSequenceFastaParser genomeSequenceFastaParser = new GenomeSequenceFastaParser();
        genomeSequenceFastaParser.parseToJsonWithConservedRegions(fasta, json,phastConsFolderPath,phylopConsFolderPath);
    }

    @Test
    public void testOrangeParseToJson() {
        GenomeSequenceFastaParser genomeSequenceFastaParser = new GenomeSequenceFastaParser();
        genomeSequenceFastaParser.parseToJson(new File(USER_HOME + "/cellbase_v3/cclementina/Cclementina_v1.0_scaffolds.fasta"), new File(USER_HOME + "/cellbase_v3/cclementina/cclementina_genome_sequence.json"));
    }



    @Test
    public void testConservedRegions() throws IOException, SQLException {
        String chrFile = "22";
        Path filePath = Paths.get(USER_HOME + "/cellbase_v3/hsapiens/conserved_regions/phastCons/chr"+chrFile+".phastCons46way.primates.wigFix.gz");
        Path outPath = Paths.get(USER_HOME + "/cellbase_v3/hsapiens/conserved_regions/phastCons/chr"+chrFile+".phastCons46way.primates.wigFix.json");

        BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(filePath))));
        BufferedWriter bw = Files.newBufferedWriter(outPath, Charset.defaultCharset(), StandardOpenOption.CREATE);

        String line = null;
        int start = 0, offset = 0, step = 1, chunkId, lastChunkId=0, position;
        float value;
        String chromosome = "";
        int chunkSize = 2000;
        String chunkIdStr;
        Map<String, String> attributes = new HashMap<>();

        BasicDBList values = new BasicDBList();
        values.put(chunkSize-1,null);

        while ((line = br.readLine()) != null) {
            if (!line.startsWith("fixedStep")) {
                position = start + offset;
                offset += step;
                chunkId = position/chunkSize;
                if(lastChunkId != chunkId){
                    bw.write("db.genome_sequence.update({chunkId:"+chunkId+",chromosome:'"+chromosome+"'},{$set:{'phastCons':"+values+"}})"+"\n");
                    values = new BasicDBList();
                    values.put(chunkSize-1,null);
                }
                value = Float.parseFloat(line.trim());
                values.put((position%chunkSize),value);
                lastChunkId = chunkId;
            } else {
                offset = 0;
                attributes.clear();
                String[] atrrFields = line.split(" ");
                String[] attrKeyValue;
                for (String attrField : atrrFields) {
                    if (!attrField.equalsIgnoreCase("fixedStep")) {
                        attrKeyValue = attrField.split("=");
                        attributes.put(attrKeyValue[0].toLowerCase(), attrKeyValue[1]);
                    }
                }
                start = Integer.parseInt(attributes.get("start"));
                step = Integer.parseInt(attributes.get("step"));
                chromosome = attributes.get("chrom").replace("chr", "");
                chunkId = start/chunkSize;
                lastChunkId = chunkId;
                System.out.println(line);
            }
        }
        br.close();
        bw.close();
    }


    @Test
    public void testConservedRegionsSQLite() throws IOException, SQLException, ClassNotFoundException,SQLException {
        Path conservedRegionFolderPath = Paths.get(USER_HOME + "/cellbase_v3/hsapiens/conserved_regions/phastCons/");

        GenomeSequenceFastaParser genomeSequenceFastaParser = new GenomeSequenceFastaParser();
        genomeSequenceFastaParser.conservedRegionsSQLite(conservedRegionFolderPath, "phastCons", "22");
    }

    @Test
    public void testQueryConservedRegions() throws IOException, SQLException {
        Path conservedRegionFolderPath = Paths.get(USER_HOME + "/cellbase_v3/hsapiens/conserved_regions/phastCons/");
        GenomeSequenceFastaParser genomeSequenceFastaParser = new GenomeSequenceFastaParser();
        genomeSequenceFastaParser.queryConservedRegions(conservedRegionFolderPath, "phastCons", "22", 16050001, 16051999);
    }
}
