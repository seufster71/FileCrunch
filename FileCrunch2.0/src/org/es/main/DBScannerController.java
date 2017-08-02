package org.es.main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.es.main.model.Column;
import org.es.main.model.Database;
import org.es.main.model.Table;

public class DBScannerController extends ProcessManager {

	private Calldb callDB = null;
	private String action = "INIT";
	private DBScannerView dbScannerView = null;
	private Database database = null;
	
	public DBScannerController(DBScannerView dbScannerView, String action) {
		this.action = action;
		this.dbScannerView = dbScannerView;
		this.database = dbScannerView.getDatabase();
	}

	protected void process() {
		System.out.println("Running process");
		if (callDB == null){
			callDB = new Calldb();
		}
		if ("INIT".equals(action)){
			System.out.println("Action INIT");
			dbScannerView.setDatabases(callDB.init());
			dbScannerView.updateDBSelect();
		} else if ("DISCOVER_TABLES".equals(action)){
			System.out.println("Action DISCOVER_TABLES");
			List<Table> tables = (List<Table>) callDB.discover("DISCOVER_TABLES", database.getName(), null);
			if (tables != null){
				dbScannerView.getDatabase().setTables(tables);
				dbScannerView.updateTableSelect();
			}
		} else if ("DISCOVER_COLUMNS".equals(action)){
			System.out.println("Action DISCOVER_COLUMNS");
			List<Column> columns = (List<Column>) callDB.discover("DISCOVER_COLUMNS", dbScannerView.getDBSelected(), dbScannerView.getTableSelected());
			if (columns != null){
				dbScannerView.getDatabase().getTable(dbScannerView.getTableSelected()).setColumns(columns);
				dbScannerView.updateColumnSelect();
			}
		} else if ("ANALYZE_TABLE".equals(action)){
			System.out.println("Action ANALYZE_TABLE");
			System.out.println("Database " + dbScannerView.getDBSelected());
			System.out.println("Table " + dbScannerView.getTableSelected());
			System.out.println("Columns " + dbScannerView.getColumnsSelected());
			Table table = dbScannerView.getDatabase().getTable(dbScannerView.getTableSelected());
			getRows(table, 0l, 50l);
		
		}
	}
	
	protected void closeDB(){
		if (callDB != null){
			callDB.close();
		}
	}
	
	protected ResultSet getRows(Table table, Long start, Long count){
		ResultSet result = null;
		Statement stmt = null;
		try {
			Connection conn = callDB.getConnection();
			stmt = conn.createStatement();
		    String sql;
		    sql = table.getSelect();
		    ResultSet rs = stmt.executeQuery(sql);
		    while(rs.next()){
		    	//analyze 
		    	
			     //Retrieve by column name
			     int id  = rs.getInt("SystemId");
			     int c = rs.getInt("UpdateCount");
			     String user = rs.getString("UpdateUser");
			     String xml = rs.getString("XmlContent");

			     //Display values
			    /* System.out.print("ID: " + id);
			     System.out.print(", Count: " + c);
			     System.out.print(", user: " + user);
			     System.out.println(", xml: " + xml);
			     */
			  }
			rs.close();
		    stmt.close();
		    
		}catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      callDB.close();
		   }//end try
		return result;
	}

	@Override
	public Map<String, Object> call() {
		this.process();
		return null;
	}
}
