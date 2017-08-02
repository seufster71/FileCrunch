package org.es.main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class FileCrunch extends Application implements StatusListener{
	
	private Stage primaryStage = null;
	private static final TextField textField = null;
	private static final TextField textField2 = null;
	private static final TextField textField3 = null;
 	private static final Button splitButton = null;
	private static final Label label6 = null;
	final FileCrunch parent = this;
	private BorderPane root = null;
	private String statusMessage = "READY";
	private FileSplit fileSplit = null;
	private LogAnalysis logAnalysis = null;
	private FileSearch fileSearch = null;
	private CommissionLogAnalysis commissionLogAnalysis = null;
	private JobLogAnalysis jobLogAnalysis = null;
	private ChainAnalysis chainAnalysis = null;
	private TestArea testArea = null;
	private DBScannerView dbScanner = null;
	private ExecutorService pool = Executors.newFixedThreadPool(3);
	
	public static void main(String[] args) {
        launch(args);
    }
    
	@Override
	public void start(Stage stage) throws Exception {
		 primaryStage = stage;
		 primaryStage.setTitle("File Crunch");
		 fileSplit = new FileSplit(this);
		 fileSearch = new FileSearch(this);
		 logAnalysis = new LogAnalysis(this);
		 commissionLogAnalysis = new CommissionLogAnalysis(this);
		 jobLogAnalysis = new JobLogAnalysis(this);
		 chainAnalysis = new ChainAnalysis(this);
		 testArea = new TestArea(this);
		 dbScanner = new DBScannerView(this);
		 root = new BorderPane();
		 VBox topContainer = new VBox();
		 Scene scene = new Scene(root, 800, 500);
	     scene.setFill(Color.OLDLACE);
	     
	     MenuBar menuBar = new MenuBar();
	     
	     // --- Menu File
	     Menu menuFile = new Menu("File");
	
	     // --- Menu Tools
	     Menu menuTools = new Menu("Tools");
	     MenuItem fileSplitMenu = new MenuItem("File Split");
         fileSplitMenu.setOnAction(new EventHandler<ActionEvent>() {
             public void handle(ActionEvent t) {
                 fileSplit.draw(primaryStage, root);
             }
         });
         MenuItem fileSearchMenu = new MenuItem("File Search");
         fileSearchMenu.setOnAction(new EventHandler<ActionEvent>() {
             public void handle(ActionEvent t) {
                 fileSearch.draw(primaryStage, root);
             }
         });   
         MenuItem logAnalysisMenu = new MenuItem("Log Analysis");
         logAnalysisMenu.setOnAction(new EventHandler<ActionEvent>() {
             public void handle(ActionEvent t) { 
                 logAnalysis.draw(primaryStage, root);  
             }
         }); 
         MenuItem chainAnalysisMenu = new MenuItem("Chain Analysis");
         chainAnalysisMenu.setOnAction(new EventHandler<ActionEvent>() {
             public void handle(ActionEvent t) { 
                 chainAnalysis.draw(primaryStage, root);
             }
         }); 
         MenuItem commissionLogAnalysisMenu = new MenuItem("Commission Log Analysis");
         commissionLogAnalysisMenu.setOnAction(new EventHandler<ActionEvent>() {
             public void handle(ActionEvent t) { 
                 commissionLogAnalysis.draw(primaryStage, root);
                 
             }
         }); 
         MenuItem jobLogAnalysisMenu = new MenuItem("Job Log Analysis");
         jobLogAnalysisMenu.setOnAction(new EventHandler<ActionEvent>() {
             public void handle(ActionEvent t) { 
                 jobLogAnalysis.draw(primaryStage, root);
             }
         }); 
         MenuItem testAreaMenu = new MenuItem("Test area");
         testAreaMenu.setOnAction(new EventHandler<ActionEvent>() {
             public void handle(ActionEvent t) { 
                 testArea.draw(primaryStage, root);
             }
         });
         MenuItem dbScannerMenu = new MenuItem("DB Scanner");
         dbScannerMenu.setOnAction(new EventHandler<ActionEvent>() {
             public void handle(ActionEvent t) { 
                 dbScanner.draw(primaryStage, root);
             }
         }); 
         menuTools.getItems().addAll(fileSplitMenu,fileSearchMenu,logAnalysisMenu,chainAnalysisMenu,commissionLogAnalysisMenu,jobLogAnalysisMenu,testAreaMenu,dbScannerMenu);	     
	     
	     menuBar.getMenus().addAll(menuFile, menuTools);
	     topContainer.getChildren().addAll(menuBar);
	     root.setTop(topContainer);
	     fileSplit.draw(primaryStage, root);
	     primaryStage.setScene(scene);
	     primaryStage.show();
	}

	@Override
	public void updateStatus(String message) {
		statusMessage = message;
		if (message.contains("Complete")){
			//splitButton.setDisable(false);
			//textField.setDisable(false);
			//textField2.setDisable(false);
			//textField3.setDisable(false);
		}
		 Platform.runLater(new Runnable() {
	            @Override
	            public void run() {
	            	label6.setText(" " + statusMessage);
	            }
	          });          
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		
		// close any open db connections
		if (dbScanner != null) {
			dbScanner.closeDB();
		}
		try {
		    System.out.println("attempt to shutdown executor");
		    pool.shutdown();
		    pool.awaitTermination(5, TimeUnit.SECONDS);
		}
		catch (InterruptedException e) {
		    System.err.println("tasks interrupted");
		}
		finally {
		    if (!pool.isTerminated()) {
		        System.err.println("cancel non-finished tasks");
		    }
		    pool.shutdownNow();
		    System.out.println("shutdown finished");
		}
	}
	
	public ExecutorService getPool(){
		return pool;
	}
}
