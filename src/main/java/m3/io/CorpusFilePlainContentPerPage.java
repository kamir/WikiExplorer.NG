/**
 * 
 * A corpus file contains documents to a special analysis context.
 *
 * 
 */
package m3.io;


import com.thoughtworks.xstream.XStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import javax.swing.JFrame;
import m3.jstat.data.Corpus;
import m3.jstat.data.Document;
import m3.jstat.data.WikiDocumentKey;
import org.crunchts.store.TSB;

/**
 *
 * @author root
 */
public class CorpusFilePlainContentPerPage {
    
    static int LIMIT = Integer.MAX_VALUE;
    
    /**
     * Im sourceFolder wird eine komplette Gruppe gewählt
     * und in einen TS Bucket überführt.
     * 
     * ==> ist nur eine SAVE Funktion ... 
     *
     * @param groupFolder
     */
    public static void _createCorpusFile(String outPath, String name, Corpus corpus, String studie) throws IOException, URISyntaxException {
      
        File aspectFolder = TSB.getAspectFolder("corpus.plain-text", studie );

        File path = new File( aspectFolder.getAbsolutePath() + "/" + name + ".corpus.plain-text" );

        path.mkdirs();
        
        System.out.println("###=====> create plain text corpus : " + path.toString() );
  
        // javax.swing.JOptionPane.showMessageDialog(new JFrame(), corpus.docs.size() + " documents have to be stored.");
  
        try {
            for( Document doc : corpus.docs ) {

                
                
                WikiDocumentKey key = new WikiDocumentKey(doc);

                String fn = key.toStringFilename();

                System.out.println( " ***>>> " + fn );

                // write a XML String of the Corpus
                FileWriter os = new FileWriter( path + "/" + fn );
                BufferedWriter bw = new BufferedWriter( os );
                bw.write( doc.html );
                bw.flush();
                bw.close();
            }
        }
        catch( Exception ex) {
            ex.printStackTrace();
        }
        
    }

    public static Corpus loadFromLocalFS( String fn ) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, URISyntaxException {

        HashSet groups = new HashSet();

        FileInputStream os = new FileInputStream( fn );
        
        System.out.println("--> load XML-file with Corpus data ...   : " + fn );
        
        XStream xstream = new XStream();
        Object o = xstream.fromXML(os); 
        Corpus c = (Corpus)o;

        System.out.println("*** DONE *** " );

        int i = 0;
        for( Document doc : c.docs ) {
            groups.add( doc.group );
            i++;
        }
        
        System.out.println("--> nr of records     : " + i );
        System.out.println("--> nr of groups      : " + groups.size() );
        System.out.println("--> " + groups.toString() );

        return c;
    }

 
    
}
