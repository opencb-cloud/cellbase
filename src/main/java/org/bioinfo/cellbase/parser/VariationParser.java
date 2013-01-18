package org.bioinfo.cellbase.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bioinfo.cellbase.common.variation.ConsequenceType;
import org.bioinfo.cellbase.common.variation.Variation;
import org.bioinfo.cellbase.common.variation.Xref;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class VariationParser {
	Gson gson = new GsonBuilder().create(); // .setPrettyPrinting()

	public VariationParser() {

	}

	public void parseGvfToJson(File variationFile, File outJsonFile) {
		try {
			String line;
			List<Variation> container = new ArrayList<Variation>();
			Variation variation = null;
			// Java 7 IO code
			// BufferedWriter bw =
			// Files.newBufferedWriter(Paths.get(outJsonFile.toURI()),
			// Charset.defaultCharset(), StandardOpenOption.CREATE);
			BufferedReader br = Files.newBufferedReader(Paths.get(variationFile.toURI()), Charset.defaultCharset());
			while ((line = br.readLine()) != null) {
				variation = new Variation();
				System.out.println("Creando nueva instancia de Variation");
				if (!line.startsWith("##")) {
//					System.out.println(line);
					String[] data = line.split("\t");
					String[] attributesData = data[8].split(";");

					variation.setChromosome(data[0]);
					variation.setStart(Integer.parseInt(data[3]));
					variation.setEnd(Integer.parseInt(data[4]));
					variation.setStrand(data[6]);
					
					Pattern pattern = Pattern.compile(";");
					
					for (int i = 0; i < attributesData.length; i++) {
						String[] aux = attributesData[i].split("=");
						String[] variantSeq=null;
						System.out.print(aux[0].toLowerCase());
						switch (aux[0].toLowerCase()) {
						case "id":
							System.out.println("\t Estamos en id con ---->" + aux[1]);
							variation.setFeatureId(aux[1]);
							break;
						case "variant_seq":
							System.out.println("\t Estamos en variant_seq con ---->" + aux[1]);
							variantSeq=aux[1].split(",");
							variation.setAlternate(aux[1]);
							break;
						case "variant_effect":
							String[] variantEffect = aux[1].split(" ");
							System.out.println("\t variant_effect con ---->" + aux[1]);
							if(variantSeq != null) {
								variation.setConsequenceTypes(new ConsequenceType(variantEffect[0], variantEffect[3], variantEffect[Integer.parseInt(variantSeq[1])], variantEffect[2]));
							}
							break;
						case "reference_seq":
							System.out.println("\t reference_seq con ---->" + aux[1]);
							variation.setReference(aux[1]);
							break;
						case "dbxref":
							String[] dbxref = aux[1].split(",", -1);
							
							String[] fields;
							System.out.println("\t Estamos en dbxref con ---->" + aux[1]);
							for (int j = 0; j < dbxref.length; j++) {
								fields = dbxref[j].split(":"); 
								variation.setXrefs(new Xref(fields[0], fields[1]));	
							}
							
							break;
						case "validation_states":
							System.out.println("\t Estamos en validation_states con ---->" + aux[1]);
							variation.setValidationStates(aux[1]);
							break;
						default:
							break;
						}
					}
				}
				
			}
			container.add(variation);
			System.out.println(gson.toJson(variation));
			// Last chromosome must be processed
			// writeGenomeChunks(chromosome, sequenceStringBuilder.toString(),
			// bw);
			br.close();
			// bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
