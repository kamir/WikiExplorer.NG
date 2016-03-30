package com.cloudera.wikiexplorer.ng.db.notebook;

/**
 * Hierbei handelt es sich um die zweite Datenbank von Lev, in der die
 * Bearbeitungen (revisions) der einzelnen Wikipedia-Artikel gespeichert
 * sind.
 *
 * DB befindet sich auf dem Notebook von Mirko (weiss) 
 */

import com.cloudera.wikiexplorer.ng.db.DB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kamir
 */
public class WikipediaDB extends DB {

    public WikipediaDB() {
            super.driver = "org.gjt.mm.mysql.Driver";
            super.url = "jdbc:mysql://127.0.0.1:3306/wikipedia";
//            super.url = "jdbc:mysql://127.0.0.1:3306/wiki1";

            super.username = "root";
            super.password = "root";
    };

    /**
     * Test der Verbindung zur DB.
     *
     * @param args
     */
    public static void main( String[] args ) {
        try {
            DB db = new WikipediaDB();
            Connection con = db.getMySqlConnection();
            PreparedStatement prepstmt1 = con.prepareStatement(
                    "select COUNT(*) from links ");
            System.out.println(".");
            //prepstmt1.setString(1, "5");
            System.out.println("..");
            System.out.println("CONECTION..." + con.isValid(5));
            ResultSet rs = prepstmt1.executeQuery();
            System.out.println("...");
            int c = 0;
            while( !rs.isLast() ) {
                c++;
                System.out.println(  );
                rs.next();
            }
            System.out.println("RS..." + c);

        }
        catch (Exception ex) {
            Logger.getLogger(WikipediaDB.class.getName()).log(Level.SEVERE, null, ex);
        }

    };

    public void setDB_to_wiki1() {
        super.url = "jdbc:mysql://127.0.0.1:3306/wiki1";
    }

    public void setDB_to_wikipedia() {
        super.url = "jdbc:mysql://127.0.0.1:3306/wikipedia";
    }
}
