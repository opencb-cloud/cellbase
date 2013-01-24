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
			int contadorGlobal = 0;
			int contadorProcess = 0;
			String line;
			List<Variation> container = new ArrayList<Variation>();
			Variation variation = null;
			// Java 7 IO code
			BufferedWriter bw = Files.newBufferedWriter(
					Paths.get(outJsonFile.toURI()), Charset.defaultCharset(),
					StandardOpenOption.CREATE);
			BufferedReader br = Files.newBufferedReader(
					Paths.get(variationFile.toURI()), Charset.defaultCharset());
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("##")) {
					variation = new Variation();
					String[] data = line.split("\t");
					String[] attributesData = data[8].split(";");

					variation.setChromosome(data[0]);
					variation.setType(data[2]);
					variation.setStart(Integer.parseInt(data[3]));
					variation.setEnd(Integer.parseInt(data[4]));
					variation.setStrand(data[6]);

					String[] variantSeq = null;
					for (int i = 0; i < attributesData.length; i++) {
						String[] aux = attributesData[i].split("=");

						switch (aux[0].toLowerCase()) {
						case "id":
							variation.setFeatureId(aux[1]);
							break;
						case "variant_seq":
							variantSeq = aux[1].split(",");
							variation.setAlternate(aux[1]);
							break;
						case "variant_effect":
							String[] variantEffect = aux[1].split(" ");
							System.out.println("asdfasfasfads--->" + Integer.parseInt(variantEffect[1]));
							switch (variantSeq[Integer.parseInt(variantEffect[1])]) {
							case "-":
								variation
										.setConsequenceTypes(new ConsequenceType(
												variantEffect[0],
												variantEffect[3], "-",
												variantEffect[2]));
								break;

							default:
								variation
										.setConsequenceTypes(new ConsequenceType(
												variantEffect[0],
												variantEffect[3],
												variantSeq[Integer
														.parseInt(variantEffect[1])],
												variantEffect[2]));
								break;
							}

							break;
						case "reference_seq":
							variation.setReference(aux[1]);
							break;
						case "dbxref":
							String[] dbxref = aux[1].split(",", -1);

							String[] fields;
							for (int j = 0; j < dbxref.length; j++) {
								fields = dbxref[j].split(":");
								variation.setXrefs(new Xref(fields[0],
										fields[1]));
							}

							break;
						case "validation_states":
							variation.setValidationStates(aux[1]);
							break;
						default:
							break;
						}
					}
				}
				container.add(variation);
				bw.write(gson.toJson(variation) + "\n");
			}

			br.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
