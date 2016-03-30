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
public class WikiDataCache implements Runnable {

    Hashtable<String, Messreihe> hashMR = new Hashtable<String, Messreihe>();
    Vector<String> liste = new Vector<String>();
    public Hashtable<String, String> hash = new Hashtable<String, String>();
    boolean ready = false;

    public static void main(String[] arg) throws IOException, InterruptedException {

        WikiDataCache ob = new WikiDataCache();
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


        // hier wird nun das SESUENCE File gelesen ...

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

        MultiChart.open(vmr, true);
        
        ready = true;
    }
    
    Vector<Messreihe> vmr = new Vector<Messreihe>();

    
     

    @Override
    public void run() {
        try {
            init(null);
        } 
        catch (IOException ex) {
            Logger.getLogger(WikiDataCache.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
