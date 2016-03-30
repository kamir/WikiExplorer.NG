/**
 * 1.) Create a SequenceFile with random TimeSeries data.
 * 
 * 2.) Store the SequenceFile into HDFS.
 *
 */
package htstb.util.generator;

import org.apache.hadoopts.data.series.Messreihe;
import extraction.TimeSeriesFactory;
import htstb.data.TSFactory;
import htstb.data.TimeSeriesData;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import com.cloudera.wikiexplorer.ng.util.nodegroups.RandomNodesGroup;

/**
 * @author Mirko Kämpf
 *         
 * bitOcean
 * 
 */
public class RandomTimeSeriesGenerator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        // define a new NodeGroup
        String name = "randomSetA";
        
        // init random generator
        stdlib.StdRandom.initRandomGen(0);
        
        // special properties are defined in a NodeGroup class
        RandomNodesGroup ng = new RandomNodesGroup( 10000 );
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
        
        Enumeration<Messreihe> en = ng.accessReihen.elements();

        GregorianCalendar cal = new GregorianCalendar();
        cal.set(2009, 00, 01,0,0,0);

        int h = 1000 * 60 * 60;
        while ( en.hasMoreElements() ) {
            Messreihe mr = en.nextElement();
            TimeSeriesData tsd = TSFactory.map(mr, cal.getTime(), h);
                    
            Text[] kv = TSFactory.getKV( tsd );
            
            writer.append(kv[0], kv[1] );
        }

        writer.close();

        System.out.println( "DONE." );
        
    }
}
