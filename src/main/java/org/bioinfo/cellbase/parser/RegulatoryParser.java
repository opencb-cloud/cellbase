package org.bioinfo.cellbase.parser;

import org.bioinfo.cellbase.lib.common.GenericFeature;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * User: fsalavert
 * Date: 4/10/13
 * Time: 10:14 AM
 */

public class RegulatoryParser {

    public static List<String> queryChromosomes(Path dbPath, String tableName){
        List<String> chromosomes = new ArrayList<>();
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath.toString());

            Statement query = conn.createStatement();
            ResultSet rs = query.executeQuery("select distinct(seqname) from "+tableName+" where seqname like 'chr%'");

            while (rs.next()) {
                chromosomes.add(rs.getString(1).replace("chr",""));
            }
            conn.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return chromosomes;
    }

    public static List<GenericFeature> queryChromosomesRegulatoryDB(Path dbPath, String tableName, String chromosome) {
        Connection conn = null;
        List<GenericFeature> genericFeatures = new ArrayList<>();
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath.toString());

            Statement query = conn.createStatement();
            ResultSet rs = query.executeQuery("select * from " + tableName + " where seqname='chr"+chromosome+"'");
            while (rs.next()) {
                genericFeatures.add(getGenericFeature(rs, tableName));
            }
            conn.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return genericFeatures;
    }

    public static List<GenericFeature> queryRegulatoryDB(Path dbPath, String tableName, String chrFile, int start, int end) {
        Connection conn = null;
        List<GenericFeature> genericFeatures = new ArrayList<>();
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath.toString());

            Statement query = conn.createStatement();
            ResultSet rs = query.executeQuery("select * from " + tableName + " where start<=" + end + " AND end>=" + start);

            while (rs.next()) {
                genericFeatures.add(getGenericFeature(rs, tableName));
            }
            conn.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return genericFeatures;
    }

    private static GenericFeature getGenericFeature(ResultSet rs, String tableName) throws SQLException {
        GenericFeature genericFeature = null;
        switch (tableName.toLowerCase()) {
            case "annotated_features":
                genericFeature = getAnnotatedFeature(rs);
                break;
            case "regulatory_features_multicell":
                genericFeature = getRegulatoryFeature(rs);
                break;
            case "moti_features":
                genericFeature = getMotiFeature(rs);
                break;
            case "mirna_uniq":
                genericFeature = getMirnaFeature(rs);
                break;
        }
        return genericFeature;
    }

    private static GenericFeature getAnnotatedFeature(ResultSet rs)throws SQLException  {
//   GFF     https://genome.ucsc.edu/FAQ/FAQformat.html#format3
        GenericFeature genericFeature = new GenericFeature();
        Map<String, String> groupFields = getGroupFields(rs.getString(9));

        genericFeature.setChromosome(rs.getString(1));
        genericFeature.setSource(rs.getString(2));
        genericFeature.setFeatureType(rs.getString(3));
        genericFeature.setStart(rs.getInt(4));
        genericFeature.setEnd(rs.getInt(5));
        genericFeature.setScore(rs.getString(6));
        genericFeature.setStrand(rs.getString(7));
        genericFeature.setFrame(rs.getString(8));

        genericFeature.setName(groupFields.get("name"));
        genericFeature.setAlias(groupFields.get("alias"));
        genericFeature.setClassStr(groupFields.get("class"));
        genericFeature.getCellTypes().add(groupFields.get("cell_type"));

        return genericFeature;
    }

    private static GenericFeature getRegulatoryFeature(ResultSet rs)throws SQLException  {
//   GFF     https://genome.ucsc.edu/FAQ/FAQformat.html#format3
        GenericFeature genericFeature = new GenericFeature();
        Map<String, String> groupFields = getGroupFields(rs.getString(9));
        rs.getString(1);
        rs.getString(2);
        rs.getString(3);
        rs.getInt(4);
        rs.getInt(5);
        rs.getString(6);
        rs.getString(7);
        rs.getString(8);
        rs.getString(9);
        return genericFeature;
    }

    private static GenericFeature getMotiFeature(ResultSet rs) throws SQLException {
//   GFF     https://genome.ucsc.edu/FAQ/FAQformat.html#format3
        GenericFeature genericFeature = new GenericFeature();
        Map<String, String> groupFields = getGroupFields(rs.getString(9));

        genericFeature.setChromosome(rs.getString(1));
        genericFeature.setSource(rs.getString(2));
        genericFeature.setFeatureType(rs.getString(3));
        genericFeature.setStart(rs.getInt(4));
        genericFeature.setEnd(rs.getInt(5));
        genericFeature.setScore(rs.getString(6));
        genericFeature.setStrand(rs.getString(7));
        genericFeature.setFrame(rs.getString(8));

        genericFeature.setName(groupFields.get("name"));

        return genericFeature;
    }

    private static GenericFeature getMirnaFeature(ResultSet rs) throws SQLException {
//   GFF     https://genome.ucsc.edu/FAQ/FAQformat.html#format3
        GenericFeature genericFeature = new GenericFeature();

        genericFeature.setChromosome(rs.getString(1));
        genericFeature.setSource(rs.getString(2));
        genericFeature.setFeatureType(rs.getString(3));
        genericFeature.setStart(rs.getInt(4));
        genericFeature.setEnd(rs.getInt(5));
        genericFeature.setScore(rs.getString(6));
        genericFeature.setStrand(rs.getString(7));
        genericFeature.setFrame(rs.getString(8));

        return genericFeature;
    }

    private static Map<String, String> getGroupFields (String group) {
        //process group column
        Map<String, String> groupFields = new HashMap<>();
        String[] attributeFields = group.split("; ");
        String[] attributeKeyValue;
        for (String attributeField : attributeFields) {
            attributeKeyValue = attributeField.trim().split("=");
            groupFields.put(attributeKeyValue[0].toLowerCase(), attributeKeyValue[1]);
        }
        return groupFields;
    }


    public static void createSQLiteRegulatoryFiles(Path filePath, String tableName, List<String> columnNames, List<String> columnTypes, boolean gzip) throws ClassNotFoundException, IOException, SQLException {
        int LIMITROWS = 100000;
        int BatchCount = 0;

        Path dbPath = Paths.get(filePath.toString() + ".db");
        BufferedReader br;
        if (gzip) {
            br = new BufferedReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(filePath))));
        } else {
            br = Files.newBufferedReader(filePath, Charset.defaultCharset());
        }

        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath.toString());
        conn.setAutoCommit(false);//Set false to perform commits manually and increase performance on insertion

        //Create table query
        Statement createTables = conn.createStatement();

        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("CREATE TABLE if not exists " + tableName + "(");
        for (int i = 0; i < columnNames.size(); i++) {//columnNames and columnTypes must have same size
            sbQuery.append("'" + columnNames.get(i) + "' " + columnTypes.get(i) + ",");
        }
        sbQuery.deleteCharAt(sbQuery.length() - 1);
        sbQuery.append(")");

        System.out.println(sbQuery.toString());
        createTables.executeUpdate(sbQuery.toString());

        //Prepare insert query
        sbQuery = new StringBuilder();
        sbQuery.append("INSERT INTO " + tableName + "(");
        for (int i = 0; i < columnNames.size(); i++) {
            sbQuery.append("'" + columnNames.get(i) + "',");
        }
        sbQuery.deleteCharAt(sbQuery.length() - 1);
        sbQuery.append(")values (");
        sbQuery.append(repeat("?,", columnNames.size()));
        sbQuery.deleteCharAt(sbQuery.length() - 1);
        sbQuery.append(")");
        System.out.println(sbQuery.toString());

        PreparedStatement ps = conn.prepareStatement(sbQuery.toString());

        //Read file
        String line = null;
        while ((line = br.readLine()) != null) {

            insertByType(ps, getFields(line, tableName), columnTypes);

            //commit batch
            if (BatchCount % LIMITROWS == 0 && BatchCount != 0) {
                ps.executeBatch();
                conn.commit();
            }
            ps.addBatch();
            BatchCount++;
        }
        br.close();

        //Execute last Batch
        ps.executeBatch();
        conn.commit();

        //Create index
        System.out.println("TODO create indices");
        System.out.println("creating indices...");
//        createTables.executeUpdate("CREATE INDEX conserved_region_idx on conserved_region(position)");
        System.out.println("indices created.");

        conn.commit();
        conn.close();
    }

    public static List<String> getFields(String line, String tableName) {
        List<String> fields = new ArrayList<>();
        switch (tableName.toLowerCase()) {
            case "annotated_features":
                fields = getAnnotatedFeaturesFields(line);
                break;
            case "regulatory_features_multicell":
                fields = getRegulatoryFeaturesFields(line);
                break;
            case "moti_features":
                fields = getMotiFeaturesFields(line);
                break;
            case "mirna_uniq":
                fields = getMirnaFeaturesFields(line);
                break;
        }
        return fields;
    }

    public static List<String> getAnnotatedFeaturesFields(String line) {
        String[] fields = line.split("\t");
        return Arrays.asList(fields);
    }

    public static List<String> getRegulatoryFeaturesFields(String line) {
        String[] fields = line.split("\t");
        return Arrays.asList(fields);
    }

    public static List<String> getMotiFeaturesFields(String line) {
        String[] fields = line.split("\t");
        return Arrays.asList(fields);
    }

    public static List<String> getMirnaFeaturesFields(String line) {
        String[] fields = line.split("\t");
        return Arrays.asList(fields);
    }

    public static void insertByType(PreparedStatement ps, List<String> fields, List<String> types) throws SQLException {
        //Datatypes In SQLite Version 3 -> http://www.sqlite.org/datatype3.html
        String raw;
        String type;
        if (types.size() == fields.size()) {
            for (int i = 0; i < fields.size(); i++) {//columnNames and columnTypes must have same size
                int sqliteIndex = i+1;
                raw = fields.get(i);
                type = types.get(i);

                switch (type) {
                    case "INTEGER":
                    case "INT":
                        ps.setInt(sqliteIndex, Integer.parseInt(raw));
                        break;
                    case "REAL":
                        ps.setFloat(sqliteIndex, Float.parseFloat(raw));
                        break;
                    case "TEXT":
                        ps.setString(sqliteIndex, raw);
                        break;
                    default:
                        ps.setString(sqliteIndex, raw);
                        break;
                }
            }
        }

    }

    public static String repeat(String s, int n) {
        if (s == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(s);
        }
        return sb.toString();
    }
}
