package com.cloudera.wikiexplorer.ng.util.reports;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import com.cloudera.wikiexplorer.ng.util.LaunchBrowser;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;


/**
 *
 * @author kamir
 */
public class CCAnalyseReport {

    File f = null;
    
    public void createReport( Hashtable<String, String> data , File fout ) throws IOException, Exception {
        f = fout;
       /*  first, get and initialize an engine  */
        VelocityEngine ve = new VelocityEngine();
        ve.init();

        /*  next, get the Template  */
        Template t = ve.getTemplate( "singleLanguageReport_v1.vm" );

        /*  create a context and add data */
        VelocityContext context = new VelocityContext();

        for( String key : data.keySet() ) {
            context.put(key, data.get(key) );
        }
        
        /* now render the template into a StringWriter */
        StringWriter writer = new StringWriter();
        t.merge( context, writer );

        /* show the World */
        //System.out.println( writer.toString() );

        FileWriter fw = new FileWriter( fout );
        BufferedWriter bw = new BufferedWriter( fw );
        t.merge( context, bw );
        bw.flush();
        bw.close();

    }

    public void showInBrwoser() throws IOException {
        LaunchBrowser.openReport( f.getAbsolutePath() );
    }

    public String[] addFileNamesToArray(File f , String[] alt ) {
        String[] f1 = f.list();

        String[] a = new String[ alt.length + f1.length];
        int i = 0;
        for( String s : f1 ) {
            a[i] = f.getAbsolutePath() + "/" + f1[i];
            i++;
        }
        int j = 0;
        for( String s : alt ) {
            a[j] = alt[j];
            j++;
        }
        return a;
    }

    public void packAsZIP() {
       // These are the files to include in the ZIP file

        File f1 = new File( NodeGroup.pfad + "/img" );
        File f2 = new File( NodeGroup.pfad + "/cc_edits" );
        File f3 = new File( NodeGroup.pfad + "/cc_access" );

        String[] filenames = new String[0];

        filenames = addFileNamesToArray(f1, filenames );
        filenames = addFileNamesToArray(f1, filenames );
        filenames = addFileNamesToArray(f1, filenames );


        // Create a buffer for reading the files
        byte[] buf = new byte[1024];

        try {
            // Create the ZIP file
            String outFilename = NodeGroup.pfad + "/results/" + f.getName() + ".zip";

            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));

            // Compress the files
            for (int i=0; i<filenames.length; i++) {
                System.out.println( filenames[i] );
                FileInputStream in = new FileInputStream(filenames[i]);

                // Add ZIP entry to output stream.
                out.putNextEntry(new ZipEntry(filenames[i]));

                // Transfer bytes from the file to the ZIP file
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                // Complete the entry
                out.closeEntry();
                in.close();

                // System.out.println("delete now: " + filenames[i] );
            }

            // Complete the ZIP file
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
