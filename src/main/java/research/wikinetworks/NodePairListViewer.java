/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package research.wikinetworks;

import java.io.File;
import java.io.IOException;
import javax.swing.filechooser.FileFilter;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;

/**
 *
 * @author kamir
 */
public class NodePairListViewer {

    public static String pfad = "G:/PHYSICS/PHASE2/data/out/node_groups/filtered/";

    static NodeGroup ng = null;
   
    

    public static void main( String[] args ) throws Exception {
        
        File f = selectNodePairListFile("CC Links ... ");
        
        NodePairListViewer view = new NodePairListViewer();
        view.createClearLists(f);
    }

    File f2 = new File( "G:/DEV/MLU/Wiki/SQLClient/32bit_VphMean.dat.ngLINKED_both.pairs.dat" ); //selectNodePairListFile("Statische Links ... ");
        
    public void createClearLists( File f ) throws IOException {

        NodePairList statischesNetz = new NodePairList();
        statischesNetz.read( f2 );

        NodePairList assumed = new NodePairList();
        assumed.read( f );

        assumed.writeClearList( statischesNetz );


//        ng = assumed.ng;
//        ng.checkAccessTimeSeries();
//        ng.checkEditTimeSeries();
     
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
