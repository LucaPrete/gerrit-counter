// Copyright (C) 2015 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.clonecounter;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlesource.gerrit.plugins.clonecounter.ClientAction.Type;

public class DBConnection {
  
  private static final Logger log = LoggerFactory.getLogger(DBConnection.class);
  private final String url;
  private final String port;
  private final String user;
  private final String pass;
  private final String db;
  private final String table;
  private final String dateCol;
  private final String clonesCounterCol;
  private final String updatesCounterCol;
  private final String repoCol;

  public DBConnection(HashMap<String, String> dbConfig) {
    this.url = dbConfig.get("dbUrl");
    this.port = dbConfig.get("dbPort");
    this.user = dbConfig.get("dbUser");
    this.pass = dbConfig.get("dbPass");
    this.db = dbConfig.get("dbName");
    this.table = dbConfig.get("dbTable");
    this.dateCol = dbConfig.get("dbDateCol");
    this.clonesCounterCol = dbConfig.get("dbClonesCounterCol");
    this.updatesCounterCol = dbConfig.get("dbUpdatesCounterCol");
    this.repoCol = dbConfig.get("dbRepoCol");
  }

  // Increment the either the clones or the update counters, given an update type and a repo name
  public void incrementCounters(Type type, String repo){
    Connection con = null;
    PreparedStatement recordExists = null;
    PreparedStatement updateRecord = null;
    PreparedStatement insertRecord = null;
    
    String counterCol = null;
    if (type == Type.CLONE) counterCol = clonesCounterCol;
    else if (type == Type.UPDATE) counterCol = updatesCounterCol;

    String queryRecordExists = String.format("SELECT * FROM %s WHERE %s=? AND %s=?", table, dateCol, repoCol);
    String queryUpdateRecord = String.format("UPDATE %s SET %s=? WHERE %s=? AND %s=?", table, counterCol, dateCol, repoCol);
    String queryInsertRecord = String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)", table, dateCol, counterCol, repoCol);
    
    ResultSet rs = null;

    try {
      con = DriverManager.getConnection(String.format("jdbc:postgresql://%s:%s/%s", url, port, db), user, pass);
      con.setAutoCommit(true);
      recordExists = con.prepareStatement(queryRecordExists);
      recordExists.setDate(1, getTodayDate());
      recordExists.setString(2, repo);
      rs = recordExists.executeQuery();
      if (rs.next()) {
        Date recordDate = rs.getDate(1);
        Integer recordClonesCount = rs.getInt(2);
        String recordRepo = rs.getString(3);
        updateRecord = con.prepareStatement(queryUpdateRecord);
        updateRecord.setInt(1, recordClonesCount + 1);
        updateRecord.setDate(2, recordDate);
        updateRecord.setString(3, recordRepo);
        updateRecord.execute();
      }else{
        insertRecord = con.prepareStatement(queryInsertRecord);
        insertRecord.setDate(1, getTodayDate());
        insertRecord.setInt(2, 1);
        insertRecord.setString(3, repo);
        insertRecord.execute();
      }
    } catch (SQLException ex) {
      log.error(ex.getMessage());
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (recordExists != null) {
          recordExists.close();
        }
        if (updateRecord != null) {
          updateRecord.close();
        }
        if (insertRecord != null) {
          insertRecord.close();
        }
        if (con != null) {
          con.close();
        }
      } catch (SQLException ex) {
        log.error(ex.getMessage());
      }
    }
  }

  private java.sql.Date getTodayDate() {
    java.util.Date date = new java.util.Date();
    return new java.sql.Date(date.getTime());
  }
}