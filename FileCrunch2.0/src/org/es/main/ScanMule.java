package org.es.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ScanMule extends ProcessManager {

	private List<Pattern> regexPattern = null;
	
	public ScanMule(StatusListener listener){
		this.listeners.add(listener);
	}
	
	public void setRegex(List<String> regexs){
		if (regexs.size() > 0){
			regexPattern = new ArrayList<Pattern>();
			for (String regex:regexs){
				regexPattern.add(Pattern.compile(regex));
			}
		}
	}
	
	protected void process() {
		// loop through rows
		
			// loop through columns
		
				//loop through regex for that column
	}

	@Override
	public Map<String, Object> call() {
		process();
		return null;
	}

}
