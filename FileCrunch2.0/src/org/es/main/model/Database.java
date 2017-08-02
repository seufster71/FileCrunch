package org.es.main.model;

import java.util.List;

public class Database {

	private String name;
	private List<Table> tables;
	
	public Database(String name){
		this.name = name;
	}

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public List<Table> getTables() { return tables; }
	public void setTables(List<Table> tables) { this.tables = tables;}
	
	public Table getTable(String tableName){
		Table table = null;
		for(Table t : tables){
			if (t.getName().equals(tableName)){
				table = t;
				break;
			}
		}
		return table;
	}
	
}
