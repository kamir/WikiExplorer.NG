package com.cloudera.wikiexplorer.ng.util;

/**
 * Diese Klasse ist die Basis der Result-Recorder und erzeugt je nach Bedarf
 * die neuen Log-Files mit den jeweiligen Loop-IDs als Benennung.
 */



import java.io.File;
import java.io.FileWriter;
import java.util.Enumeration;
import java.util.Vector;

/**
 *
 * @author kamir
 */
public class GeneralResultRecorder {

    public static String currentSimulationLabel = null;

    public static int ID = 0;
    public static String LOOPID = "1";

    public static String folder = "./data/";
    
    public static String args[] =  {};
    public static String labels[] = {};

    public static Vector<Double>[] cols = null;
    public static String[] colLabels = {};
    public static File file = null;

//    public static void initParameter( String[] args, String[] labels ) {
//        GeneralResultRecorder.args = args;
//        GeneralResultRecorder.labels = labels;
//    };

    public static void init(String pfad, final int colCount, String[] messwerteLabels) {
        cols = new Vector[colCount];
        colLabels = messwerteLabels;
        file = new File(pfad);
        for (int i = 0; i < cols.length; i++) {
            cols[i] = new Vector<Double>();
        }        
    }


    public static void putValue(double value, int col) {
        if( cols != null ) {
            cols[col - 1].add(value);
        }
        //System.out.println( "STORE: " + value + " - " + col );

        
    };

    public static void store() {
        try {
            System.out.print( ">>> Save results to file: " );
            
            //  if ( !file.exists() ) {

            try {
                System.out.println( file.getAbsolutePath() );
                StringBuffer sb = new StringBuffer();
//                if ( simulation.MainGUI.mainPrg != null ) {
//                    sb.append("# Parameter:\n").append(simulation.MainGUI.mainPrg.getParameterListe()).append( "\n#");
//                }

                
               
                for( int i = 0; i < colLabels.length ; i++ ) {
                    sb.append(colLabels[i]).append( "\t");
                } 
                sb.append( "\n" );
               
                for ( int j = 0; j < cols[0].size() ; j++ ) {
                    try {
                        for( int i = 0; i < colLabels.length; i++ ) {
                            Vector v = cols[i];
                            if ( v.size() < j  ) {
                                sb.append( "?\t" );
                            }
                            else {
                                sb.append(v.elementAt(j)).append( "\t");
                            };
                        }
                    }
                    catch (Exception ex) {
                        System.out.println("*** (Anzahl der Spalten) != (Anzahl der Labels) ***");
                        // ex.printStackTrace();
                    }
                    finally{
                        sb.append("\n");
                    }

                }

                FileWriter fw = new FileWriter( file );
//
//                String data = sb.toString();
//                String line = data.replace('.', ',' );
                fw.write( sb.toString() );
                fw.close();
            }
            catch(Exception ex) {
                ex.printStackTrace();
            };
       // };

//            FileReader fr = new FileReader(file);
//
//            BufferedReader br = new BufferedReader(fr);
//
//            StringBuffer lines = new StringBuffer();
//
//            // Lesen der vorhandenen Zeilen...
//            int c = 0;
//            while (br.ready()) {
//                String line = br.readLine();
//                lines.append(line);
//                lines.append("\n");
//                c++;
//            }
//            // System.out.println( c );
//            br.close();
//
//
//            // Falls leer
//            // Label erzeugen
//            // sonst alte Daten schreiben
//
//            FileWriter fw = new FileWriter(file);
//            lines.append(ID + "\t");
//
//           // lines.append("*0*\t");
//
//            // neue Daten schreiben
//            for (int i = 0; i < cols.length; i++) {
//                double value = calcAverage(cols[i]);
//
//                //System.out.println( value + " ..." );
//                lines.append( value + "\t");
//            }
//
//    //        lines.append("*1*\t");
//
//            for( int i = 0; i < args.length; i++ ) {
//                    lines.append( args[i] + "\t" );
//            };
//
//      //      lines.append("*2*\t");
//
//            lines.append("\n");
//
//            String line = lines.toString();
//            line = line.replace('.',',');
//
//            fw.write(line);
//            fw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] agrs) {

        String[] labels = {"x", "y"};

        GeneralResultRecorder.init("./RESULTS" + LOOPID  + ".dat", 2, labels);

        GeneralResultRecorder.putValue(1.0, 1);
        GeneralResultRecorder.putValue(2.0, 1);
        GeneralResultRecorder.putValue(0.5, 1);
        GeneralResultRecorder.putValue(1.5, 2);
        GeneralResultRecorder.putValue(3.0, 2);
        GeneralResultRecorder.putValue(3.0, 2);

        GeneralResultRecorder.putValue(calcAverage(cols[0]), 1);
        GeneralResultRecorder.putValue(calcAverage(cols[1]), 2);

        GeneralResultRecorder.store();

        System.exit(0);

    }
    
    private static double calcAverage(Vector<Double> vector) {
        double size = vector.size() * 1.0;

        double summe = 0.0;

        Enumeration<Double> en = vector.elements();
        while (en.hasMoreElements()) {
            summe = summe + en.nextElement();
        };

        return summe / size;
    }
}
