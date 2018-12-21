/*
 * Copyright 2016 kamir.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package analysis.wikipagecorpus;

import hadoop.cluster.connector.SimpleClusterConnector;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import m3.io.CNResultManager2;
import m3.jstat.data.Corpus;
import m3.wikipedia.corpus.extractor.FileNameFilter;
import m3.wikipedia.corpus.extractor.category.ExtractCategorieCorpus;
import m3.wikipedia.explorer.data.WikiNode;
import org.crunchts.store.TSB;

import org.etosha.core.sc.connector.external.Wiki;

import org.openide.util.Exceptions;

/**
 *
 * @author kamir
 */
public class PageCorpusAnalyser {
    
    /**
     * DOMAIN-FUNCTION:Analysis
     * 
     * Returns the points for the Representation-Plot.
     * 
     * Side-effect:
     * ------------
     * 
     * The static variable listFile contains the path of the 
     * created resultfile.
     *
     * @param w
     * @param page
     * @param studie
     * @param i       index of the element in the list.
     * 
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static String runPerPageCorpusTextAnalysis(String w, String page, String studie, FileWriter fw, int FILEMODE, String ext, CNResultManager2 rm, int i, FileWriter fwChartBuble) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        // ext war hier der erste Edit der Datei

        Vector<WikiNode> ACN = new Vector<WikiNode>();
        Vector<WikiNode> AL = new Vector<WikiNode>();

        Vector<WikiNode> BIWL = new Vector<WikiNode>();
        Vector<WikiNode> BL = new Vector<WikiNode>();

        // wikipedia.explorer.ExtractCategorieCorpus.extractCorpus(wiki, page);

        page = FileNameFilter.cleanWikinameForFilename(page);

        String fileRelMapData = "iwl_corpus_RELMAP_" + studie + "_" + w + "_" + page + ".dat.corpus.JS.data";

        StringBuffer sbRelMapData = new StringBuffer();


        String file = "iwl_corpus_RELMAP_" + studie + "_" + w + "_" + page + ".dat.corpus.seq";

        String file2 = "iwl_corpus_RELMAP_" + studie + "_" + w + "_" + page + ".dat.corpus.seq.ts";

        String listFILE = "listfile_" + studie + "_" + w + "_" + page + ".lst";

        // Corpus2 c = CorpusFile2.loadFromLocalFS("/home/kamir/bin/WikiExplorer/WikiExplorer/" + file);
        Corpus c = null;
        
        String point = "";
        
        try {

            if (FILEMODE == Corpus.mode_XML) {
                file = "iwl_corpus_" + studie + "_" + w + "_" + page + ".dat.corpus.xml";
            }

            String f = Corpus.getCorpusFilePath();
            if ( f == null ) f = TSB.getAspectFolder( "corpus.xml", studie ).getAbsolutePath();
            
            c = Corpus.loadCorpus( f + "/" + file, FILEMODE);

//            if ( export ) { 
//                c.exportCorpusToSequenceFile();
//            }

            ACN = c._getWikiNodes("CN");
            AL = c._getWikiNodes("A.L");

            BIWL = c._getWikiNodes("IWL");
            BL = c._getWikiNodes("B.L");
            
            double colE = BIWL.size();
            double colF = AL.size();
            double colG = BL.size();
            
            double volCN = WikiNode.getSummeVolume(ACN);
            double volIWL = WikiNode.getSummeVolume(BIWL);
            double volBL = WikiNode.getSummeVolume(BL);
            double volAL = WikiNode.getSummeVolume(AL);
            
            double colH = volCN; 
            double colI = volIWL;
            double colJ = volAL;
            double colK = volBL;
            
            double colR = colE + colF;
            
            double colS = colG/colF;
            
            double colT = colR / ( ( colG/colF) + colE );
            
            double colU = colH * colE / colI;
            
            double colW = ( colH / 1.0 ) * ( colK/colG );

            double colX = ( colI / colE ) * ( colJ/colF );
            
            double colV = colW/colX; 

            //  ['A',   80,  167,      120],
            String pageLabel = ACN.elementAt(0).wiki + "." + ACN.elementAt(0).page;
            
            DecimalFormat df1 = new DecimalFormat("0.000");
            
            // create lines for the BubbleChart ...
            
            point = "['" + pageLabel + "'," + 
                           df1.format( colU ).replace(",", ".") + "," + df1.format(colV).replace(",", ".") + ",'" + pageLabel + "'," + df1.format( colT ).replace(",", ".") + "],\n"  ; 

            // DATA
            // ['fr',6.760,3.295,'fr',14.212],
            // ['en',3.029,0.815,'en',11.397],
            // ['it',0.961,1.023,'it',6.362],

            
            fwChartBuble.write( point + "\n" );
 
            sbRelMapData.append("var dataA = google.visualization.arrayToDataTable([ ['Country', 'REP v']" );
            sbRelMapData.append(getRelMapLinesVOL(ACN));
            sbRelMapData.append(getRelMapLinesVOL(BIWL));
            sbRelMapData.append("]);\n\n" );
            
            
            sbRelMapData.append("var dataB = google.visualization.arrayToDataTable([ ['Country', 'REP k']" );
            sbRelMapData.append(getRelMapLinesK(ACN));
            sbRelMapData.append(getRelMapLinesK(BIWL));
            sbRelMapData.append("]);" );
                                  
            
            FileWriter fw2 = new FileWriter( new File(fileRelMapData)  );
            fw2.write( sbRelMapData.toString() );
            fw2.flush();
            fw2.close();

//            rm.setResult( i +".txt.vol.CN" , volCN );
//            rm.setResult( i +".txt.vol.IWL" , volIWL );
//            rm.setResult( i +".txt.vol.B.L" , volBL );
//            rm.setResult( i +".txt.vol.A.L" , volAL );
//            
//            rm.setResult( i +".z.CN" , ACN.size() );
//            rm.setResult( i +".z.IWL" ,BIWL.size() );
//            rm.setResult( i +".z.B.L" , BL.size() );
//            rm.setResult( i +".z.A.L" , AL.size() );

            c.writeWikiNodeKeyFile(ACN.elementAt(0), studie);

            System.out.println("CN   : " + ACN.size());
            System.out.println("IWL  : " + BIWL.size());

            System.out.println("A.L  : " + AL.size());
            System.out.println("B.L  : " + BL.size());

            double r1 = 100 * volCN / volAL;  // CORE
            double r2 = 100 * volIWL / volBL; // HULL
            double r3 = 100 * (volCN + volIWL) / (volAL + volBL); // ALL



//            rm.setResult( i +".txt.r1" , r1 );
//            rm.setResult( i +".txt.r2" , r2 );
//            rm.setResult( i +".txt.r3" , r3 );


            System.out.println("A local ratio   r1: [CORE] = " + r1);

            System.out.println("B global ratio  r2: [HULL] = " + r2);
            System.out.println("B global ratio  r3: [ALL]  = " + r3);


            DecimalFormat df = new DecimalFormat("0.00000");

            if (fw != null) {

                StringBuffer line = new StringBuffer();

                line.append(studie + "\t" + ACN.elementAt(0).wiki + "\t" + ACN.elementAt(0).page + "\t");
                line.append(ACN.size() + "\t");
                line.append(BIWL.size() + "\t");
                line.append(AL.size() + "\t");
                line.append(BL.size() + "\t");
                line.append(df.format(volCN) + "\t");
                line.append(df.format(volIWL) + "\t");
                line.append(df.format(volAL) + "\t");
                line.append(df.format(volBL) + "\t");
                line.append(df.format(r1) + "\t");
                line.append(df.format(r2) + "\t");
                line.append(df.format(r3) + "\t");
                line.append(ext + "\t");


                //                clalc LRI_(k) hier !!! 


                String l = line.toString().replace('.', ',');
                fw.write(l);
                fw.write("\n");
                fw.flush();
            }

//            String args2[] = new String[3];
//            args2[0] = "/user/kamir/wikipedia/raw/2007/2007-12/page*";
//            args2[1] = "/user/kamir/wikipedia/corpus/" + page;
//            args2[2] = listFILE;

        } 
        catch (URISyntaxException ex) {
            Logger.getLogger(SimpleClusterConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        /* lets make our own string to render */

        listFile2Return = Corpus.getListfilePath() + "/" + listFILE;
        
        return point;
    }
    
    private static Object getRelMapLinesVOL(Vector<WikiNode> ACN) {
        StringBuffer sb = new StringBuffer();

        for (WikiNode wn : ACN) {

            try {
                wn = initNodeDetails(wn);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            //['pt',1.053 ],
            sb.append(",['" + wn.wiki + "'," + wn.pageVolume + "]\n");

        }

        return sb.toString();
    }
    private static Object getRelMapLinesK(Vector<WikiNode> ACN) {
        StringBuffer sb = new StringBuffer();

        for (WikiNode wn : ACN) {

            try {
                wn = initNodeDetails(wn);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            //['pt',1.053 ],
            sb.append(",['" + wn.wiki + "'," + wn.kOut + "]\n");

        }

        return sb.toString();
    }
    
    /**
     * Loads the k_OUT and k_IWL for a WikiNode from a Wiki.
     * 
     * @param wn
     * @return 
     */
    private static WikiNode initNodeDetails(WikiNode wn) {
        
        Wiki wiki1 = new Wiki(wn.wiki + ".wikipedia.org");

        String url = ExtractCategorieCorpus.getUrl(wn.wiki, wn.page);

        try {
            wn.kOut = wiki1.getLinksOnPage(wn.page).length;
            wn.kIWL = wiki1.getInterWikiLinks(wn.page).size();
            LOG.info("\n>[***PAGE] : " + wn.page + "\n");

        } 
        catch (Exception ex) {
            LOG.info("\n>[***PAGE] : " + wn.page + "\n");
        }
        return wn;

    }
    
    private static final Logger LOG = Logger.getLogger(PageCorpusAnalyser.class.getName());
    
    static String listFile2Return = null;

    public static String getListFile() {
        return listFile2Return;
    }
    
        /**
     * The result file needs a specific header.
     * 
     * @param fw
     * @throws IOException 
     */
    public static void initHeader(FileWriter fw) throws IOException {
        fw.write("#  double r1 = 100 * volCN / volAL; \n");
        fw.write("#  double r2 = 100 * volIWL / volBL;\n");
        fw.write("#  double r3 = 100 * (volCN + volIWL) / (volAL + volBL);\n");
        fw.write("#\n");
        fw.write("#\n");
        fw.write("#\n#studie\tACN.elementAt(0).wiki\tACN.elementAt(0).page\t");
        fw.write("ACN.size()\t");
        fw.write("IWL.size()\t");
        fw.write("AL.size()\t");
        fw.write("BL.size()\t");
        fw.write("text.vol.CN\t");
        fw.write("text.vol.IWL\t");
        fw.write("text.vol.AL\t");
        fw.write("text.vol.BL\t");
        fw.write("r1\t");
        fw.write("r2\t");
        fw.write("r3\t");
        fw.write("ext\t");
        fw.write("\n#\n");
        fw.flush();
    }

    
}
