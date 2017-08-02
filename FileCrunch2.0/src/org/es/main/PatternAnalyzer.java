package org.es.main;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternAnalyzer extends ProcessManager{

	private List<Pattern> patterns = null;
	private List<List<String>> items = null;
	
	public PatternAnalyzer(List<Pattern> patterns, List<List<String>> items){
		this.patterns = patterns;
		this.items = items;
	}

	public void process() {
		for (Pattern p : patterns){
			for (List<String> row : items){
				for (String column : row){
					Matcher matcher = p.matcher(column);
					boolean r = matcher.matches();
					
				}
			}
		}
	}

	@Override
	public Map<String, Object> call() {
		this.process();
		return null;
	}
}
