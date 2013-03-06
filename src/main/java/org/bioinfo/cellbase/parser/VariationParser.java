package org.bioinfo.cellbase.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;

public class VariationParser {

	private File path = new File("/home/echirivella/cellbase_v3/chromosome_9/");
	private BufferedReader fabr = null;
	private BufferedReader fgbr = null;
	private BufferedReader pbr = null;
	private BufferedReader rbr = null;
	private BufferedReader tvbr = null;
	private BufferedReader vbr = null;
	private BufferedReader xbr = null;

	public VariationParser() {

		File[] myFiles = path.listFiles();
		for (File file : myFiles) {
			System.out.println(Paths.get(path.toString() ,file.getName()).toFile());
			try {

				switch (file.getName()) {
				case "frequency_allele.txt.sort.gz":
					fabr = new BufferedReader(new InputStreamReader(
							new GZIPInputStream(new FileInputStream(Paths.get(path.toString() ,file.getName()).toFile()))));
					fabr.readLine();
					break;
				case "frequency_genotype.txt.sort.gz":
					fgbr = new BufferedReader(new InputStreamReader(
							new GZIPInputStream(new FileInputStream(Paths.get(path.toString() ,file.getName()).toFile()))));
					fgbr.readLine();
					break;

				case "phenotype.txt.sort.gz":
					pbr = new BufferedReader(new InputStreamReader(
							new GZIPInputStream(new FileInputStream(Paths.get(path.toString() ,file.getName()).toFile()))));
					pbr.readLine();
					break;

				case "regulatory.txt.sort.gz":
					rbr = new BufferedReader(new InputStreamReader(
							new GZIPInputStream(new FileInputStream(Paths.get(path.toString() ,file.getName()).toFile()))));
					rbr.readLine();
					break;

				case "transcript_variation.txt.sort.gz":
					tvbr = new BufferedReader(new InputStreamReader(
							new GZIPInputStream(new FileInputStream(Paths.get(path.toString() ,file.getName()).toFile()))));
					tvbr.readLine();
					break;

				case "variation.txt.sort.gz":
					vbr = new BufferedReader(new InputStreamReader(
							new GZIPInputStream(new FileInputStream(Paths.get(path.toString() ,file.getName()).toFile()))));
					vbr.readLine();
					break;

				case "xref.txt.sort.gz":
					xbr = new BufferedReader(new InputStreamReader(
							new GZIPInputStream(new FileInputStream(Paths.get(path.toString() ,file.getName()).toFile()))));
					xbr.readLine();
					break;

				default:
					break;
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		CoreVariationParser();
	}

	private void CoreVariationParser() {
		String nameToFind = null;
		try {
			String faline = fabr.readLine();
			String fgline = fgbr.readLine();
			String pline = pbr.readLine();
			String rline = rbr.readLine();
			String tvline = tvbr.readLine();
			String vline = vbr.readLine();
			String xline = xbr.readLine();

			while (vline != null) {
				nameToFind = vline.split("\t")[0];
				System.out.println(nameToFind);

				while (nameToFind.compareTo(tvline) == 0) {
					System.out.println("\t tvline: " + tvline);
					tvline = tvbr.readLine();
				}

				vline = vbr.readLine();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
