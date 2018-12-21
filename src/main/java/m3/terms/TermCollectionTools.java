/*
 * 
 * http://www.javapractices.com/topic/TopicAction.do?Id=207
 * 
 */
package m3.terms; 
import org.apache.hadoopts.data.series.TimeSeriesObject;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author root
 */
public class TermCollectionTools {
    
    static Map<String,Double> globalMap = null; 
    
    static public void initGlobalOrder( TimeSeriesObject mr ) { 
        System.out.println( ">> order by : " + mr.label );
        globalMap = TermComparator.getMapSortedByValue( mr );
        System.out.println( globalMap );
    };
    
    public static TimeSeriesObject getTermVector( TimeSeriesObject mr ) { 
        TimeSeriesObject mr2 = new TimeSeriesObject();
        
        mr2.setLabel( mr.getLabel() + "_TV");
        
        Iterator it = globalMap.keySet().iterator();
        while( it.hasNext() ) { 
            String k = (String)it.next();
            Double v = (Double)mr.hashedValues.get(k);
            if ( v == null ) v = new Double(0);
            mr2.addValue(v);
            // System.out.println( mr2.getLabel() + " : " + k + " " + v );
        }
        
        return mr2;
    }

    public static void initGlobalOrder(Vector<TimeSeriesObject> mrsTermDist, String referenz) {
        TimeSeriesObject ref = null;
        for( TimeSeriesObject mr : mrsTermDist ) { 
            if ( mr.getLabel().startsWith( referenz ) && ref == null ) { 
                ref = mr;
            };
        }
        initGlobalOrder(ref);
    }
    
}
