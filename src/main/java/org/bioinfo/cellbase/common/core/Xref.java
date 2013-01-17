package org.bioinfo.cellbase.common.core;


public class Xref {
	private String id;
	private String dbNameShort;
	private String dbName;
	private String description;
	public Xref(String id, String dbNameShort, String dbName, String description) {
		this.id = id;
		this.dbNameShort = dbNameShort;
		this.dbName = dbName;
		this.description = description;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDbNameShort() {
		return dbNameShort;
	}
	public void setDbNameShort(String dbNameShort) {
		this.dbNameShort = dbNameShort;
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}


