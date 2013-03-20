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

import org.bioinfo.cellbase.common.core.Gene;
import org.bioinfo.cellbase.common.core.GenomeSequenceChunk;
import org.bioinfo.cellbase.lib.common.core.Chromosome;
import org.bioinfo.cellbase.lib.common.core.Cytoband;
import org.bioinfo.cellbase.lib.common.core.InfoStats;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBList;

public class GenomeSequenceFastaParser {

	private int chunkSize = 2000;
	
	Gson gson = new GsonBuilder().create(); // .setPrettyPrinting()

	public GenomeSequenceFastaParser() {
		
	}
	
	public void parseToJson(File genomeReferenceFastaFile, File outJsonFile) {
		/*infoStats*/
		List<Chromosome> chromosomes = new ArrayList<Chromosome>();
		InfoStats infoStats= new InfoStats("cclementine",chromosomes);
		/*infoStats*/
		try {
			String chromosome = "";		
			String line;
			StringBuilder sequenceStringBuilder = new StringBuilder();
			// Java 7 IO code
			BufferedWriter bw = Files.newBufferedWriter(Paths.get(outJsonFile.toURI()), Charset.defaultCharset(), StandardOpenOption.CREATE);
			BufferedReader br = Files.newBufferedReader(Paths.get(genomeReferenceFastaFile.toURI()), Charset.defaultCharset());
			BufferedWriter bw_stats = Files.newBufferedWriter(Paths.get(outJsonFile.getParent()).resolve("cclementina_info_stats.json"), Charset.defaultCharset(), StandardOpenOption.CREATE);
			while((line = br.readLine()) != null) {
				if(!line.startsWith(">")) {
					sequenceStringBuilder.append(line);
				}else {
					
					
					// new chromosome
					// save data
					if(sequenceStringBuilder.length() > 0) {
						writeGenomeChunks(chromosome, sequenceStringBuilder.toString(), bw);
						/*infoStats*/
						int len = sequenceStringBuilder.length();
						/*infoStats*/
						/*infoStats*/
						Chromosome chromosomeObj = new Chromosome();
						chromosomeObj.setName(chromosome);
						/*infoStats*/
						/*infoStats*/
						chromosomeObj.setStart(1);
						chromosomeObj.setEnd(len);
						chromosomeObj.setSize(len);
						chromosomeObj.setIsCircular(0);
						chromosomeObj.setNumberGenes(0);
						List<Cytoband> cytobands = new ArrayList<Cytoband>();
						cytobands.add(new Cytoband("", "clementina", 1, len));
						chromosomeObj.setCytobands(cytobands);
						/*infoStats*/
						/*infoStats*/
						chromosomes.add(chromosomeObj);
						/*infoStats*/
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
			
			/*infoStats*/
			bw_stats.write(gson.toJson(infoStats));
			bw_stats.flush();
			bw_stats.close();
			/*infoStats*/
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
		if(sequence.length()<chunkSize){//chromosome sequence length can be less than chunkSize
			chunkSequence = sequence;
			genomeSequenceChunks.add(new GenomeSequenceChunk(chromosome, 0, start, sequence.length()-1, chunkSequence));
			start += chunkSize - 1;
		}else{
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
		}
		
		// Process all onjects and convert them into JSON format
		for(GenomeSequenceChunk gsc: genomeSequenceChunks) {
			bw.write(gson.toJson(gsc)+"\n");
		}
		
		genomeSequenceChunks.clear();
	}

}
