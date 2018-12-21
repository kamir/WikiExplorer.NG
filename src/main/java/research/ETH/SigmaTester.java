/*
 *   Tested die Funktion für die Standardabweichung auf 
 *   einem Strom von Zahlen ...
 */
package research.ETH;

import org.apache.hadoopts.data.series.TimeSeriesObject;

/**
 *
 * @author kamir
 */
public class SigmaTester {
    
    public static void main( String[] args ) { 
        
        stdlib.StdRandom.initRandomGen(1);
        
        int n = 10000;
        TimeSeriesObject mr = TimeSeriesObject.getGaussianDistribution(n, 5.0, 2.0);
        double[] yData = mr.getYData();
        
        double sx = 0.0;
        double sxx = 0.0;
        for( double d : yData ) { 
            sx = sx + d;
            sxx = sxx + d * d;
        }
        
        double sigma = getSigma(n, sx, sxx);
        
        System.out.println( "mw="+stdlib.StdStats.mean(yData) + "\nstdev=" + stdlib.StdStats.stddev( yData ) + "\nvar=" +
                            stdlib.StdStats.var( yData ) + "\nsigma=" + sigma );
    }
    
    private static double getSigma(double n, double sx, double sxx) {
        double f = 1.0/(n-1.0);
        
        double sigma = Math.sqrt( f * ( sxx - sx*sx/n  ));
        return sigma;
    }
    
}
