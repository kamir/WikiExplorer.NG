/*
 
 */
package m3.wikipedia.corpus.extractor;
 
import m3.io.CNResultManager2;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import m3.jstat.data.Corpus;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import m3.wikipedia.analysis.charts.RepresentationPlotBubbleChart;
import hadoop.cluster.connector.SimpleClusterConnector;
import java.io.StringReader;
import org.apache.velocity.Template;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.crunchts.store.TSB;
import wikiapiclient.SnippetLoader;
import wikiapiclient.WikiORIGINAL;
/**
 *
 * @author kamir
 */
public class CorpusStatisticsMain2 {

    public static void main(String args[]) throws IOException, URISyntaxException, ClassNotFoundException, InstantiationException, IllegalAccessException, ParseException {

//        JFileChooser jfc = new JFileChooser();
//        jfc.setCurrentDirectory( new File("/home/kamir/bin/WikiExplorer/WikiExplorer") );
//        jfc.showOpenDialog(null);
        String[] _wiki = {"de", "de"};
        String[] _page = {"Amoklauf_von_Erfurt", "Illuminati_(Buch)"};

        for (int i = 0; i < _page.length; i++) {

            String studie = "PLOSONE";
            String wiki = _wiki[i];
            String page = _page[i];

            // the extensions contain additional measures extracted from text.
            String[] ext = {"", ""};

            createTextStatisticsResultFile(studie, _wiki, _page, Corpus.mode_XML, ext, null);

            System.out.println("DONE");

        }

    }

    /**
     * @param args the command line arguments
     */
    public static void createTextStatisticsResultFile(String studie, String[] wiki, String[] page, int mode, String[] ext, CNResultManager2 rm) throws IOException, URISyntaxException, ClassNotFoundException, InstantiationException, IllegalAccessException, ParseException {

        String p = TSB.getAspectFolder("LRI", studie).getAbsolutePath();

        String f = "result_" + studie + ".dat.csv";

        File file = new File(p + "/" + f);

        FileWriter fw = new FileWriter(file);

        SimpleClusterConnector.initHeader(fw);

        FileWriter bubble = RepresentationPlotBubbleChart.getWriter(studie);

        String points = "";
        // HEADLINE        
        String HEADLINE = "['page', 'log(REP v)', 'log(REL v)', 'Categorie', 'log(REP k)'],";
        String DATA = "";

        for (int i = 0; i < wiki.length; i++) {

            System.out.println("> textanalysis : " + i);

            DATA = DATA + SimpleClusterConnector.runPerPageCorpusTextAnalysis(wiki[i], page[i], studie, fw, mode, ext[i], rm, i, bubble);

        }

        fw.close();
        bubble.close();

        System.out.println(">>> RESULT in : " + file.getAbsolutePath());

        // Create the plot with VELOCITY 
        /* first, we init the runtime engine.  Defaults are fine. */
        Velocity.init();

        /* lets make a Context and put data into it */
        VelocityContext context = new VelocityContext();

        context.put("HEADLINE", HEADLINE);
        context.put("DATA", DATA);

        System.out.println(HEADLINE);
        System.out.println(DATA);

        /* lets render a template */
        StringWriter sw = new StringWriter();

        File fileTemplateFileFromSnippet = File.createTempFile("velocity_snippet_", "_template.vm");

        /**
         * Snippet-LoaderTool
         */
        
        String tplName = "RepresentationPlot";
        
        WikiORIGINAL wikiServer = new WikiORIGINAL("semanpix.de/opendata/wiki", "");
        
        SnippetLoader sl = new SnippetLoader();
        sl.init(wikiServer);
        
        /**
         * 
         * All the rest is the report-merger ...
         * 
         */
        
        String template = sl.loadVelocityTemplate( tplName + ".code", fileTemplateFileFromSnippet, false);

        RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();
        StringReader reader = new StringReader( template );
        SimpleNode node = runtimeServices.parse( reader, tplName );

        Template tpl = new Template();
        tpl.setRuntimeServices(runtimeServices);
        tpl.setData(node);
        tpl.initDocument();

        Velocity.mergeTemplate("REPPLOTTemplate.vm", context, sw);

        System.out.println(" template : " + sw.toString().length());

        System.out.println(" report : " + sw.toString());

        File fn2 = new File("./charts/repplot_" + studie + ".html");

        System.out.println(" create file : " + fn2.getAbsolutePath());

        FileWriter fw2 = new FileWriter(fn2);
        fw2.write(sw.toString());
        fw2.flush();
        fw2.close();

    }


}
