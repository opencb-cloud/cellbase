package org.bioinfo.cellbase.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.bioinfo.cellbase.common.core.GenomeSequenceChunk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GenomeSequenceFastaParser {

	private int chunkSize = 2000;
	
	Gson gson = new GsonBuilder().create(); // .setPrettyPrinting()

	public GenomeSequenceFastaParser() {
		
	}
	
	public void parseToJson(File genomeReferenceFastaFile, File outJsonFile) {

		try {
			String chromosome = "";		
			String line;
			StringBuilder sequenceStringBuilder = new StringBuilder();
			// Java 7 IO code
			BufferedWriter bw = Files.newBufferedWriter(Paths.get(outJsonFile.toURI()), Charset.defaultCharset(), StandardOpenOption.CREATE);
			BufferedReader br = Files.newBufferedReader(Paths.get(genomeReferenceFastaFile.toURI()), Charset.defaultCharset());
			while((line = br.readLine()) != null) {
				if(!line.startsWith(">")) {
					sequenceStringBuilder.append(line);
				}else {
					// new chromosome
					// save data
					if(sequenceStringBuilder.length() > 0) {
						writeGenomeChunks(chromosome, sequenceStringBuilder.toString(), bw);
					}
					// initialize data structures
					chromosome = line.replace(">", "").split(" ")[0];
					sequenceStringBuilder.delete(0, sequenceStringBuilder.length());
				}
			}
			// Last chromosome must be processed
			writeGenomeChunks(chromosome, sequenceStringBuilder.toString(), bw);
			br.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void writeGenomeChunks(String chromosome, String sequence, BufferedWriter bw) throws IOException {
		int chunkId = 0;
		int start = 1;
		int end = chunkSize - 1;
		String chunkSequence;
		List<GenomeSequenceChunk> genomeSequenceChunks = new ArrayList<>();
		while(start < sequence.length()) {
			// First chunk of the chromosome
			if(start == 1) {
				// First chunk contains chunkSize-1 nucleotides as index start at position 1 but must end at 1999
				chunkSequence = sequence.substring(start-1, chunkSize-1);
				genomeSequenceChunks.add(new GenomeSequenceChunk(chromosome, chunkId, start, end, chunkSequence));
				start += chunkSize - 1;
			}else {
				// Regular chunk
				if((start+chunkSize) < sequence.length()) {
					chunkSequence = sequence.substring(start-1, start + chunkSize - 1);									
					genomeSequenceChunks.add(new GenomeSequenceChunk(chromosome, chunkId, start, end, chunkSequence));
					start += chunkSize;
				}else {
					// Last chunk of the chromosome
//					System.out.println("=>"+sequence.length());
					chunkSequence = sequence.substring(start-1, sequence.length());
					genomeSequenceChunks.add(new GenomeSequenceChunk(chromosome, chunkId, start, sequence.length(), chunkSequence));
					start = sequence.length();
				}
			}
			end = start + chunkSize -1;
			chunkId++;
		}
		
		// Process all onjects and convert them into JSON format
		for(GenomeSequenceChunk gsc: genomeSequenceChunks) {
			bw.write(gson.toJson(gsc)+"\n");
		}
		
		genomeSequenceChunks.clear();
	}

}
