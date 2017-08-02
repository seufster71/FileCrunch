package org.es.main;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.es.main.model.Column;
import org.es.main.model.Database;
import org.es.main.model.Table;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class DBScannerView {

	private static final Label label1 = new Label("Choose a database: ");
	private static final ComboBox<String> databaseSelect = new ComboBox<String>();
	private static final Label label2 = new Label("Choose a table: ");
	private static final ComboBox<String> tableSelect = new ComboBox<String>();
	private static final VBox vb = new VBox();
	private static final Label label3 = new Label("Choose columns to analyze: ");
	private static final TableView table = new TableView();
	private static final Button analyzeButton = new Button();
 	private Stage primaryStage = null;
 	
	final DBScannerView instance = this;
	private String extention = "txt";
	private String statusMessage = "READY";
	private DateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
	private int beanCount = 0;
	private Date startTime = null;
	private Date endTime = null;
	private long totalTime = 0l;
	private int stackTraceCount = 0;
	private Calldb callDB = null;
	private String action = "INIT";
	private Map<String,Database> databases = null;
	private Database database = null;
	private FileCrunch appParent = null;
	
	public DBScannerView(FileCrunch parent) {
		appParent = parent;
	}

	public void draw(Stage stage, BorderPane root) {
		
		primaryStage = stage;
        vb.setSpacing(5);
        vb.setPadding(new Insets(10, 0, 0, 10));
        databaseSelect.setMinWidth(200.0);
        databaseSelect.setPrefWidth(databaseSelect.getMinWidth());
		databaseSelect.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override 
			public void changed(ObservableValue<? extends String> observableValue, String oldChoice, String newChoice) {
					changedDatabase(newChoice);
				}  				
		});
		label2.setVisible(false);
		tableSelect.setMinWidth(200.0);
		tableSelect.setPrefWidth(tableSelect.getMinWidth());
		tableSelect.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override 
			public void changed(ObservableValue<? extends String> observableValue, String oldChoice, String newChoice) {
					changedTable(newChoice);
				}  				
		});
		tableSelect.setVisible(false);
		
		label3.setVisible(false);
		table.setVisible(false);
		TableColumn<Column, String> nameCol = new TableColumn<Column, String>("Column Name");
		nameCol.setMinWidth(100);
		nameCol.setCellValueFactory( new PropertyValueFactory<Column, String>("name") );
		
        TableColumn<Column, Integer> sizeCol = new TableColumn<Column, Integer>("Size");
        sizeCol.setMinWidth(20);
        sizeCol.setCellValueFactory( new PropertyValueFactory<Column, Integer>("size") );
        
        TableColumn<Column, String> typeCol = new TableColumn<Column, String>("Type");
        typeCol.setMinWidth(20);
        typeCol.setCellValueFactory( new PropertyValueFactory<Column, String>("type") );
        
        TableColumn<Column, Integer> nullableCol = new TableColumn<Column, Integer>("Nullable");
        nullableCol.setMinWidth(100);
        nullableCol.setCellValueFactory( new PropertyValueFactory<Column, Integer>("nullable") );
        
        TableColumn<Column,Boolean> analyzeCol = new TableColumn<Column,Boolean>("Analyze");
        analyzeCol.setMinWidth(100);
        analyzeCol.setCellValueFactory( new PropertyValueFactory<Column, Boolean>("analyze"));
        analyzeCol.setCellFactory(new Callback<TableColumn<Column,Boolean>, TableCell<Column,Boolean>>() {

			@Override
			public TableCell<Column, Boolean> call(TableColumn<Column, Boolean> arg0) {
				return new CheckBoxTableCell<Column, Boolean>();
			}
        	
        });
        
        table.getColumns().addAll(analyzeCol, nameCol, sizeCol, typeCol, nullableCol);
        
        analyzeButton.setText("Analyze");
        analyzeButton.setOnAction(new EventHandler<ActionEvent>() {
		
			@Override
			public void handle(ActionEvent event) {
				analyzeTable();
			}
		});
		analyzeButton.setVisible(false);
		
		vb.getChildren().addAll(label1,databaseSelect,label2,tableSelect,label3,table,analyzeButton);
		
		appParent.getPool().submit(new DBScannerController(this,action));

		root.setCenter(vb);
	}
	
	public void updateStatus(String message) {
		statusMessage = message;
		
	}
	
	public void closeDB(){
		if (callDB != null){
			callDB.close();
		}
	}
	
	public void setDatabases(Map<String,Database> databases){
		this.databases = databases;
	}
	public Map<String,Database> getDatabases(){
		return this.databases;
	}
	
	public void setDatabase(Database database){
		this.database = database;
	}
	public Database getDatabase(){
		return this.database;
	}
	
	public void updateDBSelect(){
		Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	boolean fxApplicationThread = Platform.isFxApplicationThread();
                System.out.println("Is call on FXApplicationThread: " + fxApplicationThread);
            	// update gui
        		ObservableList<String> dbList = FXCollections.observableArrayList();
        		for (String key : databases.keySet()){
        			dbList.add(key);
        		}
        		databaseSelect.setItems(dbList);
            }
          });
	}
	
	public void updateTableSelect(){
		Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	// update gui
            	
        		ObservableList<String> dbList = FXCollections.observableArrayList();
        		List<Table> tables = database.getTables();
        		for (Table table : tables){
        			dbList.add(table.getName());
        		}
        		tableSelect.setItems(dbList);
        		label2.setVisible(true);
        		tableSelect.setVisible(true);
            }
          });
	}

	public void updateColumnSelect(){
		Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	// update gui
            
        		ObservableList<Column> colList = FXCollections.observableArrayList();
        		List<Column> columns = database.getTable(getTableSelected()).getColumns();
        		for (Column column : columns){
        			colList.add(column);
        		}
        		table.setItems(colList);

            	label3.setVisible(true);
            	table.setVisible(true);
            	analyzeButton.setVisible(true);
            }
          });
	}
	
	public void changedDatabase(String new_value) {
		//System.out.println("Discover tables new value " + new_value);
		action = "DISCOVER_TABLES";
		database = databases.get(new_value);
		if (database != null){
			appParent.getPool().submit(new DBScannerController(this,action));
		}
	}
	
	public void changedTable(String new_value) {
		//System.out.println("Discover columns new value " + new_value);
		action = "DISCOVER_COLUMNS";
		if (new_value != null){
			appParent.getPool().submit(new DBScannerController(this,action));
		}
	}
	
	public void analyzeTable(){
		//System.out.println("Analyze table ");
		action = "ANALYZE_TABLE";
		appParent.getPool().submit(new DBScannerController(this,action));
	}

	public String getDBSelected(){
		return databaseSelect.getSelectionModel().getSelectedItem();
	}
	public String getTableSelected(){
		return tableSelect.getSelectionModel().getSelectedItem();
	}
	public String getColumnsSelected(){
		return "";
	}

	
	//CheckBoxTableCell for creating a CheckBox in a table cell
    public static class CheckBoxTableCell<Column, T> extends TableCell<Column, T> {
        private final CheckBox checkBox;
        private ObservableValue<T> ov;
        
        public CheckBoxTableCell() {
            checkBox = new CheckBox();
            checkBox.setAlignment(Pos.CENTER);
            checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
                	if (checkBox.isSelected()){
                		
                	}
                  System.out.println(checkBox.isSelected());
               }
            });
            
            setAlignment(Pos.CENTER);
            setGraphic(checkBox);
        } 

        @Override 
        public void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                setGraphic(checkBox);
                if (ov instanceof BooleanProperty) {
                    checkBox.selectedProperty().unbindBidirectional((BooleanProperty) ov);
                }
                ov = getTableColumn().getCellObservableValue(getIndex());
                if (ov instanceof BooleanProperty) {
                    checkBox.selectedProperty().bindBidirectional((BooleanProperty) ov);
                }
            }
        }
    }

}
