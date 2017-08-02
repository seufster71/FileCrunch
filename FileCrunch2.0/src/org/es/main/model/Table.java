package org.es.main.model;

import java.util.List;

public class Table {

	private String name;
	private String type;
	private String remarks;
	private List<Column> columns;
	
	public Table(String name, String type, String remarks){
		this.setName(name);
		this.setType(type);
		this.setRemarks(remarks);
	}

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getType() { return type; }
	public void setType(String type) { this.type = type; }

	public String getRemarks() { return remarks; }
	public void setRemarks(String remarks) { this.remarks = remarks; }

	public List<Column> getColumns() { return columns; }
	public void setColumns(List<Column> columns) { this.columns = columns; }
	
	public String getSelect(){
		String select = "";
		if (columns != null){
			select += "SELECT ";
			boolean first = true;
			for (Column column : columns){
				if (!first){
					select += ", ";
				}
				select += column.getName() + " ";
				first = false;
			}
			select += "FROM " + getName();
		}
		
		return select;
	}
	
}
