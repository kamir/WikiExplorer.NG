/*
 *  Einlesen der Resultate der Kreuzkorrelation und der Event-Synchronisation
 *  sowie der statischen Netzwerke.
 * 
 *  
 * 
 */
package research.topics.networks.comparison;

import org.apache.hadoopts.data.io.ColumnValueCalculator;
import org.apache.hadoopts.data.io.MessreihenLoader;
import org.apache.hadoopts.data.series.Messreihe;
import java.io.File;
import java.util.Vector;

/**
 *
 * @author kamir
 */
public class NetworkLoaderTool {
    
    Vector<Messreihe> mrDummy = null;
    
    public String label = null;
    
    NetworkComparator comparator = null;
    
    public NetworkLoaderTool( String l , NetworkComparator comp ) { 
        label = l;
        comparator = comp;
        
        initDummyData();
    
        initFilesToView();
    
    }

    private void initFilesToView() {
        
        comparator.jtfA1.setText( fnAccess1 );
        comparator.jtfA2.setText( fnAccess2 );
        comparator.jtfA3.setText( fnAccess3 );
        comparator.jtfE.setText( fnEdits );
        comparator.jtfS.setText( fnStatic );
        
    }
       
    Edits_ES_ResultMapper editsMapper = new Edits_ES_ResultMapper();
    
    public String fnAccess1 = "?";
    public String fnAccess2 = "?";
    public String fnAccess3 = "LOG_Datei015_final.dat";
    public String fnEdits = "editCorrelationResult_tabs2.dat";
    public String fnStatic = "?";
        
    public Messreihe loadAccessNet3() { 
        ColumnValueCalculator mapper = (ColumnValueCalculator)new CC_Media_ResultMapper();
 
        Messreihe a = this.loadRows( comparator.fA3, 1, 2, mapper );
        System.out.println( "* Access3-Net is new ... " + NetworkComparator.project.baseFolder );
        return a;
    };

    public Messreihe loadAccessNet2() { 
        return (Messreihe)mrDummy.elementAt(1);
    };

    
    public Messreihe loadEditNet() { 
        ColumnValueCalculator mapper = (ColumnValueCalculator)new Edits_ES_ResultMapper();
        
        Messreihe a = this.loadRows( comparator.fE,  1, 2, mapper );
        System.out.println( "* Edit-Net is new ...");
        return a;
    };
    
    public Messreihe loadStaticNet() { 
        return (Messreihe)mrDummy.elementAt(3);
    };

    public void initDummyData() {
        mrDummy = NetworkComparator.getTestData();
    }
    
    
    public Messreihe mr1 = null;
    public Messreihe mr2 = null;
    public Messreihe mr3 = null;
    public Messreihe mr4 = null;
    public Messreihe mr5 = null;


    public void reload() {
        mr1 = this.loadAccessNet1();
        mr2 = this.loadAccessNet2();
        mr3 = this.loadAccessNet3();
        mr4 = this.loadEditNet();
        mr5 = this.loadStaticNet();       
    }
    
    
    
    
    
    /***
     * 
     * Methoden zum einlesen ... 
     */
    public Messreihe loadRows( File f, int colLabelA, int colLabelB, int colValue ) {
        Messreihe mrA = null;
        System.out.println(">>> use network link-table : "
                + f.getAbsolutePath() + " -> " + f.exists());
        
        mrA = MessreihenLoader.getLoader().loadMessreihe_For_LinkStrength(
                f, colLabelA, colLabelB, colValue, "\t");
        // mrA = MessreihenLoader.getLoader().loadMessreihe_zr_SC(f, 2, "\t");
        return mrA;
    }

    Messreihe loadAccessNet1() {
        return (Messreihe)mrDummy.elementAt(0);
    }

    private Messreihe loadRows(File f, int colLabelA, int colLabelB, ColumnValueCalculator mapper) {
        Messreihe mrA = null;
        System.out.println(">>> use network link-table : "
                + f.getAbsolutePath() + " -> " + f.exists());
        System.out.println(">>> use value mapper : " + mapper.getName()); 
        
        mrA = MessreihenLoader.getLoader().loadMessreihe_For_LinkStrength(
                f, colLabelA, colLabelB, mapper, "\t");
        return mrA;
    }

 

}
