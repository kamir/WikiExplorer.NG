/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cloudera.wikiexplorer.ng.util.nodegroups;

import org.apache.hadoopts.chart.statistic.HistogramChart;
import org.apache.hadoopts.data.series.Messreihe;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.Vector;
import org.jfree.ui.RefineryUtilities;
import research.nodeactivity.ExtractNodeActivity;
import research.wikinetworks.PageLanguageChecker;

/**
 *
 * @author kamir
 */
public class EditActivityFilter {

    static public String extension = "_most_active_by_edits2.dat";
    static public String extension2 = "_most_active_by_access.dat";

    static public String ext_ng = "ng";

    /**
     * aus der Datei "all_activity_by_edits2.dat" wird für einzelne Sprachen
     * die Liste der jeweils n activsten ermittelt.
     *
     * @param args
     * @throws Exception
     */
    public static void main( String[] args ) throws Exception {

        PageLanguageChecker.debug = false;

        // Input
        String fn = "all_activity_by_edits.dat";
        int[] thresholds = { 20, 100, 200, 500, 1000 };
        int[] langIDs = { -1,52,62,72,60,197};


        for( int threshold : thresholds ) {
            for( int langID : langIDs ) {


                Messreihe mr = loadFromEditActivityFile( ExtractNodeActivity.pfad + fn, langID );
                createHistogramm( mr );

                Messreihe mr2 = FilteredNodeGroup.filterByYValue_max_N( mr, threshold );
                createHistogramm2( mr2 );

                FilteredNodeGroup fng = new FilteredNodeGroup( langID + "_" + threshold + extension , mr2);
                fng.checkForDoubleIds();

                fng.store();
            }
        }
        System.out.println("ENDE");
        System.exit(0);
    };

     // in der ersten Spalte steht die ID und # sind Kommentare ...
    public static Messreihe loadFromEditActivityFile( String filename , int soll_langID ) throws Exception {

        Messreihe mr = new Messreihe();
        mr.setLabel( soll_langID + "_" + filename );

        boolean filterLang = false;

        // langID = -1 ==> alle , sonst filtern ...
        if( soll_langID == -1 ) filterLang = false;
        else {
            filterLang = true;
        }

        Vector<Integer> ids = new Vector<Integer>();

        FileReader fr = new FileReader(filename);

        BufferedReader br = new BufferedReader(fr);


         int langId = 0;
         int nodeId = 0;
         int nrOfEdits = 0;

        while (br.ready()) {
            boolean doUse = false;
            String line = br.readLine();

            if (!(
                    (line.startsWith("#")|| line.startsWith("null"))
                  )  ) {

                // System.out.println(line);
                StringTokenizer st = new StringTokenizer(line);

               langId = Integer.parseInt((String) st.nextElement());
               nodeId = Integer.parseInt((String) st.nextElement());
               nrOfEdits = Integer.parseInt((String) st.nextElement());

                if ( filterLang ) {
                    if ( langId == soll_langID ) {
                        doUse = true;
                    }
                }
                else {
                        doUse = true;
                }

                if( doUse ) {
                    ids.add(nodeId);
                    mr.addValuePair(nodeId, nrOfEdits);
                }
            }
        }
        br.close();
        return mr;
    }

    public static void createHistogramm( Messreihe mr ) {
        HistogramChart demo = new HistogramChart( mr.getLabel()  );
        demo.addSerieWithBinning( mr, 100, 0, 200 );
        demo.setContentPane( demo.createChartPanel() );
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    };

    private static void createHistogramm2(Messreihe mr2) {
        HistogramChart demo = new HistogramChart( mr2.getLabel()  );
        demo.addSerieWithBinning( mr2, 100, 50, 5000 );
        demo.setContentPane( demo.createChartPanel() );
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }


}
