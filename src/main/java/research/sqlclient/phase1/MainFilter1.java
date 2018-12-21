
package research.sqlclient.phase1;

import com.cloudera.wikiexplorer.ng.util.TimeLog;
import com.mysql.jdbc.Statement;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Zähle die IDs der Sprache, die als Arg[1] übergeben wird, sonst nutze
 * das int Array mit den LangIDs oder eine Abfrage für alle Sprachen.
 *
 * Erstellt pro Sprache eine Datei mit dein IDs aller zugehörigen nodes.
 *
 * Die beiden Felder
 *     langs
 *     codes
 * werden in Methode selectAllLanguages() belegt.
 *
 */
public class MainFilter1 {

    // beide Felder werden in Methode selectAllLanguages() belegt
    static int[] langs = {60, 52, 72, 62, 197};
    static String[] codes = { "en","de","fr","sp","ru" };
    
    public static Connection getMySqlConnection() throws Exception {
        String driver = "org.gjt.mm.mysql.Driver";
        String url = "jdbc:mysql://localhost/wikipedia";
        String username = "root";
        String password = "";

        Class.forName(driver);
        Connection conn = DriverManager.getConnection(url, username, password);
        return conn;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {

            FileWriter fw = new FileWriter( "/Volumes/MyExternalDrive/CALCULATIONS/data/out/log-filter1.dat" );

            // nur die 5 wichtigsten ...

            /**
             * Variante 1 - alle Codes aus der DB lesen
             */
            // selectAllLanguages();

            /**
             * Variante 2 - nur die 5 ausgewählten
             */
            int[] lang = langs;
      
            
            for (int a = 0; a < lang.length; a++) {

                String language = ""+lang[a];
                
                TimeLog tl = new TimeLog();
                int counter1 = 0;
                int counter2 = 0;
                
                // System.out.println("LanguageID: " + args[0]);

                Connection con = getMySqlConnection();

                /** Alle Artikel einer Sprache auswählen */
                PreparedStatement prepstmt1 = con.prepareStatement("select PageID from pagenames where LanguageID=?");
                prepstmt1.setString(1, language);

                ResultSet rs1;
                rs1 = prepstmt1.executeQuery();

                FileWriter fwID1 = new FileWriter( "/Volumes/MyExternalDrive/CALCULATIONS/data/out/MainFilter1/allNodeIDs_of_lang_"+language+"_"+codes[a]+".dat" );
                StringBuffer sb1 = new StringBuffer();
                sb1.append("# all NodeIDs of language: " + language + " (" + codes[a] + ")\n" );

                String nl1 = "\n";

                while (rs1.next()) {
                    counter1++;
                    sb1.append( rs1.getInt(1) + nl1 );
                }
                prepstmt1.close();
                fwID1.write( sb1.toString());
                fwID1.flush();
                fwID1.close();




               // tl.setStamp("Step 1");

                String tableNameOfFilter = "Filter1";
                PreparedStatement prepstmt2 = con.prepareStatement(
                        "select pagenames.PageID,pagenames.LanguageID from pagenames," + tableNameOfFilter + " where LanguageID=? AND pagenames.PageID="+tableNameOfFilter+".PageID");
                prepstmt2.setString(1, language);
                ResultSet rs2;
                rs2 = prepstmt2.executeQuery();


                FileWriter fwID = new FileWriter( "/Volumes/MyExternalDrive/CALCULATIONS/data/out/MainFilter1/lang_"+language+"-"+codes[a]+".ids.dat" );
                StringBuffer sb = new StringBuffer();
                sb.append("# all NodeIDs of language: " + language + " (" + codes[a] + ")\n" );
                sb.append("# filtered by Filter1\n");

                String nl = "\n";

                while (rs2.next()) {
                    counter2++;
                    sb.append( rs2.getInt(1) + nl );
                }
                ;
                prepstmt2.close();

                fwID.write( sb.toString());
                fwID.flush();
                fwID.close();
                
               // tl.setStamp("Step 2");

                double rel = (double) counter2 / (double) counter1;
                double rel2 = (double) counter2 / (double) 16e6;

                String line = (a+1) + "\t" + codes[a] + "\t" + language + " \t " + counter1 + "\t" + counter2 + "\t" + rel + "\t" + (100.0 * rel2) + "\n";

                System.out.print( line );
                fw.write( line );
            }
            fw.flush();
            fw.close();

        } catch (SQLException ex) {
            Logger.getLogger(MainFilter1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(MainFilter1.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    private static void _selectAllLanguages() {
        try {

            Vector<Integer> p1 = new Vector<Integer>();
            Vector<String> p2 = new Vector<String>();

            Connection con = getMySqlConnection();
            Statement st = (Statement) con.createStatement();
            ResultSet rs;
            rs = st.executeQuery("Select LanguageID, ShortName from languages" );
            while (rs.next()) {
                Integer i = rs.getInt(1);
                String s = rs.getString(2);

                p1.add(i);
                p2.add(s);
            }
            st.close();

            langs = new int[p1.size()];
            codes = new String[p1.size()];

            for( int i=0; i < p1.size(); i++ ) {
                langs[i] = p1.elementAt(i);
                codes[i] = p2.elementAt(i);
            };
            System.out.println( p1.size() + " Sprachen verfügbar ... \n" );
            
        } 
        catch (Exception ex) {
            Logger.getLogger(MainFilter1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
