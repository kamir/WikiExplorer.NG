package com.cloudera.wikiexplorer.ng.db.mlu3;

/**
 * Hierbei handelt es sich um die zweite Datenbank von Lev, in der die
 * Bearbeitungen (revisions) der einzelnen Wikipedia-Artikel gespeichert
 * sind.
 *
 * DB befindet isch auf dem Server MLU3 mit der IP-Adresse: ....104
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
            super.url = "jdbc:mysql://192.168.0.2:3306/wiki3";
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
                    "select PageID from pagenames where LanguageID=?");
            System.out.println(".");
            prepstmt1.setString(1, "197");
            System.out.println("..");
            System.out.println("CONECTION..." + con.isValid(5));
            ResultSet rs = prepstmt1.executeQuery();
            System.out.println("...");
            int c = 0;
            while( !rs.isLast() ) {
                c++;
                rs.next();
            }
            System.out.println("RS..." + c);

        }
        catch (Exception ex) {
            Logger.getLogger(WikipediaDB.class.getName()).log(Level.SEVERE, null, ex);
        }

    };
}
