package org.es.main.model;

public class Column {

	private String name;
	private String type;
	private int size;
	private int nullable;
	private Boolean analyze;
	
	public Column(String name, String type, int size, int nullable){
		this.name = name;
		this.type = type;
		this.size = size;
		this.nullable = nullable;
		this.setAnalyze(true);
	}

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getType() { return type; }
	public void setType(String type) { this.type = type; }

	public int getSize() { return size; }
	public void setSize(int size) { this.size = size; }

	public int getNullable() { return nullable; }
	public void setNullable(int nullable) { this.nullable = nullable; }

	public Boolean getAnalyze() { return analyze; }
	public void setAnalyze(Boolean analyze) { this.analyze = analyze; }

	
}
