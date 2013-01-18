package org.bioinfo.cellbase.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class VariationParser {
	Gson gson = new GsonBuilder().create(); // .setPrettyPrinting()

	public VariationParser(){
		
	}
	
	public void parseToJson(File variationFile, File outJsonFile) {
		try {
			String chromosome = "";		
			String line;
			StringBuilder sequenceStringBuilder = new StringBuilder();
			// Java 7 IO code
//			BufferedWriter bw = Files.newBufferedWriter(Paths.get(outJsonFile.toURI()), Charset.defaultCharset(), StandardOpenOption.CREATE);
			BufferedReader br = Files.newBufferedReader(Paths.get(variationFile.toURI()), Charset.defaultCharset());
			while((line = br.readLine()) != null) {

				if(line.startsWith("##")){
					System.out.println("Hemos saltado las primeras lineas");
				}else{
					System.out.println("Estamos en las siguientes");
				}

//				if(!line.startsWith(">")) {
//					sequenceStringBuilder.append(line);
//				}else {
//					// new chromosome
//					// save data
//					if(sequenceStringBuilder.length() > 0) {
//						writeGenomeChunks(chromosome, sequenceStringBuilder.toString(), bw);
//					}
//					// initialize data structures
//					chromosome = line.replace(">", "").split(" ")[0];
//					sequenceStringBuilder.delete(0, sequenceStringBuilder.length());
//				}
			}
			// Last chromosome must be processed
//			writeGenomeChunks(chromosome, sequenceStringBuilder.toString(), bw);
			br.close();
//			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
