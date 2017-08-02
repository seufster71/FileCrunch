package org.es.main;

public class FilterItem {

	private String filter = null;
	private Long count = 0l;
	
	public FilterItem(String filter){
		this.setFilter(filter);
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getFilter() {
		return filter;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Long getCount() {
		return count;
	}
	
	
}
