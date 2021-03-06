/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package m3.wikipedia.explorer;

import m3.util.WikiToolHelper;
import m3.util.MathTools;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import javax.security.auth.login.*;
import javax.swing.JTextArea; 
import m3.wikipedia.explorer.gui.PageRequestDialog;
import org.etosha.core.sc.connector.external.Wiki;

/**
 *
 * @author root
 */
public class WikiExplorerChartTool {
    
    public static PageRequestDialog dlg = null;
     
    public static void main( String[] args ) throws IOException, ClassNotFoundException, FailedLoginException { 
        PageRequestDialog.show(args);
    }
    
     
    public static void processRequestGUILookup( String wikipedia, String pn, JTextArea a ) throws IOException, Exception {
        Wiki wiki = new Wiki( wikipedia + ".wikipedia.org" ); // create a new wiki connection to en.wikipedia.org
                    
        // System.out.println( wiki.getPageText( pn ) );
        
 
        
        String[] cat = wiki.getCategories( pn );
        System.out.println( "> # category: " + cat.length );
        System.out.flush();
        a.append(  "> # category: " + cat.length  );
        a.append( "\n" );
        
        int i = 0;
        for (String c : cat ) { // pages generated from (say) getCategoryMembers()
            System.out.println( i + " - " + c );
            a.append( i + " - " + c );
            a.append( "\n" );
            i++;
        }
        System.out.flush();
             
        HashMap<String,String> map = wiki.getInterWikiLinks( pn );
        System.out.println( "> # of interwiki links: " + map.size() );
        a.append( "> # of interwiki links: " + map.size() );
        a.append( "\n" );
        
                
        int j = 0;
        for (String key : map.keySet() ) { // pages generated from (say) getCategoryMembers()
            System.out.println( j + " : " + key + " -> " + map.get(key) );
            a.append(  j + " : " + key + " -> " + map.get(key) );
            a.append( "\n" );
            j++;
        }
        System.out.flush();
        
        String[] links = wiki.getLinksOnPage( pn );
        System.out.println( "> # of links: " + links.length );
        a.append( "> # of links: " + links.length );
        a.append( "\n" );
        int k = 0;
        for (String link : links ) { // pages generated from (say) getCategoryMembers()
            System.out.println( k + " : " + link );
            // a.append( k + " : " + link  );
            a.append( link  );
            
            a.append( "\n" );
            k++;
        }
        System.out.flush();
        a.append("=================================\n\n\n");
        
        dlg.setNrOfLinks( map.size() , links.length );
       
    }
    
    public static void processRequestGUIFull( String wikipedia, String pn, JTextArea a, 
        String[] langs, JTextArea b , JTextArea e, JTextArea d ) throws IOException, Exception {
        Wiki wiki = new Wiki( wikipedia + ".wikipedia.org" ); // create a new wiki connection to en.wikipedia.org
                    
   System.out.println( ">>> [ " + pn + " ]");        
        String[] cat = wiki.getCategories( pn );
        System.out.println( "> # category: " + cat.length );
        System.out.flush();
        a.append(  "> # category: " + cat.length  );
        a.append( "\n" );
        
        int i = 0;
        for (String c : cat ) { // pages generated from (say) getCategoryMembers()
            System.out.println( i + " - " + c );
            a.append( i + " - " + c );
            a.append( "\n" );
            i++;
        }
        System.out.flush();
             
        HashMap<String,String> map = wiki.getInterWikiLinks( pn );
        System.out.println( "> # of interwiki links: " + map.size() );
        a.append( "> # of interwiki links: " + map.size() );
        a.append( "\n" );
        
        int j = 0;
        for (String key : map.keySet() ) { 
            System.out.println( j + " : " + key + " -> " + map.get(key) );
            a.append(  j + " : " + key + " -> " + map.get(key) );
            a.append( "\n" );
            j++;
        }
        System.out.flush();
        
//        Vector<String> nodes = new Vector<String>();
//        nodes.add( "***" + pn + "***" );
//        
//        String[] links = wiki.getLinksOnPage( pn );
//        System.out.println( "> # of links: " + links.length );
//        a.append( "> # of links: " + links.length );
//        a.append( "\n" );
//        int k = 0;
//        for (String link : links ) { // pages generated from (say) getCategoryMembers()
//            System.out.println( k + " : " + link );
//            a.append( k + " : " + link  );
//                        
//            if ( isLinkInLangsAvailable( link, langs , b ) ) { 
//                nodes.add( link );
//            };
//            
//            a.append( "\n" );
//            k++;
//        }
//        System.out.flush();
//        System.out.println(" go on ... " );
//        
//        a.append("=================================\n\n\n");
//        
//        System.out.println("In all langs: " + nodes.size() );
//        d.append( "In all langs: " + nodes.size() +"\n"  );
//        for( String s : nodes ) { 
//            System.out.println( s );
//            d.append( s + "\n"  );
//        }
//        System.out.flush();
        
        System.out.println( ">>>calculateWeights() ... ");
        
        map.put(wikipedia, pn);
        double[] w = calcLanguageWeights(map);
        
        
        double sum = 0;
        DecimalFormat df = new DecimalFormat("0.000");
        Iterator it = map.keySet().iterator();
        System.out.println("Language weights: " );
        System.out.println("=================================");
        e.append("Language weights: \n" );
        e.append("=================================\n");
        for( int m = 0; m < w.length; m++ ) { 
            String lang = (String) it.next();
            System.out.println( m + " " + lang + " " + df.format( w[m] ) );
            
            /*
                      ['fr', 1.937 ],

 
             */
            String s = df.format( w[m] ).replace(",", ".");
            e.append( "['" + lang + "'," + s + "],\n");        
            //e.append(m + " " + lang + " " + df.format( w[m] ) + "\n");        
//            System.out.println( m + " " + lang + " " + w[m] + "%" );
            sum = sum + w[m];
        }
        System.out.println( "Summe=" + sum );
        System.out.flush();
        
        
    }

    private static boolean isLinkInLangsAvailable(String link, String[] langs, JTextArea b) throws IOException {
        boolean bo = true;
        for( String l : langs ) { 
            Wiki wiki = new Wiki( l + ".wikipedia.org" ); 
            
            link = WikiToolHelper.ignoreUncleanPageNames( link );
            
            if ( link == null ) return false;
            
            HashMap<String,Object> map = wiki.getPageInfo( link );
            Integer i = (Integer)map.get("size");
            b.append( l + " : " + link + " => " + i + "\n");    
            System.out.println( l + " : " + link + " => " + i + "\n");    
            bo = bo && (i > 0);
        }
        System.out.flush();
        return bo;
    }
    
    private static double[] calcLanguageWeights(HashMap<String,String> map) throws IOException {
        double[] weights = new double[ map.size() ];
        int j = 0;
        for (String key : map.keySet() ) { // pages generated from (say) getCategoryMembers()
            Wiki wiki = new Wiki( key + ".wikipedia.org" ); 
            HashMap<String,Object> props = wiki.getPageInfo( (String)map.get(key) );
            Integer i = (Integer)props.get("size");
            if ( i < 0 ) i = 0; 
            weights[j] = i;
            System.out.println( "w="+ weights[j] + " : " + j + " : " + key + " -> " + map.get(key)  );
            j++;
            System.out.println(j);
        }
        // return MathTools.logNormalize( weights );
        return MathTools.normalize( weights );
    }


    
}
