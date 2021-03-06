/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package research.nodeactivity;

import org.apache.hadoopts.chart.statistic.HistogramChart;
import com.mysql.jdbc.*;
import org.apache.hadoopts.data.series.TimeSeriesObject;
import com.cloudera.wikiexplorer.ng.db.DB;
import extraction.TimeSeriesFactory;
import extraction.TimeSeriesTransformer;
import java.awt.Container;
import java.io.File;
import java.util.Vector;
import org.jfree.ui.RefineryUtilities;
import research.wikinetworks.CCCalculator;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;

/**
 * Benötigt Transformierte Zeitreihen ...
 *
 * @author kamir
 */
public class DaylyDistribution {

    static DB db = null;
    static Connection con = null;
    static Statement st  = null;



    static boolean debug = true;

    /**
     * Test der Verbindung zur DB.
     *
     * @param args
//     */

    static NodeGroup ng = null;//new VphMean32bit();
    static int nrOfDays = 300;

    public static void main(String[] args) throws Exception {

//        db = new WikipediaDB();
//        con = (Connection) db.getMySqlConnection();
//        st  = (Statement) con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY );

        CCCalculator nc = CCCalculator.getCCCalculator();
        nc.ng = ng;  // define the full group of 32 bit nodes ...

        TimeSeriesFactory.doBlock = true; // default is true
        NodeGroup.doShuffle = false;
        NodeGroup.supressFirstPart = false;
        NodeGroup.supressLastPart = false;

        NodeGroup.offsetAtEnd = 150;
        NodeGroup.offsetAtStart = 150;

        /*
         * select one special NodeGroup ...
         */
        selectNodeGroup();
        nc.ng = ng;

        nc.ng.load();  // load the id list

        /*
         * do we supress parts of the days?
         */
        NodeGroup.supressFirstPart = false;
        NodeGroup.supressLastPart = false;

//        /*
//         * Container for all rows ... of that group ...
//         */
//        Vector<TimeSeriesObject> mr = loadAllRows( nc );
//
//        System.out.println( ">>> prepare the cuts ... ");
//
//        Vector<TimeSeriesObject> horiCuts = loadAllRows( nc );
//        for( int i = 0; i < nrOfDays; i++) {
//            TimeSeriesObject cut = getHorizontalCut( mr , i );
//            horiCuts.add(cut);
//        }

        double[][] data = TimeSeriesTransformer.loadTransformedData(ng);

        /**
         * Wie sind alle Zugriffe an aller Tagen in allen Nodes verteilt?
         */
        TimeSeriesObject allAccessValues = createRowOfAllValues( data );
        createHistogramm(ng, allAccessValues, 100, -25, ((int) allAccessValues.getMaxY() + 1) );


        /**
         * Wie sind die std, av und Q=av/std an den Tagen verteilt?
         */
        Vector<TimeSeriesObject> mr = createDailyRows( data );
        TimeSeriesObject mrDayly_av = new TimeSeriesObject("daily   <access>");
        TimeSeriesObject mrDayly_std = new TimeSeriesObject("daily   std(access)");
        TimeSeriesObject mrDayly_av_by_std = new TimeSeriesObject("daily   std / <access>");
        for( TimeSeriesObject m : mr ) {
            m.calcAverage();
            double av = m.getAvarage();
            double std = m.getStddev();
            double q = std/av;
            mrDayly_av.addValue(av);
            mrDayly_std.addValue(std);
            mrDayly_av_by_std.addValue(q);
        }
        createHistogramm(ng, mrDayly_av, 100, -24, ( (int)mrDayly_av.getMaxY() + 1) );
        createHistogramm(ng, mrDayly_std, 100, -5, ( (int)mrDayly_std.getMaxY() + 1));
        createHistogramm(ng, mrDayly_av_by_std, 200, -5, 5);





        /*
         * Show us the dayly-Activity distribution ...
         */
        // DaylyDistributionPanel.open(mr);


    }

    public static Container createHistogramm( NodeGroup ng,TimeSeriesObject mr, int bins, int min, int max ) {
        HistogramChart demo = new HistogramChart( mr.getLabel()  );

        demo.addSerieWithBinning( mr, bins, min, max );
        demo.setContentPane( demo.createChartPanel() );
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
        //demo.store( ng.pfad + "img/" , ng.getLangID() +"_" + ng.editReihen.size() + mr.getLabel().replaceAll(" ", "_" ) );
        return demo.getContentPane();
    };

    public static Vector<TimeSeriesObject> loadAllRows( CCCalculator nc ) {
        nc.ng.checkAccessTimeSeries();
        System.out.println( nc.ng.getAaccessReihen().size() + " rows loaded ...");
        return nc.ng.getAaccessReihen();
    }

    public static void selectNodeGroup() {
        File f = NodeGroup.selectNodegroupFile();
        ng = new NodeGroup(f);
        //ng.checkForDoubleIds();
    }

    public static TimeSeriesObject getHorizontalCut(Vector<TimeSeriesObject> mrs, int i) {
        TimeSeriesObject mr = new TimeSeriesObject();
        mr.setLabel( "Cut at: " + i );
        int c= 0;
        for( TimeSeriesObject r : mrs ) {
            mr.addValuePair(1.0*c, (Double)r.yValues.elementAt(i) );
            c++;
        }
        return mr;
    }

    private static Vector<TimeSeriesObject> createDailyRows(double[][] data) {
        Vector<TimeSeriesObject> v = new Vector<TimeSeriesObject>();
        int day_max = data.length;
        int rows_max = data[0].length;
        System.out.println( day_max + " " + rows_max);
        for( int i = 0; i < day_max; i++ ) {
        
            TimeSeriesObject r = new TimeSeriesObject();
            r.setLabel("day_"+(i+1) );
            for( int j = 0; j < rows_max; j++ ) {
                r.addValuePair( i , data[i][j] );
            }
            v.add(r);
        }
        return v;
    }

    private static TimeSeriesObject createRowOfAllValues(double[][] data) {
        TimeSeriesObject mr = new  TimeSeriesObject();
        mr.setLabel("allDaysAccessValues");

        int day_max = data.length;
        int rows_max = data[0].length;
        for( int i = 0; i < day_max; i++ ) {

            for( int j = 0; j < rows_max; j++ ) {
                mr.addValuePair( i , data[i][j] );
            }
        }
        return mr;
    }

//    private static TimeSeriesObject createRowOfAllValues_LOG(double[][] data) {
//        TimeSeriesObject mr = new  TimeSeriesObject();
//        mr.setLabel("allDaysAccessValues");
//
//        int day_max = data.length;
//        int rows_max = data[0].length;
//        for( int i = 0; i < day_max; i++ ) {
//
//            for( int j = 0; j < rows_max; j++ ) {
//                mr.addValuePair( i , Math.log10( data[i][j] ) );
//            }
//        }
//        return mr;
//    }


}
