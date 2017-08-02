package org.es.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

public class FileSplit extends ProcessManager implements StatusListener{
	private static final TextField textField = new TextField();
	private static final TextField textField2 = new TextField();
	private static final TextField textField3 = new TextField();
 	private static final Button splitButton = new Button();
 	private Stage primaryStage = null;
	private static final Label label6 = new Label("Status: ");
	private String inputPath = null;
	private String inputFile = null;
	private String outputPath = null;
	private String fileName = null;
	private String extention = "txt";
	private Long fileSize = 60000000l;
	private String statusMessage = "READY";
	
	public FileSplit(FileCrunch parent){
		appParent = parent;
	}
	
	public FileSplit(String inputFile, String outputPath, String fileName, Long fileSize) {
		this.inputFile = inputFile;
		this.outputPath = outputPath + "\\";
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.addListener(this);
	}
	
	public void draw(Stage stage, BorderPane root){
		primaryStage = stage;
		Label label1 = new Label("File:");
		textField2.setPrefWidth(600);
		textField2.setOnMouseClicked(new EventHandler<MouseEvent>() {
		
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
			       	outputPath = file.getParent();
			       	textField2.setText(path);
			    }
			}
		 
		});
		textField.setPrefWidth(600);
		textField.setOnMouseClicked(new EventHandler<MouseEvent>() {
		
			@Override
			public void handle(MouseEvent arg0) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Resource File");
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
			        	textField2.setText(outputPath);
			        }
			        textField.setText(inputFile);
			    }
			}
			 
		});
		textField3.setText("60000000");

		splitButton.setText("Split");
		splitButton.setOnAction(new EventHandler<ActionEvent>() {
		
			@Override
			public void handle(ActionEvent event) {
				splitButton.setDisable(true);
				textField.setDisable(true);
				textField2.setDisable(true);
			    textField3.setDisable(true);
			    FileSplit fileSplit = new FileSplit(inputFile,outputPath,fileName,new Long(textField3.getText()));
			  //  fileSplit.addListener(parent);
			    appParent.getPool().submit(fileSplit);
			}
		});
		    
		 HBox hb1 = new HBox();
		 hb1.getChildren().addAll(label1, textField);
		 hb1.setSpacing(10);
		 
		 Label label2 = new Label("Input");
		 Label label3 = new Label("Output");
		 Label label4 = new Label("Output Directory: ");
		 
		 
		 HBox hb2 = new HBox();
		 hb2.getChildren().addAll(label4, textField2);
		 hb2.setSpacing(10);
		 
		 Label label5 = new Label("File Size: ");
		 
		 HBox hb3 = new HBox();
		 hb3.getChildren().addAll(label5, textField3);
		 hb3.setSpacing(10);
		 
		 VBox vb = new VBox();
		 vb.getChildren().addAll(label2,hb1,label3,hb2,hb3,splitButton,label6);
		
		 root.setCenter(vb);
	}
	
	public void process() {
		File file = new File(inputFile);
		if (!file.exists()){
			System.out.println("File does not exist " + file.getPath());
			return;
		}
		
		BufferedReader br;
		try {
		//	System.out.println("Calculating line Count");
			notifyListeners("Status: Calculating Line Count");
			FileReader fr1 = new FileReader(file);
			LineNumberReader lnr = new LineNumberReader(fr1);
		    int linenumber = 0;
	        while (lnr.readLine() != null){
	        	linenumber++;
	        }
	    //    System.out.println("Total lines : "+linenumber);
			lnr.close();
			fr1.close();
			double px = linenumber*.1;
			Long pl = Math.round(px);
			notifyListeners("Status: Processing File");
			FileReader fr2 = new FileReader(file);
			br = new BufferedReader(fr2);
			
			String line;
			Long lineCount = 1l;
			int offset = 1;
			Long size = 1l;
			String fileOut = outputPath + fileName + ".Part" + offset + "." + extention;
	//		System.out.println("File out " + fileOut );
			File outFile = new File(fileOut);
			if (!outFile.exists()) {
				outFile.createNewFile();
			}
			FileWriter fw = new FileWriter(outFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			int percentComplete = 0;
			Long plTotal = pl;
			while ((line = br.readLine()) != null) {
				if (lineCount > plTotal){
					plTotal = plTotal + pl;
					percentComplete = percentComplete + 10;
					notifyListeners("Status: Processing File -- Percentage: "+percentComplete);
				}
				byte[] chunks = line.getBytes("UTF-8");
				size = size + chunks.length;
				if (size > fileSize * offset) {
						bw.close();
						offset++;
						outFile = new File(outputPath + fileName + ".Part" + offset + "." + extention);
						if (!outFile.exists()) {
							outFile.createNewFile();
						}
						fw = new FileWriter(outFile.getAbsoluteFile());
						bw = new BufferedWriter(fw);
				}
	 
				bw.write(line + "\r\n");
				lineCount++;
			}
			bw.close();
			br.close();
			fr2.close();
		notifyListeners("Status: Complete");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void updateStatus(String message) {
		statusMessage = message;
		if (message.contains("Complete")){
			splitButton.setDisable(false);
			textField.setDisable(false);
			textField2.setDisable(false);
			textField3.setDisable(false);
		}
		 Platform.runLater(new Runnable() {
	            @Override
	            public void run() {
	            	label6.setText(" " + statusMessage);
	            }
	          });          
		
	}

	@Override
	public Map<String, Object> call() {
		this.process();
		return null;
	}
	
}
