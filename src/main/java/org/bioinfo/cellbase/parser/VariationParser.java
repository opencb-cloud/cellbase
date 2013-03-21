package org.bioinfo.cellbase.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.bioinfo.cellbase.common.variation.Xref;

public class VariationParser {

	private File path = new File("/home/echirivella/");
	private BufferedReader fabr = null;
	private BufferedReader fgbr = null;
	private BufferedReader pbr = null;
	private BufferedReader rbr = null;
	private BufferedReader tvbr = null;
	private BufferedReader vbr = null;
	private BufferedReader xbr = null;
	private HashMap<String,String> seq_region = new HashMap<String,String>();
	Connection conn = null;
	
	public VariationParser() {

		File[] myFiles = path.listFiles();
		for (File file : myFiles) {
			System.out.println(Paths.get(path.toString(), file.getName())
					.toFile());
			try {

				switch (file.getName()) {
				case "frequency_allele.txt.gz":
					fabr = new BufferedReader(
							new InputStreamReader(new GZIPInputStream(
									new FileInputStream(Paths.get(
											path.toString(), file.getName())
											.toFile()))));
					fabr.readLine();
					break;
				case "frequency_genotype.txt.gz":
					fgbr = new BufferedReader(
							new InputStreamReader(new GZIPInputStream(
									new FileInputStream(Paths.get(
											path.toString(), file.getName())
											.toFile()))));
					fgbr.readLine();
					break;

				case "phenotype.txt.gz":
					pbr = new BufferedReader(
							new InputStreamReader(new GZIPInputStream(
									new FileInputStream(Paths.get(
											path.toString(), file.getName())
											.toFile()))));
					pbr.readLine();
					break;

				case "regulatory.txt.gz":
					rbr = new BufferedReader(
							new InputStreamReader(new GZIPInputStream(
									new FileInputStream(Paths.get(
											path.toString(), file.getName())
											.toFile()))));
					rbr.readLine();
					break;

				case "transcript_variation.txt.gz":
					tvbr = new BufferedReader(
							new InputStreamReader(new GZIPInputStream(
									new FileInputStream(Paths.get(
											path.toString(), file.getName())
											.toFile()))));
					tvbr.readLine();
					break;

				case "variation.txt.gz":
					vbr = new BufferedReader(
							new InputStreamReader(new GZIPInputStream(
									new FileInputStream(Paths.get(
											path.toString(), file.getName())
											.toFile()))));
					vbr.readLine();
					break;

				case "xref.txt.gz":
					xbr = new BufferedReader(
							new InputStreamReader(new GZIPInputStream(
									new FileInputStream(Paths.get(
											path.toString(), file.getName())
											.toFile()))));
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
	
	private void createTables(){
		try {
			String vline = null;

			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:sqlite:mydb.db");
			Statement createTables = conn.createStatement();
			createTables.executeUpdate("CREATE TABLE variation(variation_id INT,name TEXT, ancestral_allele TEXT");
			PreparedStatement ps = conn.prepareStatement("INSERT INTO variation (?,?,?)");
			while ((vline = vbr.readLine())!= null) {
				String[] vlineFields = vline.split("\t");
					  ps.setInt(1, Integer.parseInt(vlineFields[0]));
					  ps.setString(2, vlineFields[2]);
					  ps.setString(3, vlineFields[4]);
					  ps.execute();
			}
		} catch (ClassNotFoundException | SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
				
				//Transcript variation
				while (nameToFind.compareTo(tvline.split("\t")[0]) == 0) {
					System.out.println("..........tvline: " + tvline);
					tvline = tvbr.readLine();
				}
				
				//Xref
				List<Xref> xrefs = new ArrayList<Xref>();
				while (nameToFind.compareTo(xline.split("\t")[0]) == 0) {
					System.out.println("..........xref: " + xline);
					String[] splitXref = xline.split("\t");
					xrefs.add(new Xref(splitXref[1],splitXref[2],splitXref[3]));
					xline = tvbr.readLine();
				}
				
				vline = vbr.readLine();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
