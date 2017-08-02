package org.es.main;


import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TestArea extends ProcessManager {

 	private Button callDbButton = null;
 	private Stage primaryStage = null;
 	
	final TestArea instance = this;
	private String extention = "txt";
	private String statusMessage = "READY";
	private DateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
	private int beanCount = 0;
	private Date startTime = null;
	private Date endTime = null;
	private long totalTime = 0l;
	private int stackTraceCount = 0;
	private Calldb callDB = null;

	
	public TestArea(FileCrunch parent) {
		appParent = parent;
	}
	
	
	
	public void draw(Stage stage, BorderPane root ) {
		
		primaryStage = stage;
		
		callDbButton = new Button();
		callDbButton.setText("Analyze");
		callDbButton.setOnAction(new EventHandler<ActionEvent>() {
		
			@Override
			public void handle(ActionEvent event) {
				process();
			}
			
		});

		
		 VBox vb = new VBox();
		 vb.getChildren().addAll(callDbButton);
		
		 root.setCenter(vb);
	}
	
	
	public void process() {
		System.out.println("Running process");
		String action = null;
		if (callDB == null){
			callDB = new Calldb();
		}
		//callDB.connect();
	}

	public void updateStatus(String message) {
		statusMessage = message;
		
	}
	
	
	
	
	public void procesStat(FileWriter fw, String line, String action) throws Exception{
		if (line.contains("Starting Processing JobAction "+action)){
			String[] x = line.split(" ");
			Date date = formatTime.parse(x[0]+" "+x[1]);
			startTime = date;
			
		} else if (line.contains("Ending Processing JobAction "+action)) {
			String[] x = line.split(" ");
			Date date = formatTime.parse(x[0]+" "+x[1]);
			endTime = date;
			if (startTime != null && endTime != null){
				beanCount++;
				long millsec = date.getTime() - startTime.getTime();
				totalTime = totalTime + millsec;
				fw.write(beanCount + "\t" +formatTime.format(startTime)+ "\t" + millsec/1000 + "\n");
				
			}		
			
		} else if (line.contains("Stack Trace")){
			stackTraceCount++;
		}

	}



	@Override
	public Map<String, Object> call() {
		this.process();
		return null;
	}
	
	
}
