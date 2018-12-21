/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package research.ETH;

import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.data.series.TimeSeriesObject;
import java.util.Vector;
import research.wikinetworks.NodePair;
import experiments.crosscorrelation.KreuzKorrelation;

/**
 *
 * @author kamir
 */
public class ExtendedNodePair extends NodePair {
    
    public double[] stats = null;
    boolean debug = true;
    
    public TimeSeriesObject mrA = null;
    public TimeSeriesObject mrB = null;
    
    KreuzKorrelation kk = null;
        
    public ExtendedNodePair( TimeSeriesObject a, TimeSeriesObject b ) { 
        mrA = a;
        mrB = b;
        System.out.println( a.getLabel() + "\t" + b.getLabel() + "\t" );
    }
    
    public KreuzKorrelation calcCrossCorrelation() throws Exception {
        
//        if ( MainETH.dtA == null ) { 
//            MainETH.dtA = DistributionTester.getDistributionTester();
//        }
//        if ( MainETH.dtB == null ) { 
//            MainETH.dtB = DistributionTester.getDistributionTester();
//        }
//        
//        double[] d1 = mrA.getYData();
//        double[] d2 = mrB.getYData();
//        
//        shapA = MainETH.dtA.testDatasetS(d1);
//        shapB = MainETH.dtB.testDatasetS(d2);
                
        kk = KreuzKorrelation.calcKR(mrA, mrB, false, false) ;
        return kk;
    }
    
    public String shapA = "";
    public String shapB = "";   

    
    boolean check = true;
    public String toString() { 
        String line = mrA.getLabel() + "\t" + mrB.getLabel() + "\t" + getLinkStrength() + "\t" + kk.getResultLine2() + "\t" + shapA + "\t" + shapB;
        if (!check)
            line = null;        
        check = true;
        return line;
    };

    double linkA = 0.0;
    double linkB = 0.0;
    public String getLinkStrength() {
        
//        CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_NORMALIZED;
//        linkA = CheckInfluenceOfSingelPeaks.calcStrength(kk);
//        String s = "\t" + linkA;
//        
//        CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_CC_TAU_0;
//        linkB = CheckInfluenceOfSingelPeaks.calcStrength(kk);
        
        String s = "\t" + getLinkA() + "\t" + getLinkB();
        
        return s;
    }
    
    public double getLinkA() { 
        return linkA;
    }
    
    public double getLinkB() { 
        return linkB;
    }

    public void show() {
      Vector<TimeSeriesObject> v = new Vector<TimeSeriesObject>();
      v.add(mrA);
      v.add(mrB);
      
      MultiChart.open(v, mrA.label, "t", "y(t)", true);
    }
    
}
