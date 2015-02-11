package com.googlesource.gerrit.plugins.hooks.clonescounter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {
	
	private final String url;
	private final String user;
	private final String pass;
	private final String db;
	private final String table;
	private final String dateCol;
	private final String counterCol;
	
	Logger log = Logger.getLogger(DBConnection.class.getName());
	
	public DBConnection(){
		this.url = "url";
		this.user = "user";
		this.pass = "pass";
		this.db = "dbname";
		this.table = "table";
		this.dateCol = "date";
		this.counterCol = "column";
	}
	
	// Increment the clones counter given a certain db and a certain table
	public void incrementClonesCounter(){		
		Connection con = null;
        Statement st = null;
        ResultSet rs = null;

		try {
            con = DriverManager.getConnection(String.format("%s/%s/{}", url, db), user, pass);
            st = con.createStatement();
            rs = st.executeQuery(String.format("SELECT FROM %s WHERE %s=%s", table, dateCol, getTodayDate()));
            if (rs.next()) {
            	String recordDate = rs.getString(1);
            	String recordClonesCount = rs.getString(2);
            	rs = st.executeQuery(String.format("UPDATE %s SET %s=%s WHERE %s=%s", table, counterCol, recordClonesCount+1, dateCol, recordDate));
            }else{
            	rs = st.executeQuery(String.format("INSERT INTO %s (%s, %s) VALUES (%s, %s)", table, dateCol, counterCol, getTodayDate(), 1));
            }
        } catch (SQLException ex) {
        	log.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                log.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
	}
	
	private String getTodayDate() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}

}