package com.cloudera.wikipedia.explorer;


import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.data.series.Messreihe;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
public class StockDataCache implements Runnable {

    
    static boolean modeDummy = false;
    
    static void setDummy(boolean b) {
        modeDummy = b; 
    }

    private Hashtable<String, Messreihe> hashMR = new Hashtable<String, Messreihe>();
    private Vector<String> liste = new Vector<String>();
    private Hashtable<String, String> hash = new Hashtable<String, String>();
    
    boolean ready = false;

    public static void main(String[] arg) throws IOException, InterruptedException {

        StockDataCache ob = new StockDataCache();
        Thread t = new Thread(ob);
        t.start();

        while (!ob.ready) {
 
            Thread.currentThread().sleep(1000);//sleep for 1000 ms
            System.err.println(";-)");
        }
        
        String name = "Allianz_SE";
        String key = ob.hash.get(name);

        Messreihe mr = ob.hashMR.get(key);
        Vector<Messreihe> v = new Vector<Messreihe>();
        v.add(mr);

        MultiChart.open(v, true);


    }

    public void init(String[] arg) throws IOException {

        if ( modeDummy ) return;



        int c = 0;
        String file = "daxmap.dat";
        FileReader fr = new FileReader(new File(file));
        BufferedReader br = new BufferedReader(fr);
        while (br.ready()   ) {
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
            c++;
        }

        int i = 1;
        for (String s : hash.values()) {
            System.out.println(i + ") SYMBOL: " + s);
            loadForSymbol(s);
            i++;
        }


        MultiChart.open(vmr, true);
        
        ready = true;
    }
    Vector<Messreihe> vmr = new Vector<Messreihe>();

    public void loadForSymbol(String arg) throws IOException {

        String startDate = "2009-01-01";
        String endDate = "2009-12-31";

        String callUrl = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20in%20(%22" + arg + "%22)%20and%20startDate%3D%22" + startDate + "%22%20and%20endDate%3D%22" + endDate + "%22%0A%09%09&diagnostics=true&env=http%3A%2F%2Fdatatables.org%2Falltables.env&format=json";


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

            Object obj = JSONValue.parse(s);
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject query = (JSONObject) jsonObject.get("query");
            JSONObject res = (JSONObject) query.get("results");
            JSONArray qt = (JSONArray) res.get("quote");

            // System.out.println("*** [" + qt + "]");

            String s2 = res.toJSONString();
            int i = 0;

            Messreihe mr = new Messreihe();
            mr.setLabel(arg + "_Adj_Close");

            Hashtable<Long, Double> data = new Hashtable<Long, Double>();

            while (i < qt.size()) {
                JSONObject val = (JSONObject) qt.get(i);
                String b = (String) val.get("Adj_Close");
                String a = (String) val.get("date");


                String target = "Thu Sep 28 20:29:30 JST 2000";
                // DateFormat df = new SimpleDateFormat("E MM dd kk:mm:ss z yyyy");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                Date date = df.parse(a);

                data.put(date.getTime(), Double.parseDouble(b));

                //System.out.println( date.getTime() + " # " + a + " : " + b );
                //System.out.println( "{"+qt.get(i)+"}" );
                i++;
            }

            Set<Long> k = data.keySet();
            ArrayList<Long> liste = new ArrayList<Long>();
            liste.addAll(k);

            Collections.sort(liste);

            for (Long key : k) {
                //System.out.println(key + " : " + data.get(key));
                mr.addValuePair(key, data.get(key));
            }

            vmr.add(mr);

            hashMR.put(arg, mr);
            







        } catch (Exception pe) {
            Logger.getLogger(StockDataCache.class.getName()).log(Level.SEVERE, null, pe);

            System.out.println(pe);

        }
    }

    @Override
    public void run() {
        try {
            init(null);
        } 
        catch (IOException ex) {
            Logger.getLogger(StockDataCache.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    Messreihe getMessreihe(String key) {

        double mu = Math.random() * 100.0 + 100;
        double std = Math.random() * 25 + 5.0;
        
        if ( modeDummy ) { 
            Messreihe mr = Messreihe.getGaussianDistribution(257, mu, std);
            mr.setLabel(key + ":" + mr.getLabel());
            return mr;
        }
        else { 
            return  hashMR.get(key);
        }
    }

    void listKeyset() {
        for (String s : hash.keySet()) {
            System.out.println("###" + s);
        }
    }

    public Hashtable<String, String> getHash() {
        return hash;
    }
}
