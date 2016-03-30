/**
 * Ein konkretes Object meldet sich hier als LogFileUser an. Dbei erhält es ein
 * eigenes LogFile, in dem es mittels der Methode 
 *
 *      logData( String ... )
 * 
 * dauerhaft speichern kann. Eine separate Log-Datei wird pro Instanz erstellt
 *
 */

package com.cloudera.wikiexplorer.ng.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.DateFormatter;

/**
 * @year    2010
 * @author  kamir
 */
public class LogFile {

    // sollen alle Dateien im gleichen Ordner abgelegt werden?
    static boolean useFixedFolder = true;

    // Vorgabe des gemeinsamen AblageOrdners
    static public void setFixedRootFolder(String folderName) {
        folder = folderName;
        useFixedFolder = true;
    };

    static public String folder = "TEST";

    static public File folderFile = null;
    static public File imageFolderFile = null;

    String filename = null;

    StringBuffer header = null;
    StringBuffer line = null;
    FileWriter fw = null;
    BufferedWriter bfw = null;

    LogFileUser user = null;

    /**
     * Zu Beginn einer Simulation wird ein neuer Ordner angelegt ...
     */
    public static void defineNewFolder() {

        if ( !useFixedFolder ) {

            long time = System.currentTimeMillis();
            Date date = new Date(time);

            // Festlegung des Formats:
            SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd_HH-mm" );
            df.setTimeZone( TimeZone.getDefault() );

            // Formatierung zu String
            folder = "data" + File.separator + df.format( date );
            System.out.println(">>> VARIABLE LogFile Folder is: " + folder );
        }
        else {
            System.out.println(">>> FIXED LogFile Folder is: " + folder );
        }

        createFolders();
    }
static String datafolder_base = "./log/";
    static void createFolders() {
        
        File f = new File( datafolder_base + folder );
        f.mkdirs();

        File f2 = new File( datafolder_base + folder + File.separator + "images" );
        f2.mkdirs();

        imageFolderFile = f2;
        folderFile = f;
    };

    public LogFile(Object o, String filename) {
        this.user = (LogFileUser) o;
        this.line = new StringBuffer();
        this.filename = filename;
        // hier ist der AblageOrdner noch nicht eindeutig definiert.
        // Dazu muss dann erst noch defineNewFolder aufgerufen werden
    }

    /**
     *  Es wird zunächst nur der Header geschrieben.
     */
    public void openLogFile() {
        File f = new File(getFullFilename());
        if ( !f.getParentFile().exists() ) f.getParentFile().mkdirs();
        
        try {
            // System.out.println( SimulationParameter.datafolder_base + folder + "/" + filename  );
            bfw = new BufferedWriter(new FileWriter( f ));
            bfw.write( user.getLogFileHeader());
            bfw.flush();
        }
        catch (IOException ex) {
            Logger.getLogger( LogFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *   Nun ist das LogFile geschlossen.
     */
    public void closeLogfile() {
        try {
            //bfw.flush();
            bfw.close();
        }
        catch (IOException ex) {
            Logger.getLogger(LogFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    /**
     * Die Daten werden gespeichert und dabei werden alle ","
     * durch "." ersetzt.
     *
     * @param data
     */
    public void logData(String data) {
        line.append( data.replace(",","." ));
    }

    /**
     *  Der Puffer wird geleert.
     */
    public void flushData() {
        try {
            line.append("\n");

            bfw.write(line.toString());
            // bfw.flush();

            line = new StringBuffer();
        }
        catch (IOException ex) {
            Logger.getLogger(LogFile.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Wo werden die Daten gespeichert - das erfährt man hier!
     * 
     * @return
     */
    public String getFullFilename() {
        return datafolder_base + folder + File.separator + filename;
    }
}
