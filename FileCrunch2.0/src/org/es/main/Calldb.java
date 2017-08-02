package org.es.main;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.es.main.model.Column;
import org.es.main.model.Database;
import org.es.main.model.Table;

public class Calldb {

	public Connection conn = null;
	
	public Calldb(){
		
	}
	
	public Connection getConnection() throws Exception{
		if (conn == null){
			connect();
		}
		return conn;
	}
	
	public Map<String,Database> init(){
		Map<String,Database> databases = null;
		try{
			if (conn == null){
				connect();
			}
			databases = discoverDatabases();
		} catch (SQLException ex) {
	        ex.printStackTrace();
	    } catch (Exception e) {
			e.printStackTrace();
		} finally {
	       close();
	    }
		return databases;
	}
	
	public List<?> discover(String action, String dbName, String tableName) {
		List<?> result = null;
		try{
			if (conn == null){
				connect();
			}
			if ("DISCOVER_TABLES".equals(action)){
				result = discoverTables(dbName);
			} else if ("DISCOVER_COLUMNS".equals(action)){
				result = discoverColumns(dbName,tableName);
			}
		} catch (SQLException ex) {
	        ex.printStackTrace();
	    } catch (Exception e) {
			e.printStackTrace();
		} finally {
	        close();
	    }
		return result;
	}
	
	private Map<String,Database> discoverDatabases() throws Exception{
		DatabaseMetaData meta = conn.getMetaData();
        ResultSet res = meta.getCatalogs();
        Map<String,Database> databases = new LinkedHashMap<String,Database>();
        while (res.next()) {
        	databases.put(res.getString("TABLE_CAT"),new Database(res.getString("TABLE_CAT")));
        }
        return databases;
	}
	
	private List<Table> discoverTables(String databaseName) throws Exception{
		DatabaseMetaData meta = conn.getMetaData();
		ResultSet res = meta.getTables(databaseName, null, null, new String[] {"TABLE"});
        System.out.println("\tList of tables: ");
        List<Table> tables = new ArrayList<Table>();
        while (res.next()) {
        	/*System.out.println(
    	            "\t"+res.getString("TABLE_CAT") 
    	           + ", "+res.getString("TABLE_SCHEM")
    	           + ", "+res.getString("TABLE_NAME")
    	           + ", "+res.getString("TABLE_TYPE")
    	           + ", "+res.getString("REMARKS")); */
        	
        	tables.add(new Table(res.getString("TABLE_NAME"),res.getString("TABLE_TYPE"),res.getString("REMARKS")));
        }
        return tables;
	}
	
	private List<Column> discoverColumns(String databaseName, String tableName) throws Exception{
		DatabaseMetaData meta = conn.getMetaData();
		List<Column> columns = new ArrayList<Column>();
		if (databaseName != null  && tableName != null) {
    	   ResultSet resC = meta.getColumns(databaseName, null, tableName, null);
    	   while (resC.next()) {
    		/* System.out.println(
             "\t\t"+resC.getString("TABLE_SCHEM")
             + ", "+resC.getString("TABLE_NAME")
             + ", "+resC.getString("COLUMN_NAME")
             + ", "+resC.getString("TYPE_NAME")
             + ", "+resC.getInt("COLUMN_SIZE")
             + ", "+resC.getInt("NULLABLE"));*/
    		 columns.add(new Column(resC.getString("COLUMN_NAME"),resC.getString("TYPE_NAME"),resC.getInt("COLUMN_SIZE"),resC.getInt("NULLABLE")));
            }
            resC.close();
               
		}
		return columns;
	}

	private void connect() throws Exception{
        String dbURL = "jdbc:sqlserver://10.248.80.61:1433;DatabaseName=SPIDev_Sandbox1;";
        String user = "SPIAPPDEV";
        String pass = "DSuLa$z2Q@";
        conn = DriverManager.getConnection(dbURL, user, pass);
        if (conn != null) {
            DatabaseMetaData dm = (DatabaseMetaData) conn.getMetaData();
            System.out.println("Driver name: " + dm.getDriverName());
            System.out.println("Driver version: " + dm.getDriverVersion());
            System.out.println("Product name: " + dm.getDatabaseProductName());
            System.out.println("Product version: " + dm.getDatabaseProductVersion());

        }
	}
	
	public void close(){
		try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
	}
}
