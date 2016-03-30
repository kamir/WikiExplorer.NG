/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package research.ETH;

import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.data.series.Messreihe;
import java.util.Vector;
import org.apache.hadoopts.statistics.DistributionTester;
import research.wikinetworks.NodePair;
import experiments.crosscorrelation.KreuzKorrelation;
import experiments.linkstrength.CheckInfluenceOfSingelPeaks;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;

/**
 *
 * @author kamir
 */
public class ExtendedNodePair extends NodePair {
    
    public double[] stats = null;
    boolean debug = true;
    
    public Messreihe mrA = null;
    public Messreihe mrB = null;
    
    KreuzKorrelation kk = null;
        
    public ExtendedNodePair( Messreihe a, Messreihe b ) { 
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
        
        CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_NORMALIZED;
        linkA = CheckInfluenceOfSingelPeaks.calcStrength(kk);
        String s = "\t" + linkA;
        
        CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_CC_TAU_0;
        linkB = CheckInfluenceOfSingelPeaks.calcStrength(kk);
        s = s + "\t" + linkB + "\t";
        if ( Double.isNaN( linkA ) || Double.isNaN( linkB ) ) check = false;
        return s;
    }
    
    public double getLinkA() { 
        return linkA;
    }
    
    public double getLinkB() { 
        return linkB;
    }

    public void show() {
      Vector<Messreihe> v = new Vector<Messreihe>();
      v.add(mrA);
      v.add(mrB);
      
      MultiChart.open(v, mrA.label, "t", "y(t)", true);
    }
    
}
