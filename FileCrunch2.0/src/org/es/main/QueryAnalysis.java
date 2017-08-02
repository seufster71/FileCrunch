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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class QueryAnalysis extends ProcessManager {
	private TextField inputFileField = null;
	private TextField inputDirectoryField = null;
	private TextField outputDirectoryField = null;
 	private Button analyzeButton = null;
 	private Stage primaryStage = null;
 	private Label label6 = null;
	private String inputPath = null;
	private String inputDirPath = null;
	private String inputFile = null;
	private String outputPath = null;
	private String fileName = null;
	final QueryAnalysis instance = this;
	private String extention = "txt";
	private String statusMessage = "READY";
	private DateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
	private String runTime = "(\\s)(\\w+) run time = (\\d+)";
	private Pattern runTimePattern = Pattern.compile(runTime);
	private int insertCount = 0;
	private int totalInsertCount = 0;
	private int updateCount = 0;
	private int totalUpdateCount = 0;
	
	//private Pattern insertIntoPattern = Pattern.compile("DEBUG (JDBCData.java:2540) - INSERT INTO");
	
	public QueryAnalysis(FileCrunch parent) {
		appParent = parent;
	}
	
	public void draw(Stage stage, BorderPane root) {
		
		primaryStage = stage;
		// Output 
		Label label4 = new Label("Output Directory: ");
		outputDirectoryField = new TextField();
		outputDirectoryField.setPrefWidth(400);
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
		inputDirectoryField.setPrefWidth(400);
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
			       	
			    }
			}
		 
		});
		
		inputFileField = new TextField ();
		inputFileField.setPrefWidth(400);
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
		 vb.getChildren().addAll(inputHeaderLabel,hb1,hb3,outputHeaderLabel,hb2,analyzeButton,label6);
		
		 root.setCenter(vb);
	}
	
	
	public void process() {
		System.out.println("Running Query Analysis");
		System.out.println("Directory in " + inputDirectoryField.getText());
		System.out.println("Directory out " + outputDirectoryField.getText());
		// get all files in
		File folder = new File(inputDirectoryField.getText());
		File[] listOfFiles = folder.listFiles();
		List<File> filesToProcess = new ArrayList<File>();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				filesToProcess.add(listOfFiles[i]);
		    }
		}
		listOfFiles = null;
		FileWriter fw = null;
		String fileOut = outputPath + "/analysis." + extention;
		File outFile = new File(fileOut);
		try {
			if (!outFile.exists()) {
				outFile.createNewFile();
			}
			fw = new FileWriter(outFile.getAbsoluteFile());
		}  catch (Exception e) {
			e.printStackTrace();
		}
		
		for (File file : filesToProcess) {
			System.out.println("Processing File " + file.getName());
			Map stats = new HashMap();
			BufferedReader br = null;
			Long lineCount = 1l;
			try {
				/*notifyListeners("Status: Calculating Line Count");
				FileReader fr1 = new FileReader(file);
				LineNumberReader lnr = new LineNumberReader(fr1);
			    int linenumber = 0;
		        while (lnr.readLine() != null){
		        	linenumber++;
		        }
		        lnr.close();
				fr1.close();
				double px = linenumber*.1;
				Long pl = Math.round(px);*/
				notifyListeners("Status: Processing File");
				FileReader fr2 = new FileReader(file);
				br = new BufferedReader(fr2);
				
				String line;
				int offset = 1;
				Long size = 1l;
				
		//		System.out.println("File out " + fileOut );
				
				fw.write("Processing File " + file.getName() +"\n");
				int percentComplete = 0;
				
				String queryAction = "\\.{3}\\w+";
				Pattern queryActionPattern = Pattern.compile(queryAction);
				
				
				Pattern datePattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}");
				String previousLine = null;
				Date previousDate = null;
				List<String> previousChildren = new ArrayList<String>();
				while ((line = br.readLine()) != null) {
					Matcher dm = datePattern.matcher(line);
					if (dm.find()) {
						String[] x = line.split(" ");

						Date date = null;
						long diffMinutes = 0l;

						date = formatTime.parse(x[0]+" "+x[1]);
						// check date
						
						if (previousLine != null && previousDate != null){
							// diff time
							
							long diff = date.getTime() - previousDate.getTime();	
							diffMinutes = diff / (60 * 1000) % 60;
							
						}

						if (previousLine != null && previousDate != null) {
							Matcher m = queryActionPattern.matcher(previousLine);
							if ( m.find() && diffMinutes >= 10){
								
								fw.write("line \t"+ lineCount +"\t"+ m.group(0) +"\t\tdiff min \t" + diffMinutes + "\n");
								fw.write(previousLine+"\n");
								for (String string : previousChildren) {
									fw.write(string+"\n");
								}
								fw.write("\n");
							}
						}
						
						previousDate = date;
						previousLine = line;
						previousChildren = new ArrayList<String>();
					} else {
						previousChildren.add(line);
					}
					lineCount++;
				}
				br.close();
				fr2.close();
				
			notifyListeners("Status: Complete");
				
			} catch (FileNotFoundException e) {
				System.out.println("Error at line ".concat(lineCount.toString()));
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Error at line ".concat(lineCount.toString()));
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println("Error at line ".concat(lineCount.toString()));
				e.printStackTrace();
			} 
		}

		if (fw != null) {
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Complete Log Analysis");
		updateStatus("Complete");
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
	
	
	public void actionAchExceptionProcessing(FileWriter bw, String line, Map stats) throws IOException {
		Matcher m = runTimePattern.matcher(line);
		/*if (line.contains("===== STARTING JOB ACTION ActionACHExceptions =====")) {
			String[] x = line.split(" ");
			try {
				stats.put("startDate", formatTime.parse(x[0]+" "+x[1]));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else */
		if (m.find()){
			String[] x = m.group(0).split(" ");
				//stats.put(x[1]);
				bw.write("runtime " + x[1] + " " + x[x.length - 1] +"\n");
		}
		
	}
	
	public void actionIncrementalExport(FileWriter bw, String line, Map stats, long lineCount) throws Exception{
		if (line.contains("Exporting beans of type")) {
			String[] x = line.split(" ");
			Date date = formatTime.parse(x[0]+" "+x[1]);
			if (stats.containsKey("lastDate")){
				Date lastDate = (Date) stats.get("lastDate");
				long millsec = date.getTime() - lastDate.getTime();
				bw.write(stats.get("lastName") + " time " + millsec/1000 +"\n");
			} 
			stats.put("lastDate", date);
			stats.put("lastName", x[9]);
		}

	}
	
	public void actionPolicySummaryStats(FileWriter bw, String line, Map stats, long lineCount) throws Exception{
		if (line.contains("DEBUG (JDBCData.java:2540) - INSERT INTO")) {
			insertCount++;
			totalInsertCount++;
		} else if (line.contains("DEBUG (JDBCData.java:2540) - UPDATE")) {
			updateCount++;
			totalUpdateCount++;
		} else if (line.contains("DEBUG (JDBCData.java:2527) - committed")) {
			String[] x = line.split(" ");
			Date date = formatTime.parse(x[0]+" "+x[1]);
			if (stats.containsKey("lastDate")){
				Date lastDate = (Date) stats.get("lastDate");
				long millsec = date.getTime() - lastDate.getTime();
				bw.write(x[0]+" "+x[1] +"\n");
				bw.write("commits i " + insertCount + " u " + updateCount + "  line " + lineCount + " time " + millsec +"\n");
				insertCount = 0;
				updateCount = 0;
			} 
			stats.put("lastDate", date);
		}

	}

	@Override
	public Map<String, Object> call() {
		this.process();
		return null;
	}
}
