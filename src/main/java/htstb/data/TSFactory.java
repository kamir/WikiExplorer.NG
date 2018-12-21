package htstb.data;

import htstb.data.TimeSeriesData;
import org.apache.hadoopts.data.series.TimeSeriesObject;
import java.util.Date;
import org.apache.hadoop.io.Text;

/**
 * This is the logic around the TimeSeriesData Object
 * to map data from TimeSeriesObject to  an hadoopable DataObject.
 * @author kamir
 */
public class TSFactory {

    
    public static TimeSeriesData map( TimeSeriesObject mr, Date t0, int dt ) { 
        TimeSeriesData tsd = new TimeSeriesData();
        
        tsd.label = mr.getLabel();
        tsd.dt = dt;
        tsd.t0 = t0;
        tsd.v = mr.getYData();
        tsd.size = tsd.v.length;
        
        return tsd;
    }
    
    public static String sep = " ";
    
    static public Text[] getKV( TimeSeriesData tsd ) {
        Text k = new Text( tsd.label );
        
        StringBuilder sb = new StringBuilder();
        // erster Wert ist die Größe
        sb.append( tsd.size );
        // dann mit SPACE getrennt die Werte
        for( int i = 0; i < tsd.size; i++ ) { 
            sb.append( sep + tsd.v[i] );
        } 
        
        Text v = new Text( sb.toString() );
        Text[] kv = new Text[2];
        kv[0] = k;
        kv[1] = v;
        return kv;
    }
    
    static public Text[] getKV( TimeSeriesData tsda, TimeSeriesData tsdb ) {
        Text k = new Text( tsda.label + "###" + tsdb.label );
        
        StringBuilder sb = new StringBuilder();
  
        // erster Wert ist die Größe
        sb.append( tsda.size );
        // dann mit SPACE getrennt die Werte
        for( int i = 0; i < tsda.size; i++ ) { 
            sb.append( sep + tsda.v[i] );
        } 
        
        // erster Wert ist die Größe
        sb.append( sep + tsdb.size );
        // dann mit SPACE getrennt die Werte
        for( int i = 0; i < tsdb.size; i++ ) { 
            sb.append( sep + tsdb.v[i] );
        } 
        
        Text v = new Text( sb.toString() );
        Text[] kv = new Text[2];
        kv[0] = k;
        kv[1] = v;
        return kv;
    }

    
}
