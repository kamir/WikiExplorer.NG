package m3.io;

import org.apache.hadoopts.data.series.TimeSeriesObject;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import m3.wikipedia.corpus.extractor.edits.WikiHistoryExtraction2;
import m3.wikipedia.corpus.extractor.edits.WikiHistoryExtractionBASE;
import m3.wikipedia.explorer.data.WikiNode;

public class WikiNodeCacheEntry {

    public String key;
    public TimeSeriesObject mr;

    public void _store(ObjectOutputStream store) throws IOException {
        store.writeUTF(key);
        store.writeUTF(mr.getLabel());
        store.writeUTF(mr.getIdentifier());
        store.writeObject(mr.getLabel_X());
        store.writeObject(mr.getLabel_Y());
        store.writeObject(mr.xValues);
        store.writeObject(mr.yValues);
    }

    public int load(ObjectInputStream store, Hashtable<String, TimeSeriesObject> c, Calendar von, Calendar bis) throws IOException, ClassNotFoundException {

        int i = 0;

        boolean goOn = true;
        while ( goOn ) {

            goOn = loadMore(store, c, von, bis);

            i = i + 1;
        }

        return i;
    }
    
    static boolean debug = false;
    public boolean loadMore(ObjectInputStream store, Hashtable<String, TimeSeriesObject> c, Calendar von, Calendar bis) throws IOException, ClassNotFoundException {
        boolean v;
        try {
        
            String key = (String) store.readUTF();
            String label = (String) store.readUTF();
            String id = (String) store.readUTF();
            String labelx = (String) store.readObject();
            String labely = (String) store.readObject();
            Object x = store.readObject();
            Object y = store.readObject();

            TimeSeriesObject mr = new TimeSeriesObject();
            mr.setLabel(label);
            mr.setIdentifier(id);
            mr.setLabel_X(labelx);
            mr.setLabel_Y(labely);
            mr.xValues = (Vector)x;
            mr.yValues = (Vector)y;
            
            mr.setGroupKey( WikiHistoryExtractionBASE.CURRENT_Chunk + "." );

            if ( debug) System.out.println( "I:" + mr.getIdentifier() );
            
            String k = m3.tscache.TSCache.getKey(mr, von , bis );

            c.put(k, mr);
            
            if ( debug) System.out.println( c.keySet().size() );
            
            v = true;
            
        } 
        catch (EOFException ex) {
            
            v = false;
            
            System.out.println( "EOF" );            

        }
        catch (IOException ex) {
            Logger.getLogger(WikiNodeCacheEntry.class.getName()).log(Level.SEVERE, null, ex);
            v = false;
            

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(WikiNodeCacheEntry.class.getName()).log(Level.SEVERE, null, ex);
            v = false; 

        }

        return v;
    }


}
