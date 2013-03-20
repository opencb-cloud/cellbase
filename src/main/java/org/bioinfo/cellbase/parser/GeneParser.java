package org.bioinfo.cellbase.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bioinfo.cellbase.lib.common.core.Exon;
import org.bioinfo.cellbase.lib.common.core.Gene;
import org.bioinfo.cellbase.lib.common.core.Transcript;
import org.bioinfo.cellbase.lib.common.core.Xref;
import org.bioinfo.commons.io.TextFileWriter;
import org.bioinfo.commons.io.utils.FileUtils;
import org.bioinfo.commons.io.utils.IOUtils;
import org.bioinfo.formats.core.feature.Gtf;
import org.bioinfo.formats.core.feature.io.GtfReader;
import org.bioinfo.formats.exception.FileFormatException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GeneParser {

	// List<Gene> genes;
	Map<String, Integer> geneDict;
	Map<String, Integer> transcriptDict;
	Map<String, Exon> exonDict;

	public GeneParser() {
		init();
	}

	private void init() {
		// genes = new ArrayList<Gene>(70000);
		// geneDict = new HashMap<String, Integer>(70000);
		transcriptDict = new HashMap<>(250000);
		exonDict = new HashMap<>(8000000);
	}

	public void parseToJson(File getFile, File geneDescriptionFile, File xrefsFile, File outJsonFile)
			throws IOException, SecurityException, NoSuchMethodException, FileFormatException {
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
		if (geneDescriptionFile != null && geneDescriptionFile.exists()) {
			List<String> lines = IOUtils.readLines(geneDescriptionFile);
			for (String line : lines) {
				fields = line.split("\t", -1);
				geneDescriptionMap.put(fields[0], fields[1]);
			}
		}

		Map<String, ArrayList<Xref>> xrefMap = new HashMap<String, ArrayList<Xref>>();
		if (xrefsFile != null && xrefsFile.exists()) {
			List<String> lines = IOUtils.readLines(xrefsFile);
			for (String line : lines) {
				fields = line.split("\t", -1);
				if (!xrefMap.containsKey(fields[0])) {
					xrefMap.put(fields[0], new ArrayList<Xref>());
				}
				xrefMap.get(fields[0]).add(new Xref(fields[1], fields[2], fields[3], fields[4]));
			}
		}

		TextFileWriter tfw = new TextFileWriter(outJsonFile.getAbsolutePath());
		// tfw.writeStringToFile("[");

		// BasicBSONList list = new BasicBSONList();
		int cont = 0;
		Gson gson = new GsonBuilder().create(); // .setPrettyPrinting()
		GtfReader gtfReader = new GtfReader(getFile);
		Gtf gtf;
		boolean first = false;
		while ((gtf = gtfReader.read()) != null) {
			geneId = gtf.getAttributes().get("gene_id");
			transcriptId = gtf.getAttributes().get("transcript_id");

			// Check if gene exist en Map
			if (gene == null || !geneId.equals(gene.getId())) { // !geneDict.containsKey(geneId)
				if (gene != null) { // genes.size()>0
					if (first) {
						tfw.writeStringToFile("\n");
					}
					// tfw.writeStringToFile(gson.toJson(genes.get(genes.size()-1)));
					// genes.remove(genes.size()-1);
					tfw.writeStringToFile(gson.toJson(gene));
					first = true;
				}

				gene = new Gene(geneId, gtf.getAttributes().get("gene_name"), gtf.getAttributes().get("gene_biotype"),
						"KNOWN", gtf.getSequenceName().replaceFirst("chr", ""), gtf.getStart(), gtf.getEnd(),
						gtf.getStrand(), "Ensembl", geneDescriptionMap.get(geneId), new ArrayList<Transcript>());
				// genes.add(gene);

				// Do not change order!! size()-1 is the index of the gene ID
				// geneDict.put(geneId, genes.size()-1);
			}

			// Check if Transcript exist in the Gene Set of transcripts
			if (!transcriptDict.containsKey(transcriptId)) {
				transcript = new Transcript(transcriptId, gtf.getAttributes().get("transcript_name"), gtf.getSource(),
						"KNOWN", gtf.getSequenceName().replaceFirst("chr", ""), gtf.getStart(), gtf.getEnd(),
						gtf.getStrand(), 0, 0, 0, 0, 0, "", "", xrefMap.get(transcriptId), new ArrayList<Exon>());
				gene.getTranscripts().add(transcript);
				// Do not change order!! size()-1 is the index of the transcript
				// ID
				transcriptDict.put(transcriptId, gene.getTranscripts().size() - 1);
			} else {
				transcript = gene.getTranscripts().get(transcriptDict.get(transcriptId));
			}

			// At this point gene and transcript objects are set up

			// Update gene and transcript genomic coordinates, start must be the
			// lower, and end the higher
			updateTranscriptAndGeneCoords(transcript, gene, gtf);

			if (gtf.getFeature().equalsIgnoreCase("exon")) {
				exon = new Exon(gtf.getAttributes().get("exon_id"), gtf.getSequenceName().replaceFirst("chr", ""),
						gtf.getStart(), gtf.getEnd(), gtf.getStrand(), 0, 0, 0, 0, 0, 0, -1, Integer.parseInt(gtf
								.getAttributes().get("exon_number")));
				transcript.getExons().add(exon);
				exonDict.put(transcript.getId() + "_" + exon.getExonNumber(), exon);
				if (gtf.getAttributes().get("exon_number").equals("1")) {
					cdna = 1;
					cds = 1;
				} else {
					// with every exon we update cDNA length with the previous
					// exon length
					cdna += exonDict.get(transcript.getId() + "_" + (exon.getExonNumber() - 1)).getEnd()
							- exonDict.get(transcript.getId() + "_" + (exon.getExonNumber() - 1)).getStart() + 1;
				}
			} else {
				exon = exonDict.get(transcript.getId() + "_" + exon.getExonNumber());
				if (gtf.getFeature().equalsIgnoreCase("CDS")) {
					if (gtf.getStrand().equals("+") || gtf.getStrand().equals("1")) {
						// CDS states the beginning of coding start
						exon.setGenomicCodingStart(gtf.getStart());
						exon.setGenomicCodingEnd(gtf.getEnd());

						// cDNA coordinates
						exon.setCdnaCodingStart(gtf.getStart() - exon.getStart() + cdna);
						exon.setCdnaCodingEnd(gtf.getEnd() - exon.getStart() + cdna);

						exon.setCdsStart(cds);
						exon.setCdsEnd(gtf.getEnd() - gtf.getStart() + cds);

						// increment in the coding length
						cds += gtf.getEnd() - gtf.getStart() + 1;

						// phase calculation
						if (gtf.getStart() == exon.getStart()) {
							// retrieve previous exon if exists
							if (exonDict.get(transcript.getId() + "_" + (exon.getExonNumber() - 1)) != null) {
								Exon e = exonDict.get(transcript.getId() + "_" + (exon.getExonNumber() - 1));
								if (e.getPhase() == -1) {
									exon.setPhase((e.getCdnaCodingEnd() - e.getCdnaCodingStart() + 1) % 3); // (prev-phase+1)%3
								} else {
									exon.setPhase(((e.getCdnaCodingEnd() - e.getCdnaCodingStart() + 1) % 3 + e
											.getPhase()) % 3); // (prev-phase+current-phase+1)%3
								}
							} else {
								// if it is the first exon then we just take the
								// frame
								if (gtf.getFrame().equals("0")) {
									exon.setPhase(Integer.parseInt(gtf.getFrame()));
								} else {
									if (gtf.getFrame().equals("1")) {
										exon.setPhase(2);
									} else {
										exon.setPhase(1);
									}
								}
							}
						} else {
							// if coding start and genomic start is different
							// then there is UTR: -1
							exon.setPhase(-1);
						}

						if(transcript.getGenomicCodingStart() == 0 || transcript.getGenomicCodingStart() > gtf.getStart()) {
							transcript.setGenomicCodingStart(gtf.getStart());							
						}
						if(transcript.getGenomicCodingEnd() == 0 || transcript.getGenomicCodingEnd() < gtf.getEnd()) {
							transcript.setGenomicCodingEnd(gtf.getEnd());							
						}
						// only first time
						if (transcript.getCdnaCodingStart() == 0) {
							transcript.setCdnaCodingStart(gtf.getStart() - exon.getStart() + cdna);
						}
						// strand -
					} else {
						// CDS states the beginning of coding start
						exon.setGenomicCodingStart(gtf.getStart());
						exon.setGenomicCodingEnd(gtf.getEnd());

						// cDNA coordinates
						exon.setCdnaCodingStart(exon.getEnd() - gtf.getEnd() + cdna);
						exon.setCdnaCodingEnd(exon.getEnd() - gtf.getStart() + cdna);

						exon.setCdsStart(cds);
						exon.setCdsEnd(gtf.getEnd() - gtf.getStart() + cds);

						// increment in the coding length
						cds += gtf.getEnd() - gtf.getStart() + 1;

						// phase calculation
						if (gtf.getEnd() == exon.getEnd()) {
							// retrieve previous exon if exists
							if (exonDict.get(transcript.getId() + "_" + (exon.getExonNumber() - 1)) != null) {
								Exon e = exonDict.get(transcript.getId() + "_" + (exon.getExonNumber() - 1));
								if (e.getPhase() == -1) {
									exon.setPhase((e.getCdnaCodingEnd() - e.getCdnaCodingStart() + 1) % 3); // (prev-phase+1)%3
								} else {
									exon.setPhase(((e.getCdnaCodingEnd() - e.getCdnaCodingStart() + 1) % 3 + e
											.getPhase()) % 3); // (prev-phase+current-phase+1)%3
								}
							} else {
								// if it is the first exon then we just take the
								// frame
								if (gtf.getFrame().equals("0")) {
									exon.setPhase(Integer.parseInt(gtf.getFrame()));
								} else {
									if (gtf.getFrame().equals("1")) {
										exon.setPhase(2);
									} else {
										exon.setPhase(1);
									}
								}
							}
						} else {
							// if coding start and genomic start is different
							// then there is UTR: -1
							exon.setPhase(-1);
						}

						if(transcript.getGenomicCodingStart() == 0 || transcript.getGenomicCodingStart() > gtf.getStart()) {
							transcript.setGenomicCodingStart(gtf.getStart());
						}
						if(transcript.getGenomicCodingEnd() == 0 || transcript.getGenomicCodingEnd() < gtf.getEnd()) {
							transcript.setGenomicCodingEnd(gtf.getEnd());
						}
						// only first time
						if (transcript.getCdnaCodingStart() == 0) {
							transcript.setCdnaCodingStart(gtf.getStart() - exon.getStart() + cdna);
						}
					}

					// no strand deppendent
					transcript.setProteinID(gtf.getAttributes().get("protein_id"));
				}

				if (gtf.getFeature().equalsIgnoreCase("start_codon")) {
					// nothing to do
				}

				if (gtf.getFeature().equalsIgnoreCase("stop_codon")) {
					if (exon.getStrand().equals("+")) {
						// we need to increment 3 nts, the stop_codon length.
						exon.setGenomicCodingEnd(gtf.getEnd());
						exon.setCdnaCodingEnd(gtf.getEnd() - exon.getStart() + cdna);
						exon.setCdsEnd(gtf.getEnd() - gtf.getStart() + cds);
						cds += gtf.getEnd() - gtf.getStart();

						transcript.setGenomicCodingEnd(gtf.getEnd());
						transcript.setCdnaCodingEnd(gtf.getEnd() - exon.getStart() + cdna);
						transcript.setCdsLength(cds);
					} else {
						// we need to increment 3 nts, the stop_codon length.
						exon.setGenomicCodingStart(gtf.getStart());
						exon.setCdnaCodingEnd(exon.getEnd() - gtf.getStart() + cdna);
						exon.setCdsEnd(gtf.getEnd() - gtf.getStart() + cds);
						cds += gtf.getEnd() - gtf.getStart();

						transcript.setGenomicCodingStart(gtf.getStart());
						transcript.setCdnaCodingEnd(exon.getEnd() - gtf.getStart() + cdna);
						transcript.setCdsLength(cds);
					}
				}
			}
		}

		// tfw.writeStringToFile("]");
		// tfw.writeLine("\n");

		gtfReader.close();
		tfw.close();
		// return gson.toJson(genes);
	}

	private void updateTranscriptAndGeneCoords(Transcript transcript, Gene gene, Gtf gtf) {
		if (transcript.getStart() > gtf.getStart()) {
			transcript.setStart(gtf.getStart());
		}
		if (transcript.getEnd() < gtf.getEnd()) {
			transcript.setEnd(gtf.getEnd());
		}
		if (gene.getStart() > gtf.getStart()) {
			gene.setStart(gtf.getStart());
		}
		if (gene.getEnd() < gtf.getEnd()) {
			gene.setEnd(gtf.getEnd());
		}
	}

	public void parseGff3ToJson(File getFile, File geneDescriptionFile, File xrefsFile, File outJsonFile)
			throws IOException, SecurityException, NoSuchMethodException, FileFormatException {

		Map<String, Gene> genes = new HashMap<String, Gene>();
		Map<String, Transcript> transcripts = new HashMap<String, Transcript>();

		Map<String, String> attributes = new HashMap<>();

		System.out.println("READ FILE START::::::::::::::::::::::::::");
		String line = "";
		BufferedReader br = new BufferedReader(new FileReader(getFile));
		System.out.println(br.readLine());
		while ((line = br.readLine()) != null) {
			if (line.startsWith("#") || !line.contains("\t"))
				continue;

			String fields[] = line.split("\t", -1);
			String group[] = fields[8].split(";");

			String id = group[0].split("=")[1];
			String name = "";
			String parent = "";
			String chromosome = fields[0].replace("_Cc_182", "");
			int start = Integer.parseInt(fields[3]);
			int end = Integer.parseInt(fields[4]);
			String strand = fields[6];
			String feature = fields[2];

			// parsing attributres, column 9
			attributes.clear();
			String[] atrrFields = fields[8].split(";");
			String[] attrKeyValue;
			for (String attrField : atrrFields) {
				attrKeyValue = attrField.split("=");
				attributes.put(attrKeyValue[0].toLowerCase(), attrKeyValue[1]);
			}

			if (feature.equalsIgnoreCase("CDS")) {
				name = "";
				// parent = group[1].split("=")[1];
				parent = attributes.get("parent");
				int phase = Integer.parseInt(fields[7]);
				Transcript t = transcripts.get(parent);

				Exon e = new Exon();
				e.setId(id);
				e.setChromosome(chromosome);
				e.setStart(start);
				e.setEnd(end);
				e.setStrand(strand);
				e.setPhase(phase);

				e.setGenomicCodingStart(start);
				e.setGenomicCodingEnd(end);

				// // just in case...
				// if(t.getExons() == null) {
				// t.setExons(new ArrayList<Exon>());
				// }
				//
				// // before adding
				// if(t.getExons().size() > 0) {
				// if(strand.equals("1") || strand.equals("+")) {
				// Exon lastExon = t.getExons().get(t.getExons().size()-1);
				// if(lastExon.getEnd() == e.getStart()-1) {
				// lastExon.setEnd(e.getEnd());
				// lastExon.setId(e.getId());
				// lastExon.setGenomicCodingStart(e.getStart());
				// lastExon.setGenomicCodingEnd(e.getEnd());
				// }else {
				// t.getExons().add(e);
				// }
				// }else { // negative strand
				//
				// }
				// }else {
				// t.getExons().add(e);
				// }

				t.getExons().add(e);

				// Collections.sort(list, new FeatureComparable());

			}
			if (feature.equalsIgnoreCase("five_prime_UTR") || feature.equalsIgnoreCase("three_prime_UTR")) {

				// name = "";
				// parent = group[1].split("=")[1];
				// FivePrimeUtr fivePrimeUtr = new FivePrimeUtr(id, chromosome,
				// start, end, strand);
				// t.getFivePrimeUtr().add(fivePrimeUtr);
				parent = attributes.get("parent");
				// int phase = Integer.parseInt(fields[7]);
				Transcript t = transcripts.get(parent);

				Exon e = new Exon();
				e.setId(id);
				e.setChromosome(chromosome);
				e.setStart(start);
				e.setEnd(end);
				e.setStrand(strand);
				// e.setPhase(phase);

				e.setGenomicCodingStart(start);
				e.setGenomicCodingEnd(end);
				t.getExons().add(e);

			}
			// if (feature.equalsIgnoreCase("three_prime_UTR")) {
			// name = "";
			// parent = group[1].split("=")[1];

			// Transcript t = transcriptsId.get(parent);
			// ThreePrimeUtr threePrimeUtr = new ThreePrimeUtr(id,
			// chromosome, start, end, strand);
			// t.getThreePrimeUtr().add(threePrimeUtr);
			// }
			if (feature.equalsIgnoreCase("mRNA")) {
				id = group[0].split("=")[1];
				name = group[1].split("=")[1];
				parent = group[4].split("=")[1];

				Transcript tr = new Transcript();
				tr.setExons(new ArrayList<Exon>());
				tr.setXrefs(new ArrayList<Xref>());
				tr.setId(id);
				tr.setName(name);
				tr.setBiotype("");
				tr.setStatus("");
				tr.setChromosome(chromosome);
				tr.setStart(start);
				tr.setEnd(end);
				tr.setGenomicCodingStart(start);
				tr.setGenomicCodingStart(end);
				tr.setStrand(strand);

				transcripts.put(id, tr);
				genes.get(parent).getTranscripts().add(tr);
			}
			if (feature.equalsIgnoreCase("gene")) {

				name = group[1].split("=")[1];
				Gene g = new Gene(id, name, "", "", chromosome, start, end, strand, "JGI", "",
						new ArrayList<Transcript>());
				// g.setTranscripts(new ArrayList<Transcript>());
				// g.setId(id);
				// g.setBiotype("");
				// g.setStatus("");
				// g.setName(name);
				// g.setChromosome(chromosome);
				// g.setStart(start);
				// g.setEnd(end);
				// g.setStrand(strand);

				genes.put(id, g);
			}

		}
		br.close();

		// Reorder
		for (String geneId : genes.keySet()) {
			Gene gene = genes.get(geneId);
			for (Transcript transcript : gene.getTranscripts()) {
				Collections.sort(transcript.getExons(), new FeatureComparable());

				Exon prevExon = null;
				List<Exon> toRemove = new ArrayList<Exon>();
				for (Exon exon : transcript.getExons()) {
					if (prevExon != null) {

						String strand = exon.getStrand();
						if (strand.equals("1") || strand.equals("+")) {

							if (prevExon.getEnd() == exon.getStart() - 1) {
								if (prevExon.getId().contains("five_prime_UTR")) {
									exon.setStart(prevExon.getStart());
//									transcript.setGenomicCodingStart(exon.getGenomicCodingStart());
									toRemove.add(prevExon);
								}
								if (exon.getId().contains("three_prime_UTR")) {
									prevExon.setEnd(exon.getEnd());
//									transcript.setGenomicCodingEnd(prevExon.getGenomicCodingEnd());
									toRemove.add(exon);
								}
							}

						} else { // negative strand

							if (prevExon.getEnd() == exon.getStart() - 1) {
								if (prevExon.getId().contains("three_prime_UTR")) {
									exon.setStart(prevExon.getStart());
//									transcript.setGenomicCodingStart(exon.getGenomicCodingStart());
									toRemove.add(prevExon);
								}
								if (exon.getId().contains("five_prime_UTR")) {
									prevExon.setEnd(exon.getEnd());
//									transcript.setGenomicCodingEnd(prevExon.getGenomicCodingEnd());
									toRemove.add(exon);
								}
							}
						}
					}

					prevExon = exon;
				}
				for (Exon primeUTR : toRemove) {
					transcript.getExons().remove(primeUTR);
				}
				
				//Update genomic coding region start and end on transcripts
				int i = 1;
				Exon e = transcript.getExons().get(0);
				while(e.getId().contains("prime_UTR")){
					e = transcript.getExons().get(i);
					i++;
				}
				transcript.setGenomicCodingStart(e.getGenomicCodingStart());
				
				int exonSize = transcript.getExons().size();
				int j = exonSize-2;
				Exon ee = transcript.getExons().get(exonSize-1);
				while(ee.getId().contains("prime_UTR")){
					ee = transcript.getExons().get(j);
					j--;
				}
				transcript.setGenomicCodingEnd(ee.getGenomicCodingEnd());
				
			}
		}

		Gson gson = new Gson();
		TextFileWriter tfw = new TextFileWriter(outJsonFile.getAbsolutePath());
		System.out.println("");
		System.out.println("START WRITE");
		for (String geneId : genes.keySet()) {
			Gene gene = genes.get(geneId);
			tfw.writeStringToFile(gson.toJson(gene));
			tfw.writeStringToFile("\n");
		}
		tfw.close();

//		System.out.println(gson.toJson(genes.get("Ciclev10007224m.g")));
		System.out.println(gson.toJson(genes.get("Ciclev10008515m.g")));
		System.out.println(gson.toJson(genes.get("Ciclev10007219m.g")));
		System.out.println("END WRITE");
	}

	private class FeatureComparable implements Comparator<Object> {
		@Override
		public int compare(Object exon1, Object exon2) {
			return ((Exon) exon1).getStart() - ((Exon) exon2).getStart();
		}
	}
}
