package org.bioinfo.cellbase.parser;

import java.io.IOException;
import java.util.List;

import org.bioinfo.formats.exception.FileFormatException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class BiopaxPathwayParserTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testParseToJson() {
		BiopaxPathwayParser pathwayParser = new BiopaxPathwayParser();
		
		try {
			// Load ecnumber map
			pathwayParser.loadECNumbers("/home/rsanchez/ec_tu_uniprot.txt");
			
//			List<DBObject> dbObjList = pathwayParser.parseToJson("/mnt/commons/formats/reactome/Homo sapiens.owl");
			List<DBObject> dbObjList = pathwayParser.parseToJson("/home/rsanchez/Homo sapiens.owl");
			
			Mongo m = new Mongo("localhost", 27017);
			DB db = m.getDB("reactome");
			DBCollection coll = db.getCollection("pathway");
			
			coll.insert(dbObjList);
			
//			long time_start, time_end;
//			time_start = System.currentTimeMillis();
//			File file = new File("/tmp/test2.bson");
//			FileInputStream fin = new FileInputStream(file);
//			byte fileContent[] = new byte[(int)file.length()];
//			fin.read(fileContent);
//			fin.close();
//			BSONObject bsObj = BSON.decode(fileContent);
////			BSON.decode(fileContent);
//			time_end = System.currentTimeMillis();
//			System.out.println("BSON read time: "+ ( time_end - time_start ) +" milliseconds");
//			
//			time_start = System.currentTimeMillis();
//			File file2 = new File("/tmp/test2.json");
//			FileInputStream fin2 = new FileInputStream(file2);
//			byte fileContent2[] = new byte[(int)file.length()];
//			fin2.read(fileContent2);
//			fin2.close();
//			String result = new String (fileContent2);
//			DBObject dbObj = (DBObject)JSON.parse(result);
////			JSON.parse(result);
//			time_end = System.currentTimeMillis();
//			System.out.println("JSON read time: "+ ( time_end - time_start ) +" milliseconds");
			
			
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
