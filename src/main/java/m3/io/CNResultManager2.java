/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package m3.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.Hashtable; 
import javax.security.auth.login.FailedLoginException;

import m3.wikipedia.corpus.extractor.WikiStudieMetaData; 
import m3.wikipedia.corpus.extractor.edits.WikiHistoryExtraction2;
import m3.wikipedia.corpus.extractor.edits.WikiHistoryExtractionBASE;
import m3.wikipedia.explorer.data.WikiNode;

/**
 *
 * @author kamir
 */
public class CNResultManager2 {
    
    
    static CNResultManager2 rm = null;
    
    static GregorianCalendar bis = new GregorianCalendar();
    static GregorianCalendar von = new GregorianCalendar();
    
    public static void main( String[] args ) throws FileNotFoundException, IOException, ClassNotFoundException, FailedLoginException, Exception { 
        
        von = new GregorianCalendar();
        von.clear();
        von.set(2008, 0, 1, 0, 0);

        bis = new GregorianCalendar();
        bis.clear();
        bis.set(2009, 0, 1, 0, 0);
        
        m3.tscache.TSCache.von = von;
        m3.tscache.TSCache.bis = bis;
        
        
        WikiHistoryExtractionBASE.setVon(von);
        WikiHistoryExtractionBASE.setVon(bis);
        
        WikiStudieMetaData wd = WikiStudieMetaData.initStudie();
        
        File ff = wd.selectFile( "/Volumes/MyExternalDrive/CALCULATIONS/");
        wd = wd.load(ff);
        
        File f = wd.getRealPathToProjectFile(ff);
        
        rm = new CNResultManager2();
        rm.init( wd, f );
        rm.printResults();
        
    }

    private static void initResultContainer(WikiStudieMetaData wd) {
        System.out.println( "> STUDIE  : " + wd.name );
        
        int i = 0;
        for( WikiNode wn : wd.getCN() ) { 
            String key = wn.getKey_TIME_DEPENDENT( von, bis );
            data.put( key , new CNStatResultsRecord( i, wd ) );
            keyMap.put( i, key);
            System.out.println( i + " > key : " + key );
            i++;
        }
    }
    
    // key = nr of CN 
    
    static Hashtable<String,CNStatResultsRecord> data = new Hashtable<String,CNStatResultsRecord>();
    static WikiStudieMetaData wd = null;
    
    static Hashtable<Integer,String> keyMap = new Hashtable<Integer,String>();
    
    public void init( WikiStudieMetaData _wd, File f ) throws IOException, FileNotFoundException, ClassNotFoundException, FailedLoginException, Exception { 
        wd = _wd;
        
        initResultContainer(wd);
        
    // erzeuge die Text-Statistik
//         WikipediaCorpusLoaderTool._createNewTextStatisticFile(wd, rm);
        
    // erzeuge die ACCESS-Statistik
        // Prozedur für einzelnes File aus dem Cluster ...
        boolean multi = false;
        String fn = wd.getTSExtractionLocationFileName();
        SequenceFileExplorer3 sep = new SequenceFileExplorer3();
        sep.setLocalNet( wd.net );
        sep.loadData( fn, wd.getLISTFILE() );
//        
//        
//        sep.doTextAnalysis();
//
        
    // erzeuge die EDITS-Statistik
        WikiHistoryExtraction2.processStudie(wd, von, bis, f);
    
    };

    public void setResult(String t, double d) {
        System.out.println("KEY="+t+ " " + d);
        int i = t.indexOf(".");
        int n = Integer.parseInt( t.substring(0, i) );
        
        System.out.println("KEY=" + n + " -> " + keyMap.get(n) );
        
        CNStatResultsRecord rec = data.get( keyMap.get(n) );
        rec.setResult( t, d );
        
    }

    public void printResults() {
        for( String keys : data.keySet() ) { 
            CNStatResultsRecord rec = data.get(keys);
            rec.print();
        }
    }

   
    
}
