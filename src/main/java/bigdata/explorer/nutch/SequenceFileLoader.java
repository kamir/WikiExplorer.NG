/* */
package bigdata.explorer.nutch;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.nutch.scoring.webgraph.LinkDatum;

/**
 * 
 * @author root
 */
public class SequenceFileLoader {
         
    SequenceFile.Reader reader = null;
    
    Path path = null;
    FileSystem fs = null;
    Configuration config = null;
    boolean useHDFS = true;

    SequenceFileLoader(File f1) throws IOException {
        String fn = f1.getAbsolutePath();
        initFileSystem(fn);
    }

    public void initFileSystem( String fn ) throws IOException {
        config = new Configuration();

        System.out.println("<HDFS>" + fn + "</HDFS>" );

        config.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
        config.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));

        fs = FileSystem.get(config);

        path = new Path(fn);
    }
    
 
    public void getMetaData() throws IOException {
 
        reader = new SequenceFile.Reader(fs, path, config);
        
        System.out.println("--> process bucket        : " + path.toString() );
        System.out.println("--> compression-codes     : " + reader.getCompressionCodec());
        System.out.println("--> key-classename        : " + reader.getKeyClassName());
        System.out.println("--> value-classname       : " + reader.getValueClassName());

    }

    
    public Vector<org.apache.nutch.scoring.webgraph.Node> loadNodes(int j, String part) throws IOException {

        Vector<org.apache.nutch.scoring.webgraph.Node> nodes = new Vector<org.apache.nutch.scoring.webgraph.Node>();

        int LIMIT = j;
        boolean goOn = true;
        int i = 0;
        int error = 0;
    
        while (goOn && i <= LIMIT) {

            Text key = new Text();
            org.apache.nutch.scoring.webgraph.Node val = new org.apache.nutch.scoring.webgraph.Node();

            goOn = reader.next(key);

            reader.getCurrentValue(val);
            
            val.getMetadata().add("name", key.toString() );
            
            nodes.add(val);
            
            System.out.println( i + " : " + val.getMetadata().get("name") );
            i++;

        }
        System.out.println("--> nr of records     : " + (i - 1));
        // System.out.println("--> nodes            : " + nodes.toString());

        return nodes;
    }
    
    public void listContent(int j, String part) throws IOException {

        Vector<org.apache.nutch.scoring.webgraph.Node> groups = new Vector<org.apache.nutch.scoring.webgraph.Node>();

        int LIMIT = j;
        boolean goOn = true;
        int i = 0;
        int error = 0;
    
        while (goOn && i <= LIMIT) {

            Text key = new Text();
            Text val = new Text();

            goOn = reader.next(key);

            reader.getCurrentValue(val);

        }
        System.out.println("--> nr of records     : " + (i - 1));
        System.out.println("--> groups            : " + groups.toString());

    }

    public Vector<MyLinkDatum> loadInLinks(int j, String part00000) throws IOException {
                
        Vector<MyLinkDatum> groups = new Vector<MyLinkDatum>();

        int LIMIT = j;
        boolean goOn = true;
        int i = 0;
        int error = 0;
    
        while (goOn && i <= LIMIT) {

            Text key = new Text();
            LinkDatum val = new LinkDatum();

            goOn = reader.next(key);

            reader.getCurrentValue(val);
            
            MyLinkDatum dat = new MyLinkDatum();
            dat.setLink(key, val);
            
            groups.add(dat);
            i++;

        }
        System.out.println("--> nr of records     : " + (i - 1));
        System.out.println("--> groups            : " + groups.toString());
        
        return groups;
    }
    
    public Vector<MyLinkDatum> loadOutLinks(int j, String part00000) throws IOException {
                
        Vector<MyLinkDatum> groups = new Vector<MyLinkDatum>();

        int LIMIT = j;
        boolean goOn = true;
        int i = 0;
        int error = 0;
    
        while (goOn && i <= LIMIT) {

            Text key = new Text();
            LinkDatum val = new LinkDatum();

            goOn = reader.next(key);

            reader.getCurrentValue(val);
                  
            MyLinkDatum dat = new MyLinkDatum();
            dat.setLink(key, val);
            
            groups.add(dat);
            
            i++;

        }
        System.out.println("--> nr of records     : " + (i - 1));
        System.out.println("--> groups            : " + groups.toString());
        
        return groups;
    }
    
}
