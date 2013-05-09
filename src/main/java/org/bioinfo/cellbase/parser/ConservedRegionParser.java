package org.bioinfo.cellbase.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bioinfo.cellbase.lib.common.core.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class ConservedRegionParser {

	private static int CHUNKSIZE = 2000;
	static Gson gson = new Gson();

    public static void parseConservedRegionFilesToJson(Path conservedRegionPath, int chunksize, Path outdirPath) throws IOException {
        Path inGzPath;
        Path outJsonPath;

        List<String> chromosomes = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "X", "Y");
//        List<String> chromosomes = Arrays.asList("22");

        for(String chr : chromosomes){
            Map<Integer, ConservedRegionChunk> conservedRegionChunks = new HashMap<>();

            inGzPath = getConservedRegionPath(conservedRegionPath.resolve(Paths.get("phastCons")), chr);
            System.out.println("processing  "+chr+" "+inGzPath+"...");
            processFile(inGzPath, conservedRegionChunks, "phastCons", chunksize);

            inGzPath = getConservedRegionPath(conservedRegionPath.resolve(Paths.get("phylop")), chr);
            System.out.println("processing  "+chr+" "+inGzPath+"...");
            processFile(inGzPath, conservedRegionChunks, "phylop", chunksize);

            outJsonPath = outdirPath.resolve("conservation_"+chr+".json");
            if(Files.exists(outJsonPath)){
                Files.delete(outJsonPath);
            }

            System.out.println("writing file for chromosome "+chr+"...");
            BufferedWriter bw = Files.newBufferedWriter(outJsonPath, Charset.defaultCharset(), StandardOpenOption.CREATE);
            for (Map.Entry<Integer, ConservedRegionChunk> result : conservedRegionChunks.entrySet()) {
                bw.write(gson.toJson(result.getValue()) + "\n");
            }
            bw.close();
        }
    }


    private static void processFile(Path inGzPath, Map<Integer, ConservedRegionChunk> conservedRegionChunks, String conservedType, int chunksize) throws IOException {
        if(chunksize <= 0) {
            chunksize = CHUNKSIZE;
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(inGzPath))));

        String line = null;
        int start = 0, offset = 0, step = 1, chunkId, position;
        float value;
        String chromosome = "";
        Map<String, String> attributes = new HashMap<>();


        while ((line = br.readLine()) != null) {
            if (!line.startsWith("fixedStep")) {
                position = start + offset;
                offset += step;
                chunkId = position/chunksize;
                value = Float.parseFloat(line.trim());

                if(conservedRegionChunks.get(chunkId)==null){
                    int chunkStart = getChunkStart(chunkId, chunksize);
                    int chunkEnd = getChunkEnd(chunkId, chunksize);
                    conservedRegionChunks.put(chunkId, new ConservedRegionChunk(chromosome, chunkId, chunkStart, chunkEnd));
                }

                Float[] values;
                if(conservedType.toLowerCase().equals("phastcons")){
                    values = conservedRegionChunks.get(chunkId).getPhastCons();
                }else{
                    values = conservedRegionChunks.get(chunkId).getPhylop();
                }

                if(chunkId==0){
                    values[((position%chunksize)-1)] = value;
                }else{
                    values[(position%chunksize)] = value;
                }
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
                System.out.println(line);
            }
        }
        br.close();

    }



    private static int getChunkId(int position, int chunksize){
        if(chunksize <= 0) {
            return position/CHUNKSIZE;
        }else {
            return position/chunksize;
        }
    }
    private static int getChunkStart(int id, int chunksize){
        if(chunksize <= 0) {
            return (id == 0) ? 1 : id*CHUNKSIZE;
        }else {
            return (id==0) ? 1 : id*chunksize;
        }
    }
    private static int getChunkEnd(int id, int chunksize) {
        if(chunksize <= 0) {
            return (id * CHUNKSIZE) + CHUNKSIZE - 1;
        }else {
            return (id*chunksize)+chunksize-1;
        }
    }

    public static Path getConservedRegionPath(Path conservedRegionFolderPath, String chrFile) {
        String file = "";
        String conservedRegion = conservedRegionFolderPath.getFileName().toString();
        switch (conservedRegion.toLowerCase()) {
            case "phastcons":
                file = "chr" + chrFile + ".phastCons46way.primates.wigFix.gz";
                break;
            case "phylop":
                file = "chr" + chrFile + ".phyloP46way.primate.wigFix.gz";
                break;
        }
        return conservedRegionFolderPath.resolve(file);
    }

}
