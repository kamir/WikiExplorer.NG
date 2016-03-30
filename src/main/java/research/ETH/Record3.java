 
package research.ETH;

import org.apache.hadoopts.data.series.Messreihe;

public class Record3 {

    String keyReihe;
    
    String keyChart;
    
    double tau;
    double plot_cc;
    double check;
    
    double max_cc;
    double max_cc2;
    
    double[] cc;
    
    double LS = 0.0;    
    
    // für V4
    int l = 0;
    double p1 = 0.0;
    double w1 = 0.0;
    double p2 = 0.0;
    double w2 = 0.0;     
    
    double calcLS() { 
        double ls = 0;

        Messreihe mr = new Messreihe();
        for( int i = 0; i < 11; i++ ) { 
            mr.addValuePair( (double)i - 5.0, cc[i] );
        }

        double stdDev = mr.getStddev();
        
//        double maxY = mr.getMaxY();
        double maxY = calcRealMaxY( mr );
                
        mr.calcAverage();
        
        double avY = mr.getAvarage();
        ls = ( maxY - avY ) / stdDev;

//        System.out.println( mr.getLabel() + "\n\tstDev=" + stdDev + 
//                    "\tmaxY=" + maxY + "\t<y>=" + avY + "\tstr=" + ls );
        // System.out.println(ls + " " + max_cc2 + " " + (ls-max_cc2) );
        return ls;
    }
    
    // 
    // ermittele nich nur das tatsächliche Maximum sondern das 
    //    max(abs(v))
    // 
    private static double calcRealMaxY(Messreihe mr) {
        double extr = 0;
        
        double maxY = mr.getMaxY();
        double minY = mr.getMinY();
        
        double aMaxY = Math.abs(maxY);
        double aMinY = Math.abs(minY);
        
        if ( aMaxY < aMinY) {
            extr = minY;
        }
        else {
            extr = maxY;
        }
        
        return extr;
//        return Math.abs(extr);
    }
    
}