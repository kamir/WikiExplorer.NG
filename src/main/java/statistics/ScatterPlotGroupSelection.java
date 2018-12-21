/*
 *  Gibt es einen Zusammenhang zw. Access und Edit Activity ?
 *
 */
package statistics;

import org.apache.hadoopts.data.series.TimeSeriesObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;
//import org.tc33.jheatchart.HeatChart;
import com.cloudera.wikiexplorer.ng.util.GS;

/**
 *
 * @author kamir
 */
public class ScatterPlotGroupSelection {

    public static HashMap<Integer, Record> map = new HashMap<Integer, Record>();

    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
        // Welche Sprache?

//       String group = "52-de";
//       String group = "62-es";
//       String group = "72-fr";
//       String group = "60-en";
//       String group = "197-ru";

        int[] lang = {62 }; //, 52}; //, 72, 197 }; // 60,
        String[] code = {"62-es", "52-de"};

        TimeSeriesObject[] rows = new TimeSeriesObject[lang.length];

        ScatterPlotTrendCheck check = new ScatterPlotTrendCheck();

        int z = 0;
        for (int i : lang) {
            System.err.println(">>> Language: " + i);

            // Lesen der Access Activity
            System.err.println(">>> read Access activity ... ");
            readAccessActivity(code[z]);

            // Lesen der Edit Activity            
            System.err.println(">>> read Edits activity ... ");
            readEditActivity(i);


            TimeSeriesObject mr = createTimeSeriesObject(map, i);
            rows[z] = mr;



            Vector<TimeSeriesObject> v = new Vector<TimeSeriesObject>();
            v.add(rows[z]);

            String fn = GS.getPathForGnuplotProject("Access_vs_Edits");
            fn = fn.concat(i + "_all_nodes_activity.dat");


            File f = new File(fn);
            System.out.println(fn + " \t " + f.canWrite());

            mr.writeToFile(f);

           
            check.init(mr, code[z] );
            check.testPerBin();
           

//            HeatChart hc = new HeatChart( rows[z].getData() );
//            hc.saveToFile( new File("./heatmap.gif" ));
            z++;
        }

        check.showChart();







//        MyXYPlot.xRangDEFAULT_MIN = 0 ;
//        MyXYPlot.xRangDEFAULT_MAX = rows[0].getMaxX() ;
//        MyXYPlot.yRangDEFAULT_MIN = 0 ;
//        MyXYPlot.yRangDEFAULT_MAX = rows[0].getMaxY() ;
//
//        MyXYPlot.rowONEDefualtColor = Color.BLACK;
//
//        MyXYPlot.open(  rows, " Access vs. Edit Activity", "Access Activity", "Edit Activity", true);



    }

    private static void readAccessActivity(String code) throws FileNotFoundException, IOException {
        String fAccess = "G:/PHYSICS/PHASE2/data/out/node_activity/extract/" + code + "/node_group_statistics.dat";
        BufferedReader br = new BufferedReader(new FileReader(fAccess));
        while (br.ready()) {
            String line = br.readLine();
            if (!line.startsWith("#") && line.length() > 0) {
                Record r = Record.createAccessRecord(line);
                map.put(r.nodeId, r);
            }
        }
        br.close();
    }

    private static void readEditActivity(int i) throws FileNotFoundException, IOException {
        String fEdits = "G:/PHYSICS/PHASE2/data/out/edit_events/all_nodes_" + i + ".edits.dat";
        BufferedReader br = new BufferedReader(new FileReader(fEdits));
        while (br.ready()) {
            String line = br.readLine();
            if (!line.startsWith("#")) {
                Record.createEditsRecord(map, line);
            }
        }
        br.close();
    }

    private static TimeSeriesObject createTimeSeriesObject(HashMap<Integer, Record> map2, int j) {
        TimeSeriesObject mr = new TimeSeriesObject();
        mr.setLabel("Lang-Id=" + j);
        for (Integer i : map2.keySet()) {
            Record r = map2.get(i);

            // System.out.print( r.nodeId + "\t" + r.access + "\t" + r.edits + "\n");

            mr.addValuePair(r.access, r.edits);
        }
        // System.out.println( mr.toString() );
        return mr;
    }
}

class Record {

    static boolean debug = false;

    static Record createEditsRecord(HashMap<Integer, Record> map, String line) {

        StringTokenizer st = new StringTokenizer(line);
        if (st.countTokens() < 2) {
            return null;
        } else {
            if (debug) {
                System.out.println(line);
            }
        }
        int id = Integer.parseInt(st.nextToken());

        Record r = map.get(id);
        r.edits = Integer.parseInt(st.nextToken());
        map.put(r.nodeId, r);
        return r;
    }

    static Record createAccessRecord(String line) {

        StringTokenizer st = new StringTokenizer(line);

        // System.out.print(line);

        int id = Integer.parseInt(st.nextToken());

        Record r = new Record();

        r.nodeId = id;
        r.tent = Integer.parseInt(st.nextToken());
        r.twent = Integer.parseInt(st.nextToken());
        r.access = Integer.parseInt(st.nextToken());
        // System.out.println( "\t" + id + "\t" + r.access );

        return r;
    }
    public int nodeId = 0;
    public int access = 0;
    public int edits = 0;
    public int twent = 0;
    public int tent = 0;
}
