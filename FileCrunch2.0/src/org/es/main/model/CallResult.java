package org.es.main.model;

import java.util.HashMap;
import java.util.Map;

public class CallResult {
	private Map<String,Object> results = new HashMap<String,Object>();
	
	public CallResult(){};
	
	public void addResult(String key, Object value){
		results.put(key, value);
	}
	
	public Object getResult(String key){
		return results.get(key);
	}
}
