/**
 * 1.) Create a SequenceFile with random TimeSeries data.
 * 
 * 2.) Store the SequenceFile into HDFS.
 *
 */
package htstb.util.generator;

import org.apache.hadoopts.data.series.TimeSeriesObject;
import htstb.data.TSFactory;
import htstb.data.TimeSeriesData;

import java.io.IOException;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import research.wikinetworks.NodePair;
import com.cloudera.wikiexplorer.ng.util.nodegroups.RandomNodesGroup;

/**
 * @author Mirko Kämpf
 *         
 * bitOcean
 * 
 */
public class RandomTimeSeriesPairGenerator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        // define a new NodeGroup
        int anzahl = 10;
        
        RandomNodesGroup.length = 24*299;
        
        String name = "randomSet_B_pairs_"+anzahl;
        // init random generator
        stdlib.StdRandom.initRandomGen(0);
        
        // special properties are defined in a NodeGroup class
        RandomNodesGroup ng = new RandomNodesGroup( anzahl );
        ng.checkAccessTimeSeries();
        
        // we do not use edit series right now ...
        // ng.checkEditTimeSeries();

        // what pairs should be used for correlation analysis?
        ng._initWhatToUse();
        
        // create the Sequencefile
        Configuration conf = new Configuration();
        conf.addResource(new Path("conf/core-site.xml"));
        conf.addResource(new Path("conf/hdfs-site.xml"));  
        
        FileSystem fs = FileSystem.get(conf);

        Path outputPath = new Path("/user/kamir/" + name + ".seq");
        
        // we overwrite existing files of the same name 
        if ( fs.exists(outputPath) ) {
            fs.delete(outputPath, true);
            System.out.println( "DELETE existing file." );
        }

        Text key = new Text(); // Example, this can be another type of class
        Text value = new Text(); // Example, this can be another type of class

        SequenceFile.Writer writer = SequenceFile.createWriter(fs, conf, outputPath, key.getClass(), value.getClass());
        
        Enumeration<TimeSeriesObject> en = ng.accessReihen.elements();

        GregorianCalendar cal = new GregorianCalendar();
        cal.set(2009, 00, 01,0,0,0);

        int h = 1000 * 60 * 60;
        
        int max = anzahl;
        int z = 0;
        for ( int i=0; i < max ; i++ ) {
            for ( int j=0; j < max ; j++ ) {
                if (!(i==j)){
                    NodePair np = new NodePair();
                    np.id_A = i;
                    np.id_B = j;
                    np.pageIDA = i;
                    np.pageIDB = j;
                            
                    boolean v = false;
                    if ( ng.doWorkWithPair(np) ) {        
                            
                        TimeSeriesObject mra = ng.accessReihen.elementAt(i);
                        TimeSeriesObject mrb = ng.accessReihen.elementAt(j);
                        
                        TimeSeriesData tsda = TSFactory.map(mra, cal.getTime(), h);
                        TimeSeriesData tsdb = TSFactory.map(mrb, cal.getTime(), h);

                        Text[] kv = TSFactory.getKV( tsda, tsdb );

                        writer.append( kv[0] , kv[1] );

                        if ( i==0 && j==1 ) {
                            System.out.println( "key: " + kv[0] );
                            System.out.println( "val: " + kv[1] );
                        }
                        System.out.println( z );
                        z++;
                        
                    }
                }
            }
        }
        
        

        writer.close();

        System.out.println( "DONE." );
        
    }
}
