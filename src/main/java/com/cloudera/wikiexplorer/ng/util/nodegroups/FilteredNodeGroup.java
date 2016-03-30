package com.cloudera.wikiexplorer.ng.util.nodegroups;

import org.apache.hadoopts.data.series.Messreihe;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;

import org.apache.hadoopts.chart.statistic.HistogramChart;
import org.apache.hadoopts.data.series.Messreihe;
import org.apache.hadoopts.data.series.ValuePair;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.ui.RefineryUtilities;
import research.nodeactivity.ExtractNodeActivity;
import com.cloudera.wikiexplorer.ng.util.nodegroups.VphMean32bit;

/**
 * Filtert die Reihen nach EDITS.
 * 
 * @author kamir
 */
public class FilteredNodeGroup extends NodeGroup {

    static boolean debug = false;

    // die N gössten Werte in Y sollen herausgelesen werden ....

    public static Messreihe filterByYValue_max_N(Messreihe _mr, int threshold) {

        if ( threshold > _mr.xValues.size() ) {
            System.out.println( "Messreihe zu kurz ... " );
            return _mr;
        }
        
        Vector<ValuePair> y2 = _mr.getValuePairs();

        Collections.sort(y2);
        // Collections.reverse(y2);

        Messreihe mr = new Messreihe();
        mr.setLabel( _mr.getLabel() + "most active( "+threshold+")" );

        int i = 0;
        Iterator it1 = y2.iterator();
        while( i < threshold ) {
            ValuePair vp = (ValuePair) it1.next();
            // System.out.println( vp );
            mr.addValuePair( vp );
            i++;
        }

        return mr;
    }

    // erwartet in x Spalte die ID und in y die Zahl der Edits ...
    public FilteredNodeGroup( String _fn, Messreihe mr ) {
        super();
        fn = _fn;
        int[] tIDS = new int[ mr.xValues.size() ];

        for ( int c = 0 ; c < mr.xValues.size(); c++ ) {
            double d = (Double)mr.xValues.elementAt(c);
            tIDS[c] = (int)d;
        };

        this.ids = tIDS;
    };



   


}
