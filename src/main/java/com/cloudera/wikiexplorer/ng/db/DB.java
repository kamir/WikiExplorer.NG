package com.cloudera.wikiexplorer.ng.db;


import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Hashtable;

/**
 * Diese Klasse stellt das allgemeine Gerüst für
 * konkrete DB-Adapter-Klassen dar.
 *
 * @author kamir
 */
public abstract class DB {
   
    public String driver = "org.gjt.mm.mysql.Driver";
    public String url = "jdbc:mysql://192.168.0.14/wikipedia";
    public String username = "root";
    public String password = "root";

    Hashtable<String,String> preparedStatements = new Hashtable<String,String>();

    public static Connection conn = null;
            
    public Connection getMySqlConnection() throws Exception {
        if ( conn == null ) {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
        };
        return conn;
    }

  

}
