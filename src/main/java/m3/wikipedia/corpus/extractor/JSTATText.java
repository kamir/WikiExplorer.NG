/*
 * 
 * A collection of Text Analysis Tools.
 * 
 */
package m3.wikipedia.corpus.extractor;

import org.apache.hadoopts.chart.simple.MultiBarChart;
import org.apache.hadoopts.data.series.TimeSeriesObject;
import m3.io.CorpusFile2;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import analysis.wikipagecorpus.CorpusAnalyser;
import m3.jstat.data.Corpus;
import m3.terms.TermCollectionTools;


/**
 *
 * @author root
 */
public class JSTATText {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, URISyntaxException {

        String[] pages = { "Berlin" };

        String referenz = pages[0];
        
        String wiki = "de";
        
        Vector<TimeSeriesObject> mrs1 = new Vector<TimeSeriesObject>();
        Vector<TimeSeriesObject> mrsTermDist = new Vector<TimeSeriesObject>();
        
        for( String page : pages ) {
            // wikipedia.explorer.ExtractCategorieCorpus.extractCorpus(wiki, page);

            String file = "cat_corpus_de_" + page + ".dat.corpus.seq";
            Corpus c = CorpusFile2.loadFromLocalFS("/home/kamir/bin/WikiExplorer/WikiExplorer/" + file );

//            TimeSeriesObject mr1 = CorpusAnalyser.analyseCharacterDistribution(c, page);
//            mrs1.add(mr1);

            TimeSeriesObject mr2 = null;
            try {
                mr2 = CorpusAnalyser.analyseTermDistribution(c, page);
            } catch (Exception ex) {
                ex.printStackTrace();
//                Logger.getLogger(JSTAT.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if ( mr2 != null ) mrsTermDist.add(mr2);
        }   
        
        TermCollectionTools.initGlobalOrder( mrsTermDist , referenz );
        Vector<TimeSeriesObject> mrsTermDistTV = createGlobalOrder( mrsTermDist );
        
//        MultiBarChart.open(mrs1, "Character Count" , "symbol" , "log( anz )", true );
        MultiBarChart.open(mrsTermDistTV, "Term Count" , "symbol" , "log( anz )", true );
        
        
    }

    private static Vector<TimeSeriesObject> createGlobalOrder(Vector<TimeSeriesObject> mrsTermDist) {

        Vector<TimeSeriesObject> mrsTermDistT = new Vector<TimeSeriesObject>();
        // determine all terms of all rows
        HashSet<String> terms = new HashSet<String>();
        for( TimeSeriesObject mr : mrsTermDist ) { 
            for( String a : mr.xLabels2 ) {
                if ( !terms.contains(a) ) {
                    terms.add( a );
                }    
            };
            System.out.println( "Nr of terms : [" + mr.getLabel() + "] " + terms.size() );
        }
        
        for( TimeSeriesObject mr : mrsTermDist ) { 
            int sVor = mr.getXValues().size();
            for( String term : terms ) {
                if ( !mr.xLabels2.contains(term) ) {
                    mr.addValue(0, term);
                }    
            };
            System.out.println( "expandet : " + mr.getLabel() + " from: " + sVor + " => " + mr.xValues.size() );
            TimeSeriesObject r = TermCollectionTools.getTermVector(mr);
            mrsTermDistT.add(r.getYLogData());
        }
        
        return mrsTermDistT;
    }
}
