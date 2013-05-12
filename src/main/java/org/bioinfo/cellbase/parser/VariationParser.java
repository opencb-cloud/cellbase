package org.bioinfo.cellbase.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.bioinfo.cellbase.common.variation.TranscriptVariation;
import org.bioinfo.cellbase.common.variation.Variation;
import org.bioinfo.cellbase.common.variation.Xref;

import com.google.gson.Gson;

public class VariationParser {

	private Connection sqlConn = null;

//	private PreparedStatement psVariationFeature, psTranscriptVariation, psVariationSynonym;
	private PreparedStatement psVariationFeature, psTranscriptVariation, psVariationSynonym;
	
	private RandomAccessFile raf,rafvariationFeature,rafTranscriptVariation,rafvariationSynonym;
	
	private int LIMITROWS = 100000;

	public VariationParser() {

	}

	public void parseVariationToJson(String species, String assembly, String source, String version, Path variationFilePath, Path outfileJson) throws IOException, SQLException {
		BufferedReader br = Files.newBufferedReader(variationFilePath.resolve("variation.txt"), Charset.defaultCharset());
		BufferedWriter bw = Files.newBufferedWriter(outfileJson, Charset.defaultCharset());
		Gson gson = new Gson();
		Map<String, List<String>> queryMap = null;
		String[] variationFields = null;
		String[] variationFeatureFields = null;
		String[] transcriptVariationFields = null;
		String[] variationSynonymFields = null;
		StringBuffer sb = new StringBuffer();
		
		Variation variation = null;
		List<TranscriptVariation> transcriptVariation = null;
		List<Xref> xrefs = null;
		
		Map<String, String> seqRegionMap = loadHashSeqRegion(variationFilePath);
		Map<String, String> sourceMap = loadHashSource(variationFilePath); 
		
		int countprocess = 0;
		int variationId = 0;
		String line = null;
		while((line = br.readLine()) != null) {
			variationFields = line.split("\t");
			variationId = Integer.parseInt(variationFields[0]);
			
//			queryMap = queryAllByVariationId(variationId, variationFilePath);
//			long start = System.currentTimeMillis();
			List<String> resultVariationFeature = queryByVariationId(variationId, "variation_feature", variationFilePath);
			
//			System.out.println("\t"+queryMap.get("variation_feature"));
//			System.out.println("\t"+queryMap.get("transcript_variation"));
//			System.out.println("\t"+queryMap.get("variation_synonym"));
//			System.out.println("\n");

			
			if(resultVariationFeature != null && resultVariationFeature.size() > 0) {
				transcriptVariation = new ArrayList<>();
				List<String> resultVariationSynonym = queryByVariationId(variationId, "variation_synonym", variationFilePath);
				variationFeatureFields = resultVariationFeature.get(0).split("\t");

				// TranscriptVariation references to VariationFeature no Variation !!!
				List<String> resultTranscriptVariations = queryByVariationId(Integer.parseInt(variationFeatureFields[0]), "transcript_variation", variationFilePath);
				if(resultTranscriptVariations != null && resultTranscriptVariations.size() > 0) {
					for(String rtv: resultTranscriptVariations) {
						transcriptVariationFields = rtv.split("\t");
						
						TranscriptVariation tv = new TranscriptVariation((transcriptVariationFields[2] != null && !transcriptVariationFields[2].equals("\\N")) ? transcriptVariationFields[2] : ""
								, (transcriptVariationFields[3] != null && !transcriptVariationFields[3].equals("\\N")) ? transcriptVariationFields[3] : "" 
								, (transcriptVariationFields[4] != null && !transcriptVariationFields[4].equals("\\N")) ? transcriptVariationFields[4] : ""
								, Arrays.asList(transcriptVariationFields[5].split(","))
								, (transcriptVariationFields[6] != null && !transcriptVariationFields[6].equals("\\N")) ? Integer.parseInt(transcriptVariationFields[6]) : 0
								, (transcriptVariationFields[7] != null && !transcriptVariationFields[7].equals("\\N")) ? Integer.parseInt(transcriptVariationFields[7]) : 0
								, (transcriptVariationFields[8] != null && !transcriptVariationFields[8].equals("\\N")) ? Integer.parseInt(transcriptVariationFields[8]) : 0
								, (transcriptVariationFields[9] != null && !transcriptVariationFields[9].equals("\\N")) ? Integer.parseInt(transcriptVariationFields[9]) : 0
								, (transcriptVariationFields[10] != null && !transcriptVariationFields[10].equals("\\N")) ? Integer.parseInt(transcriptVariationFields[10]) : 0
								, (transcriptVariationFields[11] != null && !transcriptVariationFields[11].equals("\\N")) ? Integer.parseInt(transcriptVariationFields[11]) : 0
								, (transcriptVariationFields[12] != null && !transcriptVariationFields[12].equals("\\N")) ? Integer.parseInt(transcriptVariationFields[12]) : 0
								, (transcriptVariationFields[13] != null && !transcriptVariationFields[13].equals("\\N")) ? transcriptVariationFields[13] : ""
								, (transcriptVariationFields[14] != null && !transcriptVariationFields[14].equals("\\N")) ? transcriptVariationFields[14] : ""
								, (transcriptVariationFields[15] != null && !transcriptVariationFields[15].equals("\\N")) ? transcriptVariationFields[15] : ""
								, (transcriptVariationFields[16] != null && !transcriptVariationFields[16].equals("\\N")) ? transcriptVariationFields[16] : ""
								, (transcriptVariationFields[17] != null && !transcriptVariationFields[17].equals("\\N")) ? transcriptVariationFields[17] : ""
								, (transcriptVariationFields[18] != null && !transcriptVariationFields[18].equals("\\N")) ? transcriptVariationFields[18] : ""
								, (transcriptVariationFields[19] != null && !transcriptVariationFields[19].equals("\\N")) ? Float.parseFloat(transcriptVariationFields[19]) : 0f 
								, (transcriptVariationFields[20] != null && !transcriptVariationFields[20].equals("\\N")) ? transcriptVariationFields[20] :""
								, (transcriptVariationFields[21] != null && !transcriptVariationFields[21].equals("\\N")) ? Float.parseFloat(transcriptVariationFields[21]) : 0f);
						transcriptVariation.add(tv);
					}
				}
			
				xrefs = new ArrayList<>();
				if(resultVariationSynonym != null && resultVariationSynonym.size() > 0) {
					String arr[];
					for(String rxref: resultVariationSynonym) {					
						variationSynonymFields = rxref.split("\t");
						if(sourceMap.get(variationSynonymFields[3]) != null) {
							arr = sourceMap.get(variationSynonymFields[3]).split(",");
							xrefs.add(new Xref(variationSynonymFields[4], arr[0], arr[1]));							
						}
					}
					
				}
				
				String[] arr;
				if(variationFeatureFields != null && variationFeatureFields[6] != null) {
					arr = variationFeatureFields[6].split("/");				
				}else {
					arr = new String[]{"", ""};
				}
				
				variation = new Variation((variationFields[2] != null && !variationFields[2].equals("\\N")) ? variationFields[2] : "" , seqRegionMap.get(variationFeatureFields[1]), "SNV", (variationFeatureFields != null) ? Integer.parseInt(variationFeatureFields[2]) : 0, (variationFeatureFields != null) ? Integer.parseInt(variationFeatureFields[3]) : 0, variationFeatureFields[4], (arr[0] != null && !arr[0].equals("\\N")) ? arr[0] : "" , (arr[1] != null && !arr[1].equals("\\N")) ? arr[1] : "" , variationFeatureFields[6], species, assembly, source, version, null, transcriptVariation, null, xrefs, "featureId", "featureAlias", "variantFreq", variationFields[3]);

//				System.out.println(gson.toJson(variation));
//				sb.append(gson.toJson(variation)).append("\n");
				countprocess++;
				if(countprocess % 10000 == 0 && countprocess != 0){
					System.out.println("llevamos procesados: " + countprocess);
				}
				bw.write(gson.toJson(variation)+ "\n");
//					sb = new StringBuffer();
//				}
			}
//			System.out.println(count+" "+line);
//			long end = System.currentTimeMillis();
//			System.out.println("Tiempo: " + (end-start));
			
			
		}
		br.close();
		bw.close();
	}
	
	public void connect(Path variationFilePath) throws SQLException, ClassNotFoundException, FileNotFoundException {
//		Class.forName("org.sqlite.JDBC");
		//sqlConn = DriverManager.getConnection("jdbc:sqlite::memory:");
//		sqlConn = DriverManager.getConnection("jdbc:sqlite:" + variationFilePath.toAbsolutePath().toString()+"/variation_tables.db");
		//sqlConn.setAutoCommit(false);
		
		rafvariationFeature =new RandomAccessFile(variationFilePath.resolve("variation_feature.txt").toFile(), "r"); 
		rafTranscriptVariation =new RandomAccessFile(variationFilePath.resolve("transcript_variation.txt").toFile(), "r");
		rafvariationSynonym =new RandomAccessFile(variationFilePath.resolve("variation_synonym.txt").toFile(), "r");
		
		psVariationFeature = sqlConn.prepareStatement("select offset from variation_feature where variation_id = ? order by offset ASC ");
		psTranscriptVariation = sqlConn.prepareStatement("select offset from transcript_variation where variation_id = ? order by offset ASC ");
		psVariationSynonym = sqlConn.prepareStatement("select offset from variation_synonym where variation_id = ? order by offset ASC ");
	}

	public void disconnect() throws SQLException {
		if(sqlConn != null && !sqlConn.isClosed()) {
			psVariationFeature.close();
			psTranscriptVariation.close();
			psVariationSynonym.close();

			sqlConn.close();		
		}
	}

	public void createVariationDatabase(Path variationFilePath) {
		try {

			if(!Files.exists(variationFilePath.resolve("variation_tables.db"))) {
				Class.forName("org.sqlite.JDBC");
				sqlConn = DriverManager.getConnection("jdbc:sqlite::memory:");
		//		sqlConn = DriverManager.getConnection("jdbc:sqlite:"+variationFilePath.toAbsolutePath().toString()+"/variation_tables.db");
				
				sqlConn.setAutoCommit(false);

				createTable(5, variationFilePath.resolve("variation_feature.txt"), "variation_feature");
				createTable(1, variationFilePath.resolve("transcript_variation.txt"), "transcript_variation");
				createTable(1, variationFilePath.resolve("variation_synonym.txt"), "variation_synonym");
				
//				psVariationFeature = sqlConn.prepareStatement("select offset from variation_feature where variation_id = ? order by offset ASC ");

//				sqlConn.close();		
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	public List<String> queryByVariationId(int variationId, String tableName, Path variationFilePath) throws IOException, SQLException {
		// First query SQLite to get offset position
		List<Long> offsets = new ArrayList<Long>();
		//		PreparedStatement pst = sqlConn.statement(sql)
		//		ResultSet rs = pst.executeQuery("select offset from "+tableName+" where variation_id = " + variationId + "");
		ResultSet rs = null;
//		long start = System.currentTimeMillis();
		switch(tableName) {
		case "variation_feature":
			psVariationFeature.setInt(1, variationId);
			rs = psVariationFeature.executeQuery();
			raf = rafvariationFeature; 
			break;
		case "transcript_variation":
			psTranscriptVariation.setInt(1, variationId);
			rs = psTranscriptVariation.executeQuery();
			raf = rafTranscriptVariation;
			break;
		case "variation_synonym":
			psVariationSynonym.setInt(1, variationId);
			rs = psVariationSynonym.executeQuery();
			raf = rafvariationSynonym;
			break;
		}
//		long end = System.currentTimeMillis();
//		System.out.println("Tiempo: " + (end-start)/1000);
		
		while (rs.next()) {
			offsets.add(rs.getLong(1));
		}
		Collections.sort(offsets);
		// Second go to file
		String line = null;
		List<String> results = new ArrayList<>();
		if(offsets.size() > 0) {
//			RandomAccessFile raf = new RandomAccessFile(variationFilePath.resolve(tableName+".txt").toFile(), "r");
			for(Long offset: offsets){
				if(offset >= 0) {
					raf.seek(offset);
					line = raf.readLine();
					if(line != null) {
						results.add(line);
					}
				}
			}
		}
		return results;
	}

	public Map<String, List<String>> queryAllByVariationId(int variationId, Path variationFilePath) throws IOException, SQLException {
		
		List<String> tables = Arrays.asList("variation_feature", "transcript_variation", "variation_synonym");
		Map<String, List<String>> resultMap = new HashMap<String, List<String>>();
		List<Long> offsets;
		for(String table: tables) {

			// First query SQLite to get offset position
			offsets = new ArrayList<Long>();
			//		PreparedStatement pst = sqlConn.statement(sql)
			//		ResultSet rs = pst.executeQuery("select offset from "+tableName+" where variation_id = " + variationId + "");
			ResultSet rs = null;
			switch(table) {
			case "variation_feature":
				psVariationFeature.setInt(1, variationId);
				rs = psVariationFeature.executeQuery();
				break;
			case "transcript_variation":
				psTranscriptVariation.setInt(1, variationId);
				rs = psTranscriptVariation.executeQuery();
				break;
			case "variation_synonym":
				psVariationSynonym.setInt(1, variationId);
				rs = psVariationSynonym.executeQuery();
				break;
			}
			
			while (rs.next()) {
				offsets.add(rs.getLong(1));
			}
			
			// Second go to file
			String line = null;
			List<String> results = new ArrayList<>();
			if(offsets.size() > 0) {
				RandomAccessFile raf = new RandomAccessFile(variationFilePath.resolve(table+".txt").toFile(), "r");
				for(Long offset: offsets){
					if(offset >= 0) {
						raf.seek(offset);
						line = raf.readLine();
						if(line != null) {
							results.add(line);					
						}
					}
				}
				raf.close();
			}
			resultMap.put(table, results);
		}
		return resultMap;
	}
	
	
	private void createTable(int columnIndex, Path variationFilePath, String tableName) throws SQLException, IOException {
		Statement createTables = sqlConn.createStatement();
		
		// A table containing offset for files
		createTables.executeUpdate("CREATE TABLE if not exists "+tableName+"(" + "variation_id INT , offset BIGINT)");

		PreparedStatement ps = sqlConn.prepareStatement("INSERT INTO "+tableName+"(variation_id, offset) values (?, ?)");

		long offset = 0;
		int count = 0;
		String[] fields = null;
		String line = null;
		BufferedReader br = Files.newBufferedReader(variationFilePath, Charset.defaultCharset());
		while ((line = br.readLine()) != null) {
			fields = line.split("\t");

			ps.setInt(1, Integer.parseInt(fields[columnIndex])); // motif_feature_id
			ps.setLong(2, offset); // seq_region_id
			ps.addBatch();
			count++;

			if (count % LIMITROWS == 0 && count != 0) {
				ps.executeBatch();
				sqlConn.commit();
			}

			offset += line.length() + 1;
		}
		br.close();

		ps.executeBatch();
		sqlConn.commit();

		Statement stm = sqlConn.createStatement();
		stm.executeUpdate("CREATE INDEX "+tableName+"_idx on "+tableName+"(variation_id)");
		sqlConn.commit();
	}
	
	private Map<String, String> loadHashSeqRegion(Path variationFilePath) {
		Map<String, String> seqRegion = new HashMap<String, String>();
		try {
			File seqRegionFile = variationFilePath.resolve("seq_region.txt.gz").toFile();
			if (seqRegionFile.exists()) {
				BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(seqRegionFile))));
				String readLine;
				while ((readLine = br.readLine()) != null) {
					String[] readLineFields = readLine.split("\t");
					seqRegion.put(readLineFields[0], readLineFields[1]);
				}
//				System.out.println("loadHashSeqRegion: " + seqRegion.size());
				br.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return seqRegion;
	}
	
	private Map<String, String> loadHashSource(Path variationFilePath) {
		Map<String, String> sourceMap = new HashMap<String, String>();
		try {
			File sourceFile = variationFilePath.resolve("source.txt.gz").toFile();

			if (sourceFile.exists()) {
				BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(sourceFile))));
				String readLine;
				while ((readLine = br.readLine()) != null) {
					String[] readLineFields = readLine.split("\t");
					if (readLineFields.length == 7) {
						sourceMap.put(readLineFields[0], readLineFields[1] + "," + readLineFields[2]);
					} else {
						sourceMap.put(readLineFields[0], readLineFields[1] + "," + readLineFields[2]);
					}
				}

//				System.out.println("loadHashSource: " + sourceMap.size());
				br.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sourceMap;
	}
}
