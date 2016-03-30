/*
 * The core datatype for textanalysis.
 * 
 * TODO: Write it via AFRO to interact with python later on.
 * 
 */
package m3.jstat.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Properties;
import org.apache.hadoop.io.Writable;
import m3.wikipedia.explorer.data.WikiNode;

/**
 *
 * @author root
 */
public class Document implements Writable {

    public WikiNode wn;

    public String url = null;
    public String group = "0";
    public String html = null;
    
    public Document( String url, String html ) { 
        this.url = url;
        this.html = html;
    }
    
    public Document() {}

    public void write(DataOutput d) throws IOException {
        d.writeUTF( group );
        d.writeUTF( wn.wiki );
        d.writeUTF( wn.page );
        d.writeInt( wn.pageVolume );
        d.writeUTF( html );
    }

    public void readFields(DataInput di) throws IOException {
        group = di.readUTF();
        String w = di.readUTF();
        String p = di.readUTF();
        Integer vol = di.readInt();
        wn = new WikiNode(w, p);
        wn.pageVolume = vol;
        html = di.readUTF();
    }
    
}
