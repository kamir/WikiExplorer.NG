package htstb.data;

import java.util.Date;
import org.apache.hadoop.io.Text;

/**
 * An hadoopable DataContainer.
 * 
 * Will implement Writeable soon...
 * 
 * @author kamir
 */
public class TimeSeriesData {
    
    String label = "";

    public int dt = 1000 * 60 * 60 * 24;  // day
    
    public Date t0 = null;
    public int size;
    public double[] v;
    
}
