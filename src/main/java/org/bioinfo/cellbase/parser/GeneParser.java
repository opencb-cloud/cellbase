package org.bioinfo.cellbase.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bioinfo.cellbase.common.core.Exon;
import org.bioinfo.cellbase.common.core.Gene;
import org.bioinfo.cellbase.common.core.Transcript;
import org.bioinfo.cellbase.common.core.Xref;
import org.bioinfo.commons.io.TextFileWriter;
import org.bioinfo.commons.io.utils.FileUtils;
import org.bioinfo.commons.io.utils.IOUtils;
import org.bioinfo.formats.core.feature.Gtf;
import org.bioinfo.formats.core.feature.io.GtfReader;
import org.bioinfo.formats.exception.FileFormatException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GeneParser {

//	List<Gene> genes;
	Map<String, Integer> geneDict;
	Map<String, Integer> transcriptDict;
	Map<String, Exon> exonDict;

	public GeneParser() {
		init();
	}

	private void init() {
//		genes = new ArrayList<Gene>(70000);
//		geneDict = new HashMap<String, Integer>(70000);
		transcriptDict = new HashMap<>(250000);
		exonDict = new HashMap<>(8000000);
	}

	public void parseToJson(File getFile, File geneDescriptionFile, File xrefsFile, File outJsonFile) throws IOException, SecurityException, NoSuchMethodException, FileFormatException {
		FileUtils.checkFile(getFile);
		init();

		String geneId;
		String transcriptId;

		Gene gene = null;
		Transcript transcript;
		Exon exon = null;
		int cdna = 1;
		int cds = 1;
		String[] fields;
		
		Map<String, String> geneDescriptionMap = new HashMap<String, String>();
		if(geneDescriptionFile != null && geneDescriptionFile.exists()) {
			List<String> lines = IOUtils.readLines(geneDescriptionFile);
			for(String line: lines) {
				fields = line.split("\t", -1);
				geneDescriptionMap.put(fields[0], fields[1]);
			}
		}
		
		Map<String, ArrayList<Xref>> xrefMap = new HashMap<String, ArrayList<Xref>>();
		if(xrefsFile != null && xrefsFile.exists()) {
			List<String> lines = IOUtils.readLines(xrefsFile);
			for(String line: lines) {
				fields = line.split("\t", -1);
				if(!xrefMap.containsKey(fields[0])) {
					xrefMap.put(fields[0], new ArrayList<Xref>());
				}
				xrefMap.get(fields[0]).add(new Xref(fields[1], fields[2], fields[3], fields[4]));				
			}
		}
		
		TextFileWriter tfw = new TextFileWriter(outJsonFile.getAbsolutePath());
//		tfw.writeStringToFile("[");
		
//		BasicBSONList list = new BasicBSONList();
		int cont = 0;
		Gson gson = new GsonBuilder().create(); // .setPrettyPrinting()
		GtfReader gtfReader = new GtfReader(getFile);		
		Gtf gtf;
		boolean first = false;
		while((gtf = gtfReader.read()) != null) {
			geneId = gtf.getAttributes().get("gene_id");
			transcriptId = gtf.getAttributes().get("transcript_id");

			// Check if gene exist en Map
			if(gene == null || !geneId.equals(gene.getId())) { // !geneDict.containsKey(geneId)
				if(gene != null) { // genes.size()>0
					if(first) {
						tfw.writeStringToFile("\n");						
					}
//					tfw.writeStringToFile(gson.toJson(genes.get(genes.size()-1)));
//					genes.remove(genes.size()-1);
					tfw.writeStringToFile(gson.toJson(gene));
					first = true;
				}
				
				
				gene = new Gene(geneId, gtf.getAttributes().get("gene_name"), gtf.getAttributes().get("gene_biotype"), 
						"KNOWN", gtf.getSequenceName().replaceFirst("chr", ""), gtf.getStart(), gtf.getEnd(), gtf.getStrand(), "Ensembl", geneDescriptionMap.get(geneId), new ArrayList<Transcript>());
//				genes.add(gene);
				
				// Do not change order!!   size()-1 is the index of the gene ID
//				geneDict.put(geneId, genes.size()-1);
			}
			
			// Check if Transcript exist in the Gene Set of transcripts
			if(!transcriptDict.containsKey(transcriptId)) {
				transcript = new Transcript(transcriptId, gtf.getAttributes().get("transcript_name"), 
						gtf.getSource(), "KNOWN", gtf.getSequenceName().replaceFirst("chr", ""), gtf.getStart(), gtf.getEnd(), gtf.getStrand(), 0, 0, 0, 0, 0, "", "", xrefMap.get(transcriptId), new ArrayList<Exon>());
				gene.getTranscripts().add(transcript);
				// Do not change order!!   size()-1 is the index of the transcript ID
				transcriptDict.put(transcriptId, gene.getTranscripts().size()-1);
			}else {
				transcript = gene.getTranscripts().get(transcriptDict.get(transcriptId));
			}

			// At this point gene and transcript objects are set up

			// Update gene and transcript genomic coordinates, start must be the lower, and end the higher
			updateTranscriptAndGeneCoords(transcript, gene, gtf);

			if(gtf.getFeature().equalsIgnoreCase("exon")) {
				exon = new Exon(gtf.getAttributes().get("exon_id"), gtf.getSequenceName().replaceFirst("chr", ""), gtf.getStart(), gtf.getEnd(), gtf.getStrand(), 0, 0, 0, 0, 0, 0, -1, Integer.parseInt(gtf.getAttributes().get("exon_number")));
				transcript.getExons().add(exon);
				exonDict.put(transcript.getId()+"_"+exon.getExonNumber(), exon);
				if(gtf.getAttributes().get("exon_number").equals("1")) {
					cdna = 1;
					cds = 1;
				}else {
					// with every exon we update cDNA length with the previous exon length
					cdna += exonDict.get(transcript.getId()+"_"+(exon.getExonNumber()-1)).getEnd() - exonDict.get(transcript.getId()+"_"+(exon.getExonNumber()-1)).getStart() + 1;
				}
			}else {
				exon = exonDict.get(transcript.getId()+"_"+exon.getExonNumber());
				if(gtf.getFeature().equalsIgnoreCase("CDS")) {
					if(gtf.getStrand().equals("+") || gtf.getStrand().equals("1")) {
						// CDS states the beginning of coding start
						exon.setGenomicCodingStart(gtf.getStart());
						exon.setGenomicCodingEnd(gtf.getEnd());

						// cDNA coordinates
						exon.setCdnaCodingStart(gtf.getStart()-exon.getStart()+cdna);
						exon.setCdnaCodingEnd(gtf.getEnd()-exon.getStart()+cdna);

						exon.setCdsStart(cds);
						exon.setCdsEnd(gtf.getEnd()-gtf.getStart()+cds);

						// increment in the coding length
						cds += gtf.getEnd()-gtf.getStart()+1;

						// phase calculation
						if(gtf.getStart() == exon.getStart()) {
							// retrieve previous exon if exists
							if(exonDict.get(transcript.getId()+"_"+(exon.getExonNumber()-1)) != null) {
								Exon e = exonDict.get(transcript.getId()+"_"+(exon.getExonNumber()-1));
								if(e.getPhase() == -1) {
									exon.setPhase((e.getCdnaCodingEnd()-e.getCdnaCodingStart()+1)%3); // (prev-phase+1)%3
								}else {
									exon.setPhase(((e.getCdnaCodingEnd()-e.getCdnaCodingStart()+1)%3+e.getPhase())%3);	// (prev-phase+current-phase+1)%3
								}
							}else {
								// if it is the first exon then we just take the frame
								if(gtf.getFrame().equals("0")) {
									exon.setPhase(Integer.parseInt(gtf.getFrame()));									
								}else {
									if(gtf.getFrame().equals("1")) {
										exon.setPhase(2);
									}else {
										exon.setPhase(1);
									}
								}
							}
						}else {
							// if coding start and genomic start is different then there is UTR: -1
							exon.setPhase(-1);
						}

						transcript.setGenomicCodingStart(gtf.getStart());
						transcript.setGenomicCodingEnd(gtf.getEnd());
						// only first time
						if(transcript.getCdnaCodingStart() == 0) {
							transcript.setCdnaCodingStart(gtf.getStart()-exon.getStart()+cdna);						
						}
					// strand -
					}else {
						// CDS states the beginning of coding start
						exon.setGenomicCodingStart(gtf.getStart());
						exon.setGenomicCodingEnd(gtf.getEnd());

						// cDNA coordinates
						exon.setCdnaCodingStart(exon.getEnd()-gtf.getEnd()+cdna);
						exon.setCdnaCodingEnd(exon.getEnd()-gtf.getStart()+cdna);

						exon.setCdsStart(cds);
						exon.setCdsEnd(gtf.getEnd()-gtf.getStart()+cds);

						// increment in the coding length
						cds += gtf.getEnd()-gtf.getStart()+1;

						// phase calculation
						if(gtf.getEnd() == exon.getEnd()) {
							// retrieve previous exon if exists
							if(exonDict.get(transcript.getId()+"_"+(exon.getExonNumber()-1)) != null) {
								Exon e = exonDict.get(transcript.getId()+"_"+(exon.getExonNumber()-1));
								if(e.getPhase() == -1) {
									exon.setPhase((e.getCdnaCodingEnd()-e.getCdnaCodingStart()+1)%3); // (prev-phase+1)%3
								}else {
									exon.setPhase(((e.getCdnaCodingEnd()-e.getCdnaCodingStart()+1)%3+e.getPhase())%3);	// (prev-phase+current-phase+1)%3
								}
							}else {
								// if it is the first exon then we just take the frame
								if(gtf.getFrame().equals("0")) {
									exon.setPhase(Integer.parseInt(gtf.getFrame()));									
								}else {
									if(gtf.getFrame().equals("1")) {
										exon.setPhase(2);
									}else {
										exon.setPhase(1);
									}
								}						
							}
						}else {
							// if coding start and genomic start is different then there is UTR: -1
							exon.setPhase(-1);
						}

						transcript.setGenomicCodingStart(gtf.getStart());
						transcript.setGenomicCodingEnd(gtf.getEnd());
						// only first time
						if(transcript.getCdnaCodingStart() == 0) {
							transcript.setCdnaCodingStart(gtf.getStart()-exon.getStart()+cdna);						
						}
					}

					// no strand deppendent
					transcript.setProteinID(gtf.getAttributes().get("protein_id"));
				}

				if(gtf.getFeature().equalsIgnoreCase("start_codon")) {
					// nothing to do
				}

				if(gtf.getFeature().equalsIgnoreCase("stop_codon")) {
					if(exon.getStrand().equals("+")) {
						// we need to increment 3 nts, the stop_codon length.
						exon.setGenomicCodingEnd(gtf.getEnd());
						exon.setCdnaCodingEnd(gtf.getEnd()-exon.getStart()+cdna);
						exon.setCdsEnd(gtf.getEnd()-gtf.getStart()+cds);
						cds += gtf.getEnd()-gtf.getStart();

						transcript.setGenomicCodingEnd(gtf.getEnd());
						transcript.setCdnaCodingEnd(gtf.getEnd()-exon.getStart()+cdna);
						transcript.setCdsLength(cds);
					}else {
						// we need to increment 3 nts, the stop_codon length.
						exon.setGenomicCodingStart(gtf.getStart());
						exon.setCdnaCodingEnd(exon.getEnd()-gtf.getStart()+cdna);
						exon.setCdsEnd(gtf.getEnd()-gtf.getStart()+cds);
						cds += gtf.getEnd()-gtf.getStart();

						transcript.setGenomicCodingStart(gtf.getStart());
						transcript.setCdnaCodingEnd(exon.getEnd()-gtf.getStart()+cdna);
						transcript.setCdsLength(cds);
					}
				}
			}
		}
		
		
//		tfw.writeStringToFile("]");
//		tfw.writeLine("\n");
		
		gtfReader.close();
		tfw.close();
//		return gson.toJson(genes);
	}

	private void updateTranscriptAndGeneCoords(Transcript transcript, Gene gene, Gtf gtf) {
		if(transcript.getStart() > gtf.getStart()) {
			transcript.setStart(gtf.getStart());
		}
		if(transcript.getEnd() < gtf.getEnd()) {
			transcript.setEnd(gtf.getEnd());
		}
		if(gene.getStart() > gtf.getStart()) {
			gene.setStart(gtf.getStart());
		}
		if(gene.getEnd() < gtf.getEnd()) {
			gene.setEnd(gtf.getEnd());
		}
	}
}

