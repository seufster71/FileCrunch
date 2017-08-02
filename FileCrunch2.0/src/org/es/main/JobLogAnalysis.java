package org.es.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class JobLogAnalysis extends ProcessManager {
	private TextField inputFileField = null;
	private TextField inputDirectoryField = null;
	private TextField outputDirectoryField = null;
	private ChoiceBox choiceBox = null;
 	private Button analyzeButton = null;
 	private Stage primaryStage = null;
 	private Label label6 = null;
	private String inputPath = null;
	private String inputDirPath = null;
	private String inputFile = null;
	private String outputPath = null;
	private String fileName = null;
	final JobLogAnalysis instance = this;
	private String extention = "txt";
	private String statusMessage = "READY";
	private DateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
	private int beanCount = 0;
	private Date startTime = null;
	private Date endTime = null;
	private long totalTime = 0l;
	private int stackTraceCount = 0;
	
	public JobLogAnalysis(FileCrunch parent) {
		appParent = parent;
	}
	
	public void draw(Stage stage, BorderPane root) {
		
		primaryStage = stage;
		
		choiceBox = new ChoiceBox(FXCollections.observableArrayList(
			    "ARCycle", "Task System", "Task Notification")
				);
		
		
		// Output 
		Label label4 = new Label("Output Directory: ");
		outputDirectoryField = new TextField();
		outputDirectoryField.setPrefWidth(600);
		outputDirectoryField.setOnMouseClicked(new EventHandler<MouseEvent>() {
		
			@Override
			public void handle(MouseEvent arg0) {
				DirectoryChooser directoryChooser = new DirectoryChooser();
				directoryChooser.setTitle("Output Directory");
				if (outputPath != null){
					File defaultDir = new File(outputPath);
					if (defaultDir.isDirectory()){
						directoryChooser.setInitialDirectory(defaultDir);
					}
				}
				File file = directoryChooser.showDialog(primaryStage);
			    if (file != null) {
			       	String path = file.getAbsolutePath();
			       	outputPath = path;
			       	outputDirectoryField.setText(path);
			    }
			}
		 
		});
		
		inputDirectoryField = new TextField();
		inputDirectoryField.setPrefWidth(600);
		inputDirectoryField.setOnMouseClicked(new EventHandler<MouseEvent>() {
		
			@Override
			public void handle(MouseEvent arg0) {
				DirectoryChooser directoryChooser = new DirectoryChooser();
				directoryChooser.setTitle("Input Directory");
				if (inputDirPath != null){
					File defaultDir = new File(inputDirPath);
					if (defaultDir.isDirectory()){
						directoryChooser.setInitialDirectory(defaultDir);
					}
				}
				File file = directoryChooser.showDialog(primaryStage);
			    if (file != null) {
			       	String path = file.getAbsolutePath();
			       	inputDirPath = file.getParent();
			       	inputDirectoryField.setText(path);
			       	if (outputPath == null){
			       		String opath = file.getAbsolutePath();
				       	outputPath = opath;
				       	outputDirectoryField.setText(opath);
			       	}
			    }
			}
		 
		});
		
		inputFileField = new TextField ();
		inputFileField.setPrefWidth(600);
		inputFileField.setOnMouseClicked(new EventHandler<MouseEvent>() {
		
			@Override
			public void handle(MouseEvent arg0) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Input File");
				if (inputPath != null){
					File defaultDir = new File(inputPath);
					if (defaultDir.isDirectory()){
						fileChooser.setInitialDirectory(defaultDir);
					}
				}
				File file = fileChooser.showOpenDialog(primaryStage);
			    if (file != null) {
			    	String fname = file.getName();
			    	String[] tokens = fname.split("\\.(?=[^\\.]+$)");
					fileName = tokens[0];
			        inputFile = file.getAbsolutePath();
			        inputPath = file.getParent();
			        if (outputPath == null){
			        	outputPath = file.getParent();
			        	outputDirectoryField.setText(outputPath);
			        }
			        inputFileField.setText(inputFile);
			    }
			}
			 
		});
		 
		analyzeButton = new Button();
		analyzeButton.setText("Analyze");
		analyzeButton.setOnAction(new EventHandler<ActionEvent>() {
		
			@Override
			public void handle(ActionEvent event) {
				analyzeButton.setDisable(true);
				inputFileField.setDisable(true);
				inputDirectoryField.setDisable(true);
				outputDirectoryField.setDisable(true);
				choiceBox.setDisable(true);
				appParent.getPool().submit(instance);
			}
		});
		    
		Label inputFileLabel = new Label("File: ");
		Label inputDirectoryLabel = new Label("Input Directory: ");
		 Label inputHeaderLabel = new Label("Input");
		 Label outputHeaderLabel = new Label("Output");
		 
		 
		 HBox hb1 = new HBox();
		 hb1.getChildren().addAll(inputFileLabel, inputFileField);
		 hb1.setSpacing(10);
		 
		 HBox hb3 = new HBox();
		 hb3.getChildren().addAll(inputDirectoryLabel, inputDirectoryField);
		 hb3.setSpacing(10);

		 HBox hb2 = new HBox();
		 hb2.getChildren().addAll(label4, outputDirectoryField);
		 hb2.setSpacing(10);
		 
		 label6 = new Label("Status: "); 
		 
		 
		 VBox vb = new VBox();
		 vb.getChildren().addAll(choiceBox,inputHeaderLabel,hb1,hb3,outputHeaderLabel,hb2,analyzeButton,label6);
		
		 root.setCenter(vb);
	}
	
	
	public void process() {
		System.out.println("Running Log Analysis");
		System.out.println("Directory in " + inputDirectoryField.getText());
		System.out.println("Directory out " + outputDirectoryField.getText());
		String output = (String) choiceBox.getSelectionModel().getSelectedItem().toString(); 
		System.out.println("Analysis type " + output);
		String action = null;
		if (output.equals("ARCycle")){
			action = "ActionARCycle";
		} else if (output.equals("Task System")){
			action = "ActionTaskSystem";
		} else if (output.equals("Task Notification")){
			action = "ActionTaskNotification";
		} else {
			return;
		}
		
		
		// get all files in
		File folder = new File(inputDirectoryField.getText());
		File[] listOfFiles = folder.listFiles();
		List<File> filesToProcess = new ArrayList<File>();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				if (listOfFiles[i].getName().contains(action)){
					filesToProcess.add(listOfFiles[i]);
				}
		    }
		}
		listOfFiles = null;
		
		
		for (File file : filesToProcess) {
			FileWriter fw = null;
			String name = file.getName();
			startTime = null;
			endTime = null;
			beanCount = 0;
			totalTime = 0l;
			stackTraceCount = 0;
			
			String fileOut = outputPath + "/" +name + "_analysis." + extention;
			File outFile = new File(fileOut);
			try {
				if (!outFile.exists()) {
					outFile.createNewFile();
				}
				fw = new FileWriter(outFile.getAbsoluteFile());
			}  catch (Exception e) {
				e.printStackTrace();
			}
			
			Map stats = new HashMap();
			BufferedReader br = null;
			
			try {
				
				notifyListeners("Status: Processing File");
				FileReader fr2 = new FileReader(file);
				br = new BufferedReader(fr2);
				
				String line;
				Long lineCount = 1l;
				int offset = 1;
				Long size = 1l;
				
		//		System.out.println("File out " + fileOut );
				
				fw.write("Processing File " + file.getName() +"\n");
				fw.write("Count \t Start Time \t Seconds \n");
				int percentComplete = 0;
				
				
				while ((line = br.readLine()) != null) {
					procesStat(fw, line, action);
					lineCount++;
				}
				br.close();
				fr2.close();
				
				// calc average
				fw.write("Total Time (s) " + totalTime/1000 + "\n");
				fw.write("Average per account (s) " + (totalTime/1000)/beanCount + "\n");
				fw.write("Stack trace count " + stackTraceCount + "\n");
			notifyListeners("Status: Complete");
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("Complete Log Analysis");
		analyzeButton.setDisable(false);
		inputFileField.setDisable(false);
		inputDirectoryField.setDisable(false);
		outputDirectoryField.setDisable(false);
		choiceBox.setDisable(false);
	}

	public void updateStatus(String message) {
		statusMessage = message;
		if (message.contains("Complete")){
			analyzeButton.setDisable(false);
			inputFileField.setDisable(false);
			inputDirectoryField.setDisable(false);
			outputDirectoryField.setDisable(false);
		}
		 Platform.runLater(new Runnable() {
	            @Override
	            public void run() {
	            	label6.setText(" " + statusMessage);
	            }
	          });          
		
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
