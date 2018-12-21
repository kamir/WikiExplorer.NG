package analysis.wikipagecorpus;

import hadoop.cluster.connector.SimpleClusterConnector;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import m3.util.html.HTMLRenderer;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.crunchts.store.TSB; 
//import wikiapiclient.SnippetLoader;
//import wikiapiclient.WikiORIGINAL;
import m3.wikipedia.analysis.charts.RepresentationPlotBubbleChart;
import org.openide.util.Exceptions;

/**
 * Needs a clear refactoring ...
 * 
 * ... extracts go into HTMLRenderer.
 * 
 * @author kamir
 */
public class RepPlotRenderer extends HTMLRenderer {
    
    public static void main(String[] args) throws IOException, ParseException {
    
        String p = TSB.getAspectFolder("LRI", "snippetRendererTests" ).getAbsolutePath();

        String f = "result_test1.dat.csv";

        File file = new File(p + "/" + f);

        FileWriter fw = new FileWriter(file);

        PageCorpusAnalyser.initHeader(fw);

        FileWriter bubble = RepresentationPlotBubbleChart.getWriter("snippetRendererTests");

        String points = "";
        // HEADLINE        
        String HEADLINE = "['page', 'log(REP v)', 'log(REL v)', 'Categorie', 'log(REP k)'],";
        String DATA = "1, 2, A, 0.5";
    
        fw.close();
        bubble.close();

        System.out.println(">>> RESULT in : " + file.getAbsolutePath());

        try {
            // Create the plot with VELOCITY
            /* first, we init the runtime engine.  Defaults are fine. */
            Velocity.init();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }

        /* lets make a Context and put data into it */
        VelocityContext context = new VelocityContext();

        context.put("HEADLINE", HEADLINE);
        context.put("DATA", DATA);

        System.out.println(HEADLINE);
        System.out.println(DATA);

        /* lets render a template */
        StringWriter sw = new StringWriter();

        File fileTemplateFileFromSnippet = File.createTempFile("velocity_snippet_", "_template.vm", new File(".") );

        System.out.println( fileTemplateFileFromSnippet.getAbsolutePath() );
        
        /**
         * Generic LoaderTool
         */
        
        String tplName = "RepresentationPlot";
        // String tplName = "LatexEquation";
        
       // WikiORIGINAL wikiServer = new WikiORIGINAL("semanpix.de/opendata/wiki/", "");
       // SnippetLoader sl = new SnippetLoader();
       //  sl.init(wikiServer);
        
       // String template = sl.loadVelocityTemplate( tplName + ".code", fileTemplateFileFromSnippet, false);

        
        
        System.out.println();
        
        
        
//        RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();
//        StringReader reader = new StringReader( template );
//        
//        SimpleNode node = runtimeServices.parse( reader, tplName );
//
////        Template tpl = new Template();
////        tpl.setRuntimeServices(runtimeServices);
////        tpl.setData(node);
////        tpl.initDocument();
//
//        // Velocity.mergeTemplate("REPPLOTTemplate.vm", context, sw);
//        Velocity.mergeTemplate( fileTemplateFileFromSnippet.getName(), context, sw);
//
//        System.out.println(" template : " + sw.toString().length());
//
//        System.out.println(" report : " + sw.toString());
//
//        File fn2 = new File("./charts/repplot_snippetRendererTests.html");
//
//        System.out.println(" create file : " + fn2.getAbsolutePath());
//
//        FileWriter fw2 = new FileWriter(fn2);
//        fw2.write(sw.toString());
//        fw2.flush();
//        fw2.close();

    }
    
}
