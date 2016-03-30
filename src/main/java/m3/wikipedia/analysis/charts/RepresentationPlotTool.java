/***
 *
 *
 ***/
package m3.wikipedia.analysis.charts;

import m3.io.CNResultManager2;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import m3.jstat.data.Corpus;
import org.apache.velocity.runtime.parser.ParseException;
import wikiapiclient.WikiORIGINAL;
import wikiapiclient.WikiORIGINAL.Revision;
import m3.wikipedia.corpus.extractor.CorpusStatisticsMain2;
import m3.wikipedia.corpus.extractor.WikiStudieMetaData;
import com.cloudera.wikiexplorer.ng.app.WikipediaCorpusLoaderTool;
import m3.wikipedia.explorer.data.WikiNode;

/**
 *
 * @author kamir
 */
public class RepresentationPlotTool {
    
    
    public static void createTextStatisticFile(WikiStudieMetaData sdm, CNResultManager2 rm) throws ParseException {

        int errors = 0;
        String errorstatus="successfully";

        System.out.println(">>> Calculate text based measures for corpus ["+ sdm.name +"]... \n> nr of CN= " + sdm.getWn().size());

        // der rm wird hier genutz, um später mal die Kenndaten alle zusammen zu tragen

        String p[] = new String[sdm.getWn().size()];
        String w[] = new String[sdm.getWn().size()];
        
        String ext[] = new String[sdm.getWn().size()];

        int i = 0;
        for (WikiNode wn : sdm.getWn()) {
        
            WikiORIGINAL wiki1 = new WikiORIGINAL(wn.wiki + ".wikipedia.org");
            ext[i] = "?";
            
            System.out.print("\n[PAGE]:" + wn.page + "\n");
            Object o;
            try {

                o = wiki1.getPageInfo("exists");
                System.out.println("[PageInfo#exixt]:"+o);
                
                // node degree
                int kout = wiki1.getLinksOnPage(wn.page).length;

                // first revision of the node
                Revision first = wiki1.getFirstRevision(wn.page);
                
                ext[i] = kout + "\t" + first.getTimestamp().getTime().toString();
                //ext[i] = "-ext-";

                System.out.println( "[Page MD]:" + ext[i] );
                
            } 
            catch (IOException ex) {
                Logger.getLogger(WikipediaCorpusLoaderTool.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println(ex.getCause());
                errors++;
            }
            p[i] = wn.page;
            w[i] = wn.wiki;
            i++;
        }

        try {

            CorpusStatisticsMain2.createTextStatisticsResultFile(sdm.getName(), w, p, Corpus.mode_XML, ext, rm);

        } catch (IOException ex) {
            errors++;
            Logger.getLogger(WikipediaCorpusLoaderTool.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            errors++;
            Logger.getLogger(WikipediaCorpusLoaderTool.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            errors++;
            Logger.getLogger(WikipediaCorpusLoaderTool.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            errors++;
            Logger.getLogger(WikipediaCorpusLoaderTool.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            errors++;
            Logger.getLogger(WikipediaCorpusLoaderTool.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if ( errors != 0 ) errorstatus = "!!!with errors!!!";
        
        System.out.println(">>> " + errors + " errors detected.");
        System.out.println(">>> TextStatisticFile created. " + errorstatus);

    }
    
}
