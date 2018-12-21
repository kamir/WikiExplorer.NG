/*
 *  [OK] Lese die Link Liste
 *  [OK] Ermittle die NodeGroup aller beteiligten Links
 *  [OK] Ermittle k (Degree) aller Nodes
 *
 *  [OK] Ermittle Activity (edits)
         ... und access) aller Nodes
 *         Erstelle Tabelle [id,k,act_access,act_edits]
 * 
 *  [OK] Erstelle Diagramm k vs. act_access
 *       Erstelle Diagramm k vs. act_edits
 *
 */

package research.wikinetworks;

import org.apache.hadoopts.chart.statistic.HistogramChart;
import org.apache.hadoopts.data.series.TimeSeriesObject;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import javax.swing.filechooser.FileFilter;
import org.jfree.ui.RefineryUtilities;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;

/**
 *
 * @author kamir
 */
public class DegreeActivityCheck {

    public static String pfad = "G:/PHYSICS/PHASE2/data/out/node_groups/filtered/";

    static NodeGroup ng = null;



    public static void main( String[] args ) throws Exception {

        // File f = selectNodePairListFile("CC Links ... ");

        DegreeActivityCheck view = new DegreeActivityCheck();
        view.createLists();
    }

    File f2 = new File( pfad + "32bit_VphMean.dat.ngLINKED_both.pairs.dat" ); //selectNodePairListFile("Statische Links ... ");

    public void createLists() throws IOException {

        NodePairList statischesNetz = new NodePairList();
        statischesNetz.read( f2 );

        System.out.println(">>> Net loaded ..." );


        // degrees 
        Hashtable<Integer,Integer> degreeOfNodes = statischesNetz.getDegreeList();

        System.out.println(">>> Degreelist created ..." );

        statischesNetz.ng.checkAccessTimeSeries();

        TimeSeriesObject mrCheeck = new TimeSeriesObject();

        // check for  detrendet data sets ...
        for ( TimeSeriesObject mr : statischesNetz.ng.getAaccessReihen() ) {

            int lenge = mr.yValues.size();

            double summe = mr.summeY();

            double mwReadData = (summe ) / lenge;
            mrCheeck.addValue(mwReadData);
        }
        createHistogramm(mrCheeck);


        System.out.println("\n>>> Rows loaded ..." );

//        TimeSeriesObject mr1 = new TimeSeriesObject();
//        mr1.setLabel("activity_SUM vs. k");
//
//        TimeSeriesObject mr2 = new TimeSeriesObject();
//        mr2.setLabel("activity_MAX vs. k");
//
//        BufferedWriter bw = new BufferedWriter( new FileWriter( "./out.dat" ) );
//
//        TimeSeriesObject histK = new TimeSeriesObject();
//        histK.setLabel("Hist. k");
//
//        TimeSeriesObject histActivity1 = new TimeSeriesObject();
//        histActivity1.setLabel("Hist. activity SUM");
//
//        TimeSeriesObject histActivity2 = new TimeSeriesObject();
//        histActivity2.setLabel("Hist. activity MAX");
//
//
//        double maxDegree = 0;
//        int i = 0;
//        while ( i < statischesNetz.ng.ids.length ) {
//            int id = statischesNetz.ng.ids[i];
//            double degree = degreeOfNodes.get( new Integer(id) );
//            double actSum = statischesNetz.ng.accessActivity[ i ];
//            double actMAX = statischesNetz.ng.accessMAX[ i ];
//
//            bw.write( id + "\t" + degree + "\t"+ actSum + "\t" + actMAX + "\n" );
//
//            mr1.addValuePair(  degree, actSum );
//            mr2.addValuePair(  degree, actMAX  );
//
//            histK.addValue(degree);
//            histActivity1.addValue( actSum );
//            histActivity2.addValue( actMAX );
//
//            if ( degree > maxDegree ) maxDegree = degree;
//            i++;
//        }
//        bw.flush();
//        bw.close();
//
//        System.out.println(">>> File created ..." );
//
//        Vector<TimeSeriesObject> v = new Vector<TimeSeriesObject>();
//        v.add( mr1 );
//
//        Vector<TimeSeriesObject> v2 = new Vector<TimeSeriesObject>();
//        v2.add( mr2 );
//
//        MyXYPlot.xRangDEFAULT_MAX = (int)maxDegree;
//        MyXYPlot.xRangDEFAULT_MIN = 0;
//        MyXYPlot.yRangDEFAULT_MAX = 2500;
//        MyXYPlot.yRangDEFAULT_MIN = 0;
//
//        MyXYPlot.rowONEDefualtColor = Color.BLACK;
//        MyXYPlot.open( v , "activity SUM vs. k", "k", "activity SUM", true);
//
//        createHistogramm(histActivity1);
//        createHistogramm(histActivity2);
//        createHistogramm(histK);
//
//        MyXYPlot.xRangDEFAULT_MAX = (int)maxDegree;
//        MyXYPlot.xRangDEFAULT_MIN = 0;
//        MyXYPlot.yRangDEFAULT_MAX = 1500;
//        MyXYPlot.yRangDEFAULT_MIN = 0;
//        MyXYPlot.open( v2 , "activity MAX vs. k", "k", "activity MAX", true);

//

    };

    public static void createHistogramm( TimeSeriesObject mr ) {
        HistogramChart demo = new HistogramChart( mr.getLabel()  );
        demo.addSerieWithBinning( mr, 100, 0, mr.getMaxY() );
        demo.setContentPane( demo.createChartPanel() );
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    };

//    public static TimeSeriesObject[] getReihenForNodePair( String s) {
//        TimeSeriesObject[] mr = new TimeSeriesObject[2];
//
//        int is[] = getIdsForNodePair(s);
//        mr[0] = loadRows(is[0]);
//        mr[1] = loadRows(is[1]);
//
//        return mr;
//    }



//
//    private TimeSeriesObject[] loadRows(Integer id) throws Exception {
//        TimeSeriesObject[] rows = new TimeSeriesObject[2];
//
//        TimeSeriesObject edits = ng.loadEditsForOneID(id, NodeGroup.time_scale);
//        TimeSeriesObject access = ng.loadAccessForOneID(id);
//
//
//        if ( NetworkComparator.doCutOffAtSTART ) {
//            access.supressAtStart( NetworkComparator.START_CUTTOF_LENGTH, 0.0);
//        }
//        access = TimeSeriesFactory.blockSpecialValues(access);
//        if ( supressFirstPart ) access.supressAtStart(150, 0);
//        if ( supressLastPart ) access.supressAtEnd( 150, 0);
//
//        rows[0] = edits;
//        rows[1] = access;
//
//        return rows;
//
//    }


    public static File selectNodePairListFile( String t ) {
        javax.swing.JFileChooser jfc = new javax.swing.JFileChooser( new File( pfad ) );
        jfc.setDialogTitle(t);
        jfc.setFileFilter(  (FileFilter) NodePair.getFileFilter());

        // jfc.setDialogTitle("Select a prefiltered NodePair List ...");
        jfc.setSize( 800,600 );

        jfc.showOpenDialog(null);

        File f = jfc.getSelectedFile();
        if ( f == null ) {
            System.err.println(">>> KEINE NodePair Liste gewählt !");
        }
        else {
            System.err.println(">>> NodePair Liste : " + f.getAbsolutePath() + " gewählt !");
        }
        return f;
    };





}
