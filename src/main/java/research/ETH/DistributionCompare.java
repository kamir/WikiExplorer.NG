package research.ETH;

import java.io.BufferedWriter;
import java.io.IOException; 
import org.apache.commons.math3.stat.inference.TestUtils;
import org.apache.hadoopts.statistics.HaeufigkeitsZaehlerDouble;

/**
 *
 * @author kamir
 */
public class DistributionCompare {

    public double[] zNormal = null;
    public double[] zShuffles = null;

    public void storeData(BufferedWriter bw, String pre) throws IOException, IllegalArgumentException {
        String line = describe(zNormal) + " ";
        line = line + describe(zShuffles) + "  ";
        line = line + compare(zNormal, zShuffles);
        bw.write(pre + " " + line + "\n");
        bw.flush();
    }

    private String describe(double[] dist) {
        return "anz=" + dist.length;
    }

    private String compare(double[] d1, double[] d2) throws IllegalArgumentException {

        double n1 = d1.length;
        double av1 =  0.0;
        double stdev1 =  0.0;
        double avMW1 =  0.0;

        double n2 = d2.length;
        double av2 =  0.0;
        double stdev2 =  0.0;
        double avMW2 =  0.0;

        double p = 0.0;

        try {
            n1 = d1.length;
            av1 = stdlib.StdStats.mean(d1);
            stdev1 = stdlib.StdStats.stddev(d1);
            avMW1 = stdev1 / Math.sqrt(n1);

            n2 = d2.length;
            av2 = stdlib.StdStats.mean(d2);
            stdev2 = stdlib.StdStats.stddev(d2);
            avMW2 = stdev2 / Math.sqrt(n2);

            p = TestUtils.tTest(d1, d2);
        } 
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return p + " " + n1 + " " + av1 + " " + stdev1 + " " + avMW1 + " " + n2 + " " + av2 + " " + stdev2 + " " + avMW2;

    }
}
