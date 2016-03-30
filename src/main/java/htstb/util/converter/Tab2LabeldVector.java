/**
 * Create a SequenceFile for a CLUSTER analysis in MAHOUT.
 * 
 * Based on a table from HDFS (result of a join) a SequenceFile is
 * generated which contains 
 * 
 * key:    Text
 * value:  VectorWritable
 */
package htstb.util.converter;

import org.apache.hadoopts.data.series.Messreihe;
import java.io.File;
import java.io.*;
import java.util.*;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.VectorWritable;

/**
 *
 * @author kamir
 */
public class Tab2LabeldVector {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        // create the Sequencefile
        Configuration conf = new Configuration();
        conf.addResource(new Path("conf/core-site.xml"));
        conf.addResource(new Path("conf/hdfs-site.xml"));

        FileSystem fs = FileSystem.get(conf);

        String name = "000000_0";
        Path in = new Path("/user/beeswax/warehouse/pn6/" + name);

        
        Path path = new Path("/user/kamir/tab6/tab6.seq");

        //write a SequenceFile form a Vector
        SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, path, Text.class, VectorWritable.class);
                


        boolean goOn = true;
        int z = 0;
        if (fs.exists(in) ) {
            System.out.println("Fine ... ");
            BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(in)));

            while (br.ready()  && goOn ) {
                String line = br.readLine();

                // System.out.println( "".getBytes("UTF-8").toString() );
                StringTokenizer st = new StringTokenizer(line, "");

                int langID = decodeToken(st.nextToken());
                int pageID = decodeToken(st.nextToken());
                int nrEDITS = decodeToken(st.nextToken());
                int kinr = decodeToken(st.nextToken());
                int koutr = decodeToken(st.nextToken());
                int kinl = decodeToken(st.nextToken());
                int koutl = decodeToken(st.nextToken());

                System.out.println(z + " : " + langID + " " + pageID + " " + nrEDITS + " " + kinr + " " + koutr + " " + kinl + " " + koutl);

                NamedVector v1 = new NamedVector(new DenseVector(new double[]{kinr,koutr,kinl,koutl,nrEDITS}), langID+"_"+pageID);
                VectorWritable vec = new VectorWritable();
                vec.set(v1);
                writer.append(new Text(v1.getName()), vec);

                z = z + 1;
//                if ( z == 10000 ) { 
//                    goOn = false;
//                }
            }
            writer.close();

        }
    }

    public static int decodeToken(String t) {
        int i = 0;
        try {
            i = Integer.parseInt(t);
        } catch (Exception ex) {
            i = 0;
        }
        return i;

    }
}
