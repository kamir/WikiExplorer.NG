/**
 *  Ein einfaches Logging-Tool zum Messen der Laufzeit.
 *
 *
TimeLog tl = new TimeLog();

// long procedure ...
loadIdsForLargeCounts();
tl.setStamp("IDs aus Datei geladen.");

tl.setStamp("Daten importiert.");

System.out.println(tl.toString());

 * 
 */
package com.cloudera.wikiexplorer.ng.util;

import java.io.PrintStream;
import java.lang.String;
import java.sql.Time;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.text.DateFormatter;

/**
 *
 * @author kamir
 */
public class TimeLog {

    public boolean quite = true;
    double lastStamp;
    double currentStamp;
    Vector<String> stamps = new Vector<String>();

    public TimeLog() {
        init();
    }

    ;

    public TimeLog(boolean isQuite) {
        this();
        quite = isQuite;
    }

    void init() {
        lastStamp = System.currentTimeMillis();
        String s = new Time((long) lastStamp) + " - Created";
        stamps.add(s);
    }

    ;

    /**
     * Fügt einen neuen Zeitstempel hinzu und zeigt diesen an, falls
     * beim Konstruktoraufruf "true" übergeben wurde.
     *
     * @param name
     */
    public void setStamp(String name) {
        currentStamp = System.currentTimeMillis();
        long diff = (long) (currentStamp - lastStamp);
        Time diffT = new Time(diff);
        DateFormat fmt = new SimpleDateFormat( "mm:ss" );
        String s = new Time((long) currentStamp) + " \t " + fmt.format(diffT)  + " \t " + name;
        stamps.add(s);
        if (!this.quite) {
            System.out.println(s);
        }
        lastStamp = currentStamp;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        Enumeration<String> st = stamps.elements();
        while (st.hasMoreElements()) {
            sb.append(st.nextElement() + "\n");
        }
        return sb.toString();
    }

    ;

    public void print(PrintStream out) {
        out.println( this.toString() );
        out.flush();
    }

}
