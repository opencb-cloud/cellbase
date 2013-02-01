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
import java.util.Iterator;
import java.util.List;

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
			double timestart = System.currentTimeMillis();
			int contador = 0;
			String line;
			//List<Variation> container = new ArrayList<Variation>();
			Variation[] variation = null;

			String[] data = new String[9];
			String[] attributesData = null;
			int numberVariantSeq = 0;
			String[] variantSeq = null;
			
			// Java 7 IO code
			BufferedWriter bw = Files.newBufferedWriter(Paths.get(outJsonFile.toURI()), Charset.defaultCharset(),
					StandardOpenOption.CREATE);
			BufferedReader br = Files.newBufferedReader(Paths.get(variationFile.toURI()), Charset.defaultCharset());
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("##")) {
					data = line.split("\t");
					attributesData = data[8].split(";");
					numberVariantSeq = attributesData[1].split("=")[1].split(",").length;
					variation = new Variation[numberVariantSeq];
					for (String string : attributesData) {
						if(string.contains("Variant_seq")){
							variantSeq = string.split("=")[1].split(",");
							break;//Testear este break;
						}
						 
					}
//					System.out.println(attributesData[1].split("=")[1].split(",").length);
					for (int i = 0; i < numberVariantSeq; i++) {
						variation[i] = new Variation();
						variation[i].setChromosome(data[0]);
						variation[i].setType(data[2]);
						variation[i].setStart(Integer.parseInt(data[3]));
						variation[i].setEnd(Integer.parseInt(data[4]));
						variation[i].setStrand(data[6]);
						for (int j = 0; j < attributesData.length; j++) {
							String[] aux = attributesData[j].split("=");
							switch (aux[0].toLowerCase()) {
							case "id":
								variation[i].setFeatureId(aux[1]);
								break;
							case "variant_seq":
								variation[i].setAlternate(aux[1].split(",")[i]);
								break;
							case "variant_effect":
//								System.out.println(aux[1]);
								String[] variantEffect = aux[1].split(" ");
//								System.out.println("sadadasdadas ->>>" +Integer.parseInt(variantEffect[1]));
//								System.out.println("la i vale... >>>>>>" +  i);
								if (Integer.parseInt(variantEffect[1]) == i) {
									switch (variantSeq[Integer.parseInt(variantEffect[1])]) {
									case "-":
										variation[i].setConsequenceTypes(new ConsequenceType(variantEffect[0],
												variantEffect[3], "-", variantEffect[2]));
										break;

									default:

										variation[i].setConsequenceTypes(new ConsequenceType(variantEffect[0],
												variantEffect[3], variantSeq[Integer.parseInt(variantEffect[1])],
												variantEffect[2]));
										break;
									}
								}

								break;
							case "reference_seq":
								variation[i].setReference(aux[1]);
								break;
							case "dbxref":
								if (numberVariantSeq == 1) {
									String[] dbxref = aux[1].split(",", -1);

									String[] fields;
									for (int z = 0; z < dbxref.length; z++) {
										fields = dbxref[z].split(":");
										variation[i].setXrefs(new Xref(fields[0], fields[1]));
									}
								}

								break;
							case "validation_states":
								variation[i].setValidationStates(aux[1]);
								break;
							default:
								break;
							}
						}
						
					}
					for (Variation var : variation) {
						//container.add(var);
						bw.write(gson.toJson(var) + "\n");
					}
				}
				contador++;
				if (contador % 100000 == 0){
					System.out.println((System.currentTimeMillis() - timestart)/1000 + " Segundos");
					System.out.println(contador);
				}
			}

			br.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
