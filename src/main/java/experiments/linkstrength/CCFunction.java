package experiments.linkstrength;

import org.apache.hadoopts.data.series.Messreihe;
import experiments.crosscorrelation.KreuzKorrelation;
import static experiments.linkstrength.CheckInfluenceOfSingelPeaks._debug;
import static experiments.linkstrength.CheckInfluenceOfSingelPeaks.removeMaximumValueFromRow;

/**
 *
 * @author kamir
 */
public class CCFunction {
    
    static public boolean debug = false;
    
    /** 
     *   This is the PARAMETER to select the location in the Cross-Correlation-Function 
     **/
    public static int ID_TO_SELECT_CC_FROM = 0;
    
    public static double calcStrength_VERSION__ADJUSTED(KreuzKorrelation kr, boolean debug ) {
    
        double C = kr.adjustedCC;
        double CS = kr.adjustedCCSMEAN;
        double SIGMA = kr.adjustedSIGMA;
        
        double str = ( C - CS ) / SIGMA;
        if ( debug ) System.out.println( "***"  + C + " " + " " + CS + " " + " " + SIGMA + "==>" + str );
        return str;
        
    }

    public static double calcStrength_VERSION_B( Messreihe mr ) {
        double v = -100.0;
        if ( mr != null ) {
            if ( debug ) System.out.println( "~~~ (" + mr + ")" );
            v = (Double)mr.yValues.elementAt(ID_TO_SELECT_CC_FROM);  
            if ( debug ) {           
                //System.out.println( mr.toString() );
                //System.out.println( mr.getLabel() + "\n\tstr=" + v );
                 System.out.println( mr.yValues.size() + " \t " +   
                                     mr.yValues.elementAt(0) );
                 if ( v != -100.0 ) System.out.println( "\tstr=" + v );
            }
        } 
        else {
            if ( debug )
                System.err.println( "mr war mal wieder NULL ");
        }
        return v;
    };
    
        /**
     * Calculates the standardized Link strength for the Cross-Correlation
     * function of a certain length.
     * 
     * The absolute value is taken into account.
     * 
     * @param mr
     * @return
     */
    public static double calcStrength_VERSION_A( Messreihe mr ) {

        if( mr == null ) return -100.0;
        
        double stdDev = mr.getStddev();
        
//        double maxY = mr.getMaxY();
        double maxY = calcAbsoluteMaxY( mr );
        
        mr.calcAverage();
        
        double avY = mr.getAvarage();
        double v = ( maxY - avY ) / stdDev;
 
        if ( debug ) {
            System.out.println( mr.getLabel() + "\n\tstDev=" + stdDev + 
                    "\tmaxY=" + maxY + "\t<y>=" + avY + "\tstr=" + v );
        }
        return v;
    };

    
    
    
    
    
    /**
     * 
     * @param mr
     * @return 
     */
    private static double calcAbsoluteMaxY(Messreihe mr) {
        
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
        
        return Math.abs(extr);
    }
    
    
    /**
     * TODO : EXTRACT data for :: 
     * 
     * calcStrength_VERSION_C(Messreihe mr, double stdevA, double stdevB, double mwA, double mwB )
     * 
     * and check results....
     */ 
    public static double _calcStrength_VERSION_D(KreuzKorrelation kr) {

        // maximum der CC-Function ermitteln
        double maxY = kr.getMaxY();

        Messreihe mr2 = kr.copy();
        
//        System.out.println( mr2.getStatisticData("<<<") );
        
        mr2 = removeMaximumValueFromRow( mr2 );
//        System.out.println( mr2.getStatisticData(">>>") );
        
        // mittelwert ermitteln mit dem Maximum
        double mwA = kr.getAvarage2();
        double stdevA = kr.getStddev();
        
        // Maximum entfernen (auf null setzen)  und neuen Mittelwert (zahl = zahl - 1 ) !!!!
        double mwB = mr2.getAvarage2();
        
        double stdevB = mr2.getStddev();
        
        // Effekt in den Daten
        double v1 = ( maxY - mwA ) / stdevA;
        
        // Rauschen
        double v2 = ( maxY - mwB ) / stdevB;
        
        return v2/v1;
    }

    
    
    
    public static double calcStrength_VERSION_D(Messreihe mr, double stdevA, double stdevB, double mwA, double mwB ) {
               
        double maxY = mr.getMaxY();

        double v1 = ( maxY - mwA ) / stdevA;
        
        double v2 = ( maxY - mwB ) / stdevB;
        

        return v2/v1;
    }
    
}
