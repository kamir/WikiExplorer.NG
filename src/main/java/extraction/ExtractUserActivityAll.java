/**
 * Für alle Nodes und alle User wird die Edit-Activity ermittelt
 *
 *
 */
package extraction;

import org.apache.hadoopts.chart.statistic.HistogramChart;
import com.mysql.jdbc.Statement;
import org.apache.hadoopts.data.series.TimeSeriesObject;
import org.apache.hadoopts.data.export.MeasurementTable;
import com.cloudera.wikiexplorer.ng.db.DB;
import com.cloudera.wikiexplorer.ng.db.notebook.WikipediaDB;
import java.awt.Container;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Vector;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author kamir
 */
public class ExtractUserActivityAll {


    //
    //  different users 19.891.773
    //

    public static DB db;
    public static Connection con;

    public static int z = 0;

    // "Select UserID, count(UserID) from revisions group by UserID";
    static String request = "Select UserID from revisions";

    public static TimeSeriesObject extractUserActivityForUserID() throws Exception {

        BufferedWriter bw = new BufferedWriter( new FileWriter( "./allEditsPerUser.dat" ) );

        if ( db == null ) db = new WikipediaDB();

        if ( con == null ) con = db.getMySqlConnection();

        // "select id, count(id) as myCount from relationships group by id"
        
        Statement statement = (Statement) con.createStatement();

        TimeSeriesObject mr = new TimeSeriesObject( "User Activity ");
        ResultSet rs;
        rs = statement.executeQuery(request);

        HashMap<Integer,UserCounter> set = new HashMap<Integer,UserCounter>();

        int i = 0;
        int j = 0;
        while (rs.next()) {

           Integer userId = rs.getInt(1);
           Integer nrOfEditPerUser = rs.getInt(2);

           UserCounter uc = set.get(userId);
           if ( uc == null ) {
               uc = new UserCounter(userId);
               set.put(userId, uc);
           }
           uc.count();
           if ( i == 1000000 ) {
               j++;
               i=0;
               System.out.println(j);
           }
           // mr.addValuePair( userId, nrOfEditPerUser );
           // System.out.println( userId + "\t" );//+ nrOfEditPerUser );
           bw.write( userId + "\t" + nrOfEditPerUser + "\n" );

        }
        bw.flush();
        bw.close();

        statement.close();

        return mr;
    }

    public static void main( String args[] ) throws Exception {
        TimeSeriesObject mr = extractUserActivityForUserID();
        Vector<TimeSeriesObject> v = new Vector<TimeSeriesObject>();
        v.add( mr );

        MeasurementTable mwt = new MeasurementTable();
        mwt.setMessReihen(v );
        mwt.setHeader( "SQL: " + request );
        mwt.writeToFile( new File( "./user_acces.dat"));

        createHistogramm(mr, mr, 100, 0, (int) mr.getMaxY());
    }

    public static Container createHistogramm( TimeSeriesObject mr, TimeSeriesObject r2, int bins, int min, int max ) {
        HistogramChart demo = new HistogramChart( mr.getLabel()  );
        //demo.addSerieWithBinning( r2, bins, min, max );
        demo.addSerieWithBinning( mr, bins, min, max );


        demo.setContentPane( demo.createChartPanel() );
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
//        demo.store( ng.pfad + "img/" , ng.getLangID() +"_" + ng.editReihen.size() + mr.getLabel().replaceAll(" ", "_" ) );
        return demo.getContentPane();
    };

}
