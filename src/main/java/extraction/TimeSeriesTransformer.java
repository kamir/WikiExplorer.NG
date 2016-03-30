/*
 *  Ein Vector v mit n Messreihen der Länge l wird
 *  in l Messreien der Länge l2=n=v.size() umgewandelt, damit wir
 *  daraus Histogramme der Verteilungen der Werte erstellen.
 *
 */

package extraction;

import org.apache.hadoopts.chart.statistic.HistogramChart;
import org.apache.hadoopts.data.series.Messreihe;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.jfree.ui.RefineryUtilities;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;

/**
 *
 * @author kamir
 */
public class TimeSeriesTransformer {

    public static String pfad = "G:/PHYSICS/PHASE2/data/out/node_groups/";

    static NodeGroup ng = null;



    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main( String[] args ) throws Exception {
        selectNodeGroup();

        File fout = new File( NodeGroup.pfad + ng.fn + "_T_.dat" );
        BufferedWriter bw = new BufferedWriter( new FileWriter( fout.getAbsolutePath() ));

        Messreihe mr = null;
        String lim = "\n";
        String tab = "\t";

        // double[][] data = new double[300][ng.ids.length];

        System.out.println( (ng.ids.length - 293555) );
        System.out.println( ng.ids.length );


        int k = 0;
        int row = 0;

                /**
         * Wie sind die std, av und Q=av/std in den Nodes verteilt?
         */

        Messreihe mrDayly_av = new Messreihe("per node   <access>");
        Messreihe mrDayly_std = new Messreihe("per node   std(access)");
        Messreihe mrDayly_av_by_std = new Messreihe("per node   std / <access>");




        for( int id : ng.ids ) {
            int day = 0;
            // OHNE BLOCKEN UND FILTERN !!!
            mr = TimeSeriesFactory.prepareAccessDataSTUNDE(id, 300*24);
            mr = mr.setBinningX_sum(24);
            
            mr.calcAverage();
            double av = mr.getAvarage();
            double std = mr.getStddev();
            double q = av / std;
            mrDayly_av.addValue(av);
            mrDayly_std.addValue(std);
            mrDayly_av_by_std.addValue(q);



//            // Mit Filter in der NodeGroup
//            ng.loadAccessForOneID(id);

//            for( Object a : mr.yValues ) {
//                bw.write( (Double)a + tab );
//                //data[day][row] = (Double)a;
//                day++;
//            }
            // System.out.print("\nd="+ day+" r=" + row + "\n");
            if ( k == 5000 ) {
                k=0;
                System.out.print("\nd="+ day+" r=" + row + "\n");
            }
            k++;
            row++;
            bw.write(lim);
        }
        bw.flush();
        bw.close();

        createHistogramm(ng, mrDayly_av, 100, -24, ( (int)mrDayly_av.getMaxY() + 1) );
        createHistogramm(ng, mrDayly_std, 100, -5, ( (int)mrDayly_std.getMaxY() + 1) );
        createHistogramm(ng, mrDayly_av_by_std, 200, -5, 5);

        /*
         * storeBinaryArray ...
         */
//       	FileOutputStream fos = new FileOutputStream( NodeGroup.pfad + ng.fn + "_T_.bin" );
//	ObjectOutputStream oos = new ObjectOutputStream(fos);
//
//        oos.writeObject(data );
//
//        oos.flush();
//        oos.close();
//
//        System.exit(0);

        
    }

    static public double[][] loadTransformedData(NodeGroup ng) throws FileNotFoundException, IOException, ClassNotFoundException {

        double[][] data = null;
        FileInputStream fis = new FileInputStream( NodeGroup.pfad + ng.fn + "_T_.bin" );
	ObjectInputStream ois = new ObjectInputStream(fis);

	data = (double[][])ois.readObject();
	
	ois.close();

        return data;

    }


        /**
     * afterwards, the propertie ng is defined or null
     */
    public static void selectNodeGroup() {
        File f = NodeGroup.selectNodegroupFile();
        ng = new NodeGroup(f);
        //ng.checkForDoubleIds();
    }

    public static void createHistogramm( NodeGroup ng,Messreihe mr, int bins, int min, int max ) {
        HistogramChart demo = new HistogramChart( mr.getLabel()  );
        demo.addSerieWithBinning( mr, bins, min, max );
        demo.setContentPane( demo.createChartPanel() );
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
        // demo.store( ng.pfad + "img/" , ng.getLangID() +"_" + ng.editReihen.size() + mr.getLabel().replaceAll(" ", "_" ) );
    };

}
