package com.cloudera.wikipedia.explorer;

import static com.cloudera.wikiexplorer.ng.app.SimpleSFE.sdl;
import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.data.series.Messreihe;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author kamir
 */
public class StockDataLoader2 implements Runnable {
    

    private void initLocalCache() throws FileNotFoundException, IOException {
        loadListe();
        loadDataFromFile();    
    }
    // namens liste ...
    Vector<String> liste = new Vector<String>();
    Hashtable<String, String> hash = new Hashtable<String, String>();
    Hashtable<String, Messreihe> hashMR = new Hashtable<String, Messreihe>();
    String label = "daxmap.dat";
    String stock = "MSFT";
    String startDate = "2009-01-01";
    String endDate = "2009-12-31";
    String column = "Adj_Close";  // Close, Low, High, Volume 

    boolean local = false;
    boolean web = false;
    
    public static StockDataLoader2 getLocalLoader(String from, String to, String label) throws FileNotFoundException, IOException {
        StockDataLoader2 l = new StockDataLoader2();
        l.startDate = from;
        l.endDate = to;
        l.label = label;
        
        l.local = true;
        l.web = false;
              
        return l;
    }

    public static StockDataLoader2 getOnlineLoader(String column, String from, String to, String label) throws FileNotFoundException, IOException {
        StockDataLoader2 l = new StockDataLoader2();
        l.startDate = from;
        l.endDate = to;
        l.label = label;
        
        l.local = false;
        l.web = true;

        return l;
    }

    public static void main(String[] arg) throws IOException, java.text.ParseException {

 
        StockDataLoader2 sdlA = StockDataLoader2.getLocalLoader("2009-01-01", "2009-12-31", "daxmap.dat");
        sdlA.column =  "Log_Return";
        sdlA.run();
        
//        sdlA.initColumn( "Log_Return" );
//        sdlA.showCharts();
        
        // System.exit(0);
        
        StockDataLoader2 sdl = StockDataLoader2.getLocalLoader("2009-01-01", "2009-12-31", "daxmap.dat");
        sdl.initColumn( "Close" );
        sdl.showCharts();
        
        StockDataLoader2 sdl2 = StockDataLoader2.getLocalLoader("2009-01-01", "2009-12-31", "daxmap.dat");
        sdl2.initColumn( "Adj_Close" );
        sdl2.showCharts();
        
        StockDataLoader2 sdl3 = StockDataLoader2.getLocalLoader("2009-01-01", "2009-12-31", "daxmap.dat");
        sdl3.initColumn( "High" );
        sdl3.showCharts();
        
        StockDataLoader2 sdl4 = StockDataLoader2.getLocalLoader("2009-01-01", "2009-12-31", "daxmap.dat");
        sdl4.initColumn( "Low" );
        sdl4.showCharts();
        
        StockDataLoader2 sdl5 = StockDataLoader2.getLocalLoader("2009-01-01", "2009-12-31", "daxmap.dat");
        sdl5.initColumn( "Open" );
        sdl5.showCharts();
        
        StockDataLoader2 sdl6 = StockDataLoader2.getLocalLoader("2009-01-01", "2009-12-31", "daxmap.dat");
        sdl6.initColumn( "Volume" );
        sdl6.showCharts();
    }
    static Vector<Messreihe> vmr = new Vector<Messreihe>();

    public void loadForSymbol(String key, String selectedColumn, BufferedWriter bw) throws IOException {

        String callUrl = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20in%20(%22" + key + "%22)%20and%20startDate%3D%22" + startDate + "%22%20and%20endDate%3D%22" + endDate + "%22%0A%09%09&diagnostics=true&env=http%3A%2F%2Fdatatables.org%2Falltables.env&format=json";

        bw.write(key + "\t");
        bw.write(callUrl + "\t");

        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(callUrl);

        HttpResponse response = httpClient.execute(httpget);

        System.out.println(response.getProtocolVersion());
        System.out.println(response.getStatusLine().getStatusCode());
        System.out.println(response.getStatusLine().getReasonPhrase());
        System.out.println(response.getStatusLine().toString());

        BufferedReader br = new BufferedReader(
                new InputStreamReader((response.getEntity().getContent())));

        StringBuffer buffer = new StringBuffer();

        String output;
        System.out.println("Output from Server .... \n");
        while ((output = br.readLine()) != null) {
            System.out.println(output);
            buffer = buffer.append(output);
        }
        System.out.println("...");

        try {
            String s = buffer.toString();
            temp.put(key, s);

            bw.write(s + "\n");

            Object obj = JSONValue.parse(s);
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject query = (JSONObject) jsonObject.get("query");
            JSONObject res = (JSONObject) query.get("results");
            JSONArray qt = (JSONArray) res.get("quote");

            System.out.println("*** [" + qt + "]");

            String s2 = res.toJSONString();
            int i = 0;

            Messreihe mr = new Messreihe();
            mr.setLabel(key + "_" + selectedColumn);

            Hashtable<Long, Double> data = new Hashtable<Long, Double>();

            while (i < qt.size()) {
                JSONObject val = (JSONObject) qt.get(i);
                String b = (String) val.get( selectedColumn );
                String a = (String) val.get("date");

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                Date date = df.parse(a);

                data.put(date.getTime(), Double.parseDouble(b));

                System.out.println( date.getTime() + " # " + a + " : " + b );
                System.out.println( "{"+qt.get(i)+"}" );
                i++;
            }

            Set<Long> k = data.keySet();
            ArrayList<Long> liste = new ArrayList<Long>();
            liste.addAll(k);

            Collections.sort(liste);

            for (Long key2 : k) {
                System.out.println(key2 + " : " + data.get(key2));
                mr.addValuePair(key2, data.get(key2));
            }

            vmr.add(mr);

        } 
        catch (Exception pe) {
            Logger.getLogger(StockDataLoader2.class.getName()).log(Level.SEVERE, null, pe);

            System.out.println(pe);

        }
    }

    private void loadListe() throws FileNotFoundException, IOException {
        String file = "./DATA/" + label;
        FileReader fr = new FileReader(new File(file));
        BufferedReader br = new BufferedReader(fr);
        while (br.ready()) {
            String line = br.readLine();

            if (line.startsWith("#")) {
            } else {
                StringTokenizer st = new StringTokenizer(line, "\t");
                String tok1 = st.nextToken();
                String tok2 = st.nextToken();
                System.out.println(tok1 + " " + tok2);

                hash.put(tok1, tok2);

                //System.out.println( tok );
                if (!liste.contains(tok2)) {
                    liste.add(tok2);
                }
            }
        }
        System.out.println( "> list loaded ... ");
    }

    private void loadDataFromWeb() throws IOException {
        loadDataFromWeb( "Adj_Close" );
    }
    
    Hashtable<String,String> temp = null;
    
    private void loadDataFromWeb( String column ) throws IOException {
        temp = new Hashtable<String,String>();
    
        int i = 1;

        FileWriter fw = new FileWriter(getFilename());

        BufferedWriter bw = new BufferedWriter(fw);

        for (String s : hash.values()) {
            System.out.println(i + ") SYMBOL: " + s);
            loadForSymbol(s, column, bw);
            i++;
        }

        bw.flush();
        bw.close();

    }

    private void initCacheFromWeb(String col) throws FileNotFoundException, IOException {
        column = col;
        loadListe();
        loadDataFromWeb( column );       
    }

    private void loadDataFromFile() throws FileNotFoundException, IOException {
        
        temp = new Hashtable<String,String>();
        vmr = new Vector<Messreihe>();
        BufferedReader br = new BufferedReader( new FileReader( getFilename() ) );
        while( br.ready() ) { 
            String line = br.readLine();
            StringTokenizer st = new StringTokenizer( line,"\t");
            int s = st.countTokens();
            String k = st.nextToken();
            String req = st.nextToken();
            String resp = st.nextToken();
            System.out.println(s + " :" + k + "\n\t" + req + "\n\t\t" + resp );
            temp.put(k, resp);
        }
        System.out.println("> responses reloaded ...");
    }

    String[] chL = new String[3];
    public void showCharts() {
       initLabels();        
       MultiChart.open(vmr, chL[0],chL[1],chL[2] ,true);
    }

    public String getFilename() {
        return "stockdata.collection." + this.startDate +"_"+ this.endDate + "_" + label;
    }

    String labelY = null;
    
    boolean lRmode = false;
    public void initColumn(String col) throws java.text.ParseException {
        
        if ( col.equals( "Log_Return" ) ) {
            lRmode = true;
            col = "Close";
            labelY = "Log_Return";
        }
        
        this.hashMR = new Hashtable<String, Messreihe>();
        
        column = col;
        for( String key : temp.keySet() ) {
            String s = temp.get(key);

            Object obj = JSONValue.parse(s);
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject query = (JSONObject) jsonObject.get("query");
            JSONObject res = (JSONObject) query.get("results");
            JSONArray qt = (JSONArray) res.get("quote");

            // System.out.println("*** [" + qt + "]");

            String s2 = res.toJSONString();
            int i = 0;

            Messreihe mr = new Messreihe();
            mr.setLabel(key + "_" + column);

            Hashtable<Long, Double> data = new Hashtable<Long, Double>();

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date t0 = df.parse( startDate );
                
            long last = 0;
            double lastv = 0;
            while (i < qt.size()) {
               try { 
                   JSONObject val = (JSONObject) qt.get(i);
                String b = (String) val.get( column );
                String a = (String) val.get("date");

                Date date = df.parse(a);
                double value = Double.parseDouble(b);
                
                long dt = date.getTime() - t0.getTime();
                dt = dt / (3600000 * 24);
                
                data.put(dt, value );

                //System.out.println( date.getTime() + " # " + a + " : " + b );
                //System.out.println( "{"+qt.get(i)+"}" );
                
                } catch (java.text.ParseException ex) {
                    Logger.getLogger(StockDataLoader2.class.getName()).log(Level.SEVERE, null, ex);
                }
               i++;
            }
                

            lastv = 0.0;
            last = 0;
            Set<Long> k = data.keySet();
            ArrayList<Long> liste = new ArrayList<Long>();
            for( long xx : k ) { 
                liste.add(xx);
            } 

            Collections.sort(liste);

            for (Long key2 : liste) {

                
                                
                long delta = key2 - last; 
                if ( delta > 1 ) {
                    for( int x = 0; x < delta; x++ ) { 
                        if ( column.equals("Volume") )
                            mr.addValuePair( last+x, 0 );
                        else       
                            mr.addValuePair( last+x, lastv );
                    }
                }
                
                last = key2;
                lastv = data.get(key2);
                
                
                
                // System.out.println(key2 + " : " + data.get(key2));
                mr.addValuePair(key2, lastv);
            }
            
            // System.out.println( mr );
            
            if ( lRmode ) { 
                mr = calcLogReturn( mr );
            }
            
            vmr.add(mr);
            hashMR.put(key, mr);
            labelY = column;
        }
        lRmode = false;
    }

    public Messreihe getMessreihe(String key) {
        return this.hashMR.get(key);
    }

    public Hashtable<String,String> getHash() {
        return this.hash;
    }

    public String[] getChartLabels() {
        initLabels();
        return chL;
    }

    private void initLabels() {
        chL[0] = label + " [" + startDate + " ... " + endDate + "]";
        chL[1] = "t";
        chL[2] = labelY;
    }

    @Override
    public void run() {
        try {
            try {
                
                if ( local ) {
                    initLocalCache();
                    initColumn( column );
                }
                else 
                    initCacheFromWeb( column );
                
                showCharts();
                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(StockDataLoader2.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(StockDataLoader2.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } 
        catch (java.text.ParseException ex) {
            Logger.getLogger(StockDataLoader2.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private Messreihe calcLogReturn(Messreihe mrO) {
        Messreihe mr = new Messreihe();
        mr.setLabel( mrO.getLabel() + "_Return" );
        
        double pj = 0;
        
        double pi = 0.0;
        double v2 = 0.0;

        double ri = 0.0;
        
        for( int k = 0; k < mrO.xValues.size(); k++ ) {

            try {
                pi = (Double)mrO.yValues.elementAt(k);
                v2 = (Double)mrO.xValues.elementAt(k);
            }
            catch( Exception ex) {

            }
            if ( k==0 ) ri = 0.0;
            else {
                ri = (pi - pj) / pi;
                pj = pi;
            }    
            mr.addValuePair(v2, ri );
        }
        
        return mr;
    }
}
