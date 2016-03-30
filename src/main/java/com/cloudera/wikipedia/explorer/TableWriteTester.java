/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cloudera.wikipedia.explorer;

import org.apache.hadoopts.chart.simple.MultiChartDistComp;
import static org.apache.hadoopts.chart.simple.MultiChartDistComp.autoClose;
import org.apache.hadoopts.data.series.Messreihe;
import org.apache.hadoopts.data.export.MesswertTabelle;
import java.awt.Container;
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

/**
 *
 * @author kamir
 */
public class TableWriteTester {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
 
                stdlib.StdRandom.initRandomGen(1);
                
                //dialog.setVisible(true);
                File f2 = new File( "./test.tab");

                MesswertTabelle tab = new MesswertTabelle();
                tab.fill_UP_VALUE = -100.0;
                tab.singleX = true;

                Messreihe r1 = Messreihe.getGaussianDistribution(5);
                Messreihe r2 = Messreihe.getGaussianDistribution(15);
                Messreihe r3 = Messreihe.getGaussianDistribution(10);
                Messreihe r4 = Messreihe.getGaussianDistribution(50);
                
                Vector<Messreihe> v = new Vector<Messreihe>();
                v.add(r1);
                v.add(r2);
                v.add(r3);
                v.add(r4);

                tab.setMessReihen( v );

                tab.writeToFile( f2 );

    }
}
