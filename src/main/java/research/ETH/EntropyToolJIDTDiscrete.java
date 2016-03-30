/**
 *   Source:    http://whaticode.com/2010/05/24/a-java-implementation-for-shannon-entropy/
 **/
package research.ETH;

import org.apache.hadoopts.data.series.Messreihe;
import infodynamics.measures.continuous.kernel.EntropyCalculatorKernel;

 
import infodynamics.measures.continuous.*;
import infodynamics.measures.*;
import infodynamics.measures.discrete.EntropyCalculatorDiscrete;
import infodynamics.measures.discrete.MutualInformationCalculatorDiscrete;
import infodynamics.utils.AnalyticMeasurementDistribution;
import infodynamics.utils.EmpiricalMeasurementDistribution;

import infodynamics.utils.MatrixUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author kamir
 */
public class EntropyToolJIDTDiscrete {
    
    /**
     * 
     * 
     * @param r1
     * @param z
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws Exception 
     */
    public static double calcEntropy(Messreihe r1, int z) throws ClassNotFoundException, IllegalAccessException, InstantiationException, Exception {

        double[] variable1 = r1.getYData();

//        double[][] vt1 = transpose(variable1, 2, 100 );
//        double[][] vt2 = transpose(variable2, 2, 100 );
        // 1. Create a reference for our calculator as
        //  an object implementing the interface type:  
        EntropyCalculator entCalc;

        // 2. Define the name of the class to be instantiated here:
        String implementingClass = "infodynamics.measures.continuous.kernel.EntropyCalculatorKernel";

        // 3. Dynamically instantiate an object of the given class:
        //  Part 1: Class.forName(implementingClass) grabs a reference to
        //   the class named by implementingClass.
        //  Part 2: .newInstance() creates an object instance of that class.
        //  Part 3: (MutualInfoCalculatorMultiVariate) casts the return
        //   object into an instance of our generic interface type.
        entCalc = (EntropyCalculator) Class.forName(implementingClass).newInstance();

        // 4. Start using our MI calculator, paying attention to only
        //  call common methods defined in the interface type, not methods
        //  only defined in a given implementation class.
        // a. Initialise the calculator to use the required number of
        //   dimensions for each variable:
        entCalc.initialise();

        // b. Supply the observations to compute the PDFs from:
        entCalc.setObservations(variable1);

        // c. Make the MI calculation:
        double entValue = entCalc.computeAverageLocalOfObservations();

        System.out.printf("ENTROPY calculator \n> %s \n> computed the entropy as: %.5f\n",
                implementingClass, entValue);

        return entValue;
    }
 
    

    /**
     * infodynamics.measures.continuous.kraskov.MutualInfoCalculatorMultiVariateKraskov1
     * 
     * @param r1
     * @param r2
     * @param z
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws Exception 
     */
    public static double calcMutualInformation(Messreihe r1, Messreihe r2, int z) throws ClassNotFoundException, IllegalAccessException, InstantiationException, Exception {

        double[][] variable1 = r1.getDataT();
        double[][] variable2 = r2.getDataT();

//        double[][] vt1 = transpose(variable1, 2, 100 );
//        double[][] vt2 = transpose(variable2, 2, 100 );
        // 1. Create a reference for our calculator as
        //  an object implementing the interface type:  
        MutualInfoMultiVariateCommon miCalc;

        // 2. Define the name of the class to be instantiated here:
        String implementingClass = "infodynamics.measures.continuous.kernel.MutualInfoCalculatorMultiVariateKernel";

        // 3. Dynamically instantiate an object of the given class:
        //  Part 1: Class.forName(implementingClass) grabs a reference to
        //   the class named by implementingClass.
        //  Part 2: .newInstance() creates an object instance of that class.
        //  Part 3: (MutualInfoCalculatorMultiVariate) casts the return
        //   object into an instance of our generic interface type.
        miCalc = (MutualInfoMultiVariateCommon) Class.forName(implementingClass).newInstance();

        // 4. Start using our MI calculator, paying attention to only
        //  call common methods defined in the interface type, not methods
        //  only defined in a given implementation class.
        // a. Initialise the calculator to use the required number of
        //   dimensions for each variable:
        miCalc.initialise(2, 2);

        // b. Supply the observations to compute the PDFs from:
        miCalc.setObservations(variable1, variable2);

        // c. Make the MI calculation:
        double miValue = miCalc.computeAverageLocalOfObservations();

        System.out.printf("MI calculator \n> %s \n> computed the joint MI as: %.5f\n",
                implementingClass, miValue);

        return miValue;
    }

        /**
     * infodynamics.measures.continuous.kraskov.MutualInfoCalculatorMultiVariateKraskov1
     * 
     * @param r1
     * @param r2
     * @param z
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws Exception 
     */
    public static double[] calcMutualInformationDISCRETE(Messreihe r1, Messreihe r2, int z) throws ClassNotFoundException, IllegalAccessException, InstantiationException, Exception {

        double[][] variable1 = r1.getDataT();
        double[][] variable2 = r2.getDataT();

        int[] v1 = MatrixUtils.discretise(r1.getYData(), 100);
        int[] v2 = MatrixUtils.discretise(r2.getYData(), 100);
        
//        double[][] vt1 = transpose(variable1, 2, 100 );
//        double[][] vt2 = transpose(variable2, 2, 100 );
        // 1. Create a reference for our calculator as
        //  an object implementing the interface type:  
        MutualInformationCalculatorDiscrete miCalc = new MutualInformationCalculatorDiscrete(100);

        // 4. Start using our MI calculator, paying attention to only
        //  call common methods defined in the interface type, not methods
        //  only defined in a given implementation class.
        // a. Initialise the calculator to use the required number of
        //   dimensions for each variable:
        miCalc.initialise();

        // b. Supply the observations to compute the PDFs from:
        miCalc.addObservations(v1, v2);

        // c. Make the MI calculation:
        double miValue = miCalc.computeAverageLocalOfObservations();

//        System.out.printf("MI calculator \n> %s \n> computed the joint MI as: %.5f\n",
//                "MutualInformationCalculatorDiscrete", miValue);
        
        double[] back = new double[5];
        back[0] = miValue;
        EmpiricalMeasurementDistribution amd = miCalc.computeSignificance( v1.length );
        back[1] = amd.getMeanOfDistribution();
        back[2] = amd.getStdOfDistribution();
        back[3] = amd.getTSscore();
        back[4] = amd.pValue;
 
        return back;
    }
    
    public static double calcEntropyDISCRETE(Messreihe r1, int z) throws ClassNotFoundException, IllegalAccessException, InstantiationException, Exception {

        int[] v1 = MatrixUtils.discretise(r1.getYData(), 100);

//        double[][] vt1 = transpose(variable1, 2, 100 );
//        double[][] vt2 = transpose(variable2, 2, 100 );
        // 1. Create a reference for our calculator as
        //  an object implementing the interface type:  
        EntropyCalculatorDiscrete entCalc = new EntropyCalculatorDiscrete( 100 );

        entCalc.initialise();

        // b. Supply the observations to compute the PDFs from:
        entCalc.addObservations(v1);

        // c. Make the MI calculation:
        double entValue = entCalc.computeAverageLocalOfObservations();

//        System.out.printf("ENTROPY calculator \n> %s \n> computed the entropy as: %.5f\n",
//                "EntropyCalculatorDiscrete", entValue);

        return entValue;
    }




}