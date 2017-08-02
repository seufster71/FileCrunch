package org.es.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public abstract class ProcessManager implements Callable<Map<String,Object>> {
	protected List<StatusListener> listeners = new ArrayList<StatusListener>();
	protected FileCrunch appParent = null;
	
	@Override
	public abstract Map<String,Object> call();
	
	public void addListener(StatusListener listener) {
		listeners.add(listener);
	}
	
	protected void notifyListeners(String message) {
        for (StatusListener listener : listeners) {
            listener.updateStatus(message);
        }
    }
}
