/**
 * 
 *   RIS für Wikipaper
 * 
 *   Abbildung 7 - RIS der Edit-Zeitreihen
 * 
 **/

package research.sqlclient.phase2;

import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.data.series.Messreihe;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;

import com.cloudera.wikiexplorer.ng.util.TimeLog;

/**
 *
 * @author Mirko Kämpf
 * 
 */
public class Topic2A {
    
    

    // HAUPTPROGRAMM
    public static void main(String[] args ) throws Exception {
        Topic2A prg = new Topic2A();
    }

    TimeLog tl = new TimeLog();

    // Quellpfad
    String folderWithEditTS = "S:/WIKIPEDIA_PHASE2/edit_events";

    
    // HIER wurden zuvor die ID und die COUNTS abgelegt ...
    String countsFile = "/Volumes/MyExternalDrive/CALCULATIONS/data/out/topic2A/RIS/all.review.counts.dat";
         
    String label = "197_100";

    public Topic2A() throws Exception {
        
               int variante = 1;
        
        pfade = new String[5];
        labels = new String[5];
        
        int risBinning = 6;
        int risScale = 10;
        
        switch( variante ) {
            case 1 : init1000MostActiveByEdits();
                     break;
            case 2 : initAllByEdits();
                     break;
        }    
                
        init();        
                
        countsFile = "G:/DEV/MLU/Wiki/TimeSeriesToolbox/data/in/node_groups/" + label + "_most_active_by_edist.dat";
    
        tl.setStamp("Start counting ... ");      
        countEditsPerNode();        
        tl.setStamp("Finished counting ... ");
        
        BufferedReader br = new BufferedReader( new FileReader( countsFile ));

        // maximale Anzahl von Edits ...
        int max = 0;

        while( br.ready() ) {
            String line = br.readLine();
            if( !line.startsWith( "#" ) ) {
                
                StringTokenizer st = new StringTokenizer( line );
                
                int id = Integer.parseInt( st.nextToken() );
                int v = Integer.parseInt( st.nextToken() );

                if ( v > max ) max = v;

                processNodeID( id,v );

            }
        }
        System.out.println( ">>> max edits=" + max + "\n" );
        br.close();
        // ---------------------------------------------------------------------
       
        
        // Anzahl der Ids pro Grenze auflisten ..
        list();

        collect();

        store();
        
        exit(); 
        
    };

    /**
     *   Vorbereiten der Container ...
     */
    Vector[] idContainer = null;
    double data[][] = new double[8][26];

    public void init() { 
        
        idContainer = new Vector[grenzen.length];
        
        // TimeLog stumm stellen
        tl.quite = true;
        
        for( int i = 0 ; i < idContainer.length; i++ ) { 
            idContainer[i] = new Vector();        
        };
    };

    /**
     *   Listet die Container und deren Größen auf ...
     */
    public void list() {
        for( int i = 0 ; i < idContainer.length; i++ ) {
            System.out.println( grenzen[i] + "\t" + idContainer[i].size() );
        };
    };




    /**
     * Für die vorgegebenen Klassen wird nun ermittelt, 
     * ob eine Node-ID mehr oder weniger Edits hat.
     *    
     * 
     * @param id
     * @param counts 
     */
    int[] grenzen = { 4,8,16,32,64,128,256 };
    
    public void processNodeID( int id, int counts ) {
        int cc = 0;

        // für jede Grenze ...
        for ( int i = 0; i < grenzen.length ; i++ ) {
            // fallse 
            if ( counts > grenzen[i] ) { 
                // index hochzählen
                cc++;
            }
            else { 

            }
        }

        if ( cc > idContainer.length-1 ) cc = idContainer.length-1;

        // die ID wird in den "höchsten" Container einsortiert.
        idContainer[cc].add(id);
    };




// Dateien mit den Verteilungen  
public void collect() {
    int errors = 0;

    System.out.println();

    for( int i = 0 ; i < idContainer.length; i++ ) {

        System.out.println( grenzen[i] + "\t" + idContainer[i].size() );

        for( int j=0; j < idContainer[i].size()-1 ; j++ ) {
            
            int id = (Integer)idContainer[i].elementAt(j);
            
            //String pfad = "/home/wikidb/WorkingDATA/RESULTS/edits/topic2/alle/ris/";
            
            String pfad = "/Volumes/MyExternalDrive/CALCULATIONS/data/out/edits/topic2A/RIS/rows/" + label;
            String fn = "PageID_"+id+"_BINNING_6.verteilung.dat";
            try {
                BufferedReader br = new BufferedReader( new FileReader( pfad + fn ));
                String l1 = br.readLine();
                String l2 = br.readLine();
                String l3 = br.readLine();
                String l4 = br.readLine();
                
                
                for( int k=0; k < 26; k++) { 
                    String line = br.readLine();
                    StringTokenizer st = new StringTokenizer( line );
                    st.nextToken();
                    double value = Double.parseDouble( st.nextToken() );

                    data[i][k] = data[i][k] + value;
                }
            }
            catch(Exception ex) {
                errors++;
                // ex.printStackTrace();
                System.err.println( "ERROR: " + pfad + fn );
            }
        }
    }
    System.out.println("\n ERRORS: " + errors );
};

    private void store() throws Exception {

        // for ( int i = 0; i<8; i++ ) {


            DecimalFormat df = new DecimalFormat("0.00000");            
            StringBuffer line = new StringBuffer();
            
//            String fn = "/Volumes/MyExternalDrive/CALCULATIONS/data/out/topic2/RIS_G " +i+".dat";
            String fn = "/Volumes/MyExternalDrive/CALCULATIONS/data/out/topic2A/RIS/RIS_G_ALL.dat";

            FileWriter fw = new FileWriter( fn );
            for( int j = 0; j < 26; j++ ) {
                for ( int k = 0; k < grenzen.length; k++ ) { 
                    int total = idContainer[k].size();
                    double value = data[k][j];
                    line.append( df.format( value ) + "\t" + df.format( value / (1.0 * total ) ) + "\t"  );
                }
                fw.write( line.toString() + "\n" );
                line = new StringBuffer();
            };
            fw.flush();
            fw.close();
       // };

    }

    
    private void countEditsPerNode() throws IOException {
                
        File output = new File( this.countsFile );
        FileWriter fw = new FileWriter( output );
        File input = new File( folderWithEditTS );
        
        // Variante 1 (alt) ----------------------------------------------------
        Vector<String> ids = new Vector<String>();
        
        
        //----------------------------------------------------------------------
        // Node-Groupe auswählen ...
        //
        //FileReader fr = new FileReader("/home/kamir/NetBeansProjects/SQLClient/data/out/filter1/lang-60-en.ids.dat");
        FileReader fr = new FileReader("/Volumes/MyExternalDrive/CALCULATIONS/data/out/topic2/extract_edits.dat");
        BufferedReader br = new BufferedReader(fr);

        while (br.ready()) {  // aus der Node-Group-Liste lesen ...

            String line = br.readLine();

            if (!(line.startsWith("#"))) {
                StringTokenizer st = new StringTokenizer(line);

                int id = Integer.parseInt((String) st.nextElement());

                ids.add(id + "");
            }
        }
        br.close();
        
        File[] nodes = null;
        for( String id : ids ) { 
            File f = new File( folderWithEditTS + "/" + id + ".countedEvents.d.revisions.dat" );        
            if ( f.exists() ) {
                System.out.println( f.getAbsolutePath() );
                // wir kennen nun die Node-ID und das File mit den Edits ...
                countEditsForID( f, fw );                 
                z++;
            }   
        }
        
        System.out.println(">>> z=" +  z + " nodes are used ... " );
        fw.flush();
        fw.close();
        
        
//        // Variante 2 (neuer) --------------------------------------------------
//        File[] nodes = input.listFiles();
//        for( File f : nodes ) { 
//            if( f.getName().endsWith( ".revisions.dat" ) && ( f.getName().endsWith( ".d.revisions.dat" ) ) ) { 
//                // wir kennen nun die Node-ID und das File mit den Edits ...
//                countEditsForID( f, fw );
//            }
//        }
//        System.out.println(">>> z=" +  z + " nodes are used ... " );
//        fw.flush();
//        fw.close();
    }
    
    Vector<Messreihe> mrRIS_ZR = new Vector<Messreihe>();

    static int z=0;
    private void countEditsForID(File f, FileWriter fw) throws IOException {
        
        // für das Sammeln der Return Intervalle ...
        Messreihe mr = new Messreihe();
        
        int index = f.getName().indexOf(".");
        z++;
        String id = f.getName().substring(0, index);
        int idI = Integer.parseInt(id);
        // System.out.println( z + "\t" + id + "\t" + f.getName() + "\n" );
        
        int nr = 0;
        int c = 0;
        
        // *** Datei Lesen und summieren ...
        BufferedReader br = new BufferedReader( new FileReader( f ) );
        while( br.ready() ) { 
            
            mr.setLabel( id + "_RIS");

            
            int t0 = 0;
            int t1 = 0;
            int dt = 0;
            
            String line = br.readLine();
            if ( line.startsWith("#") ) { 
            
            }
            else {
                StringTokenizer st = new StringTokenizer(line);
                //System.out.println( line );
                
                // aus den zwei jeweils aufeinander folgenden Werten
                // lassen sich hier die Return Intervalle ermitteln ... 
                // ... diese werden in einer neuen ZR gesammelt und dann
                // mit all diesen eine DFA gerechnet ...
                String s1 = st.nextToken();
                String s2 = st.nextToken();
                double i = Double.parseDouble(s2);
                
                // ANZAHL der EDITS zählen ...
                nr = nr + (int)i;
            
                c++;
                if ( c == 1 ) { 
                    t1 = (int)Double.parseDouble(s1);
                }
                else { 
                    t0 = t1;
                    t1 = (int)Double.parseDouble(s1);
                }
                dt = t1 - t0;
                mr.addValue(dt);
            }
        }
        System.out.println( z + "\t" + id+ "\t" + nr + "\t" + f.getName() );
        // if ( checkIfDistributionsFileExists( id ) ) {
            fw.write( id + "\t" + nr + "\n" );
        //}    
        if ( z < 100 ) {
            mrRIS_ZR.add(mr);
        }    
        processNodeID( idI, nr );    
    }

    /**
     * Programm beenden und Log ausgeben.
     */
    private void exit() {
        
        tl.print(System.out);
        System.out.println( mrRIS_ZR.size() );
        
        
        
        MultiChart.open(mrRIS_ZR);
        
        
        // System.exit(0);
    }

//    private boolean _checkIfDistributionsFileExists(String id) {
//        String fn = "/Volumes/MyExternalDrive/CALCULATIONS/data/out/topic2/PageID_"+id+"_BINNING_5.verteilung.dat";
//        File f = new File( fn );
//        return true; //f.exists();
//    }
    
    static String[] pfade = null;
    static String[] labels = null;
        
    private static void init1000MostActiveByEdits() {
        
        labels[0] = "197_1000"; //OK
        labels[1] = "52_1000";
        labels[2] = "60_1000";
        labels[3] = "62_1000";
        labels[4] = "72_1000";
              
        for( int i = 0; i < 5; i++ ) { 
            pfade[i] = "G:/DEV/MLU/Wiki/TimeSeriesToolbox/data/in/node_groups/" + labels[i] + "_most_active_by_edist.dat";
        }
    }

    private static void initAllByEdits() {
        
        labels[0] = "197_ru"; //OK
        labels[1] = "52_de";
        labels[2] = "60_en";
        labels[3] = "62_sp";
        labels[4] = "72_fr";
        
        for( int i = 0; i < 5; i++ ) { 
            pfade[i] = "/Volumes/MyExternalDrive/CALCULATIONS/data/out/filter1/lang-" + labels[i] + "-ru.ids.dat"; 
        }
 
    }


}