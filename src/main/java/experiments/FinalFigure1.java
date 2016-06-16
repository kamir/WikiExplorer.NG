/*
 * Copyright 2016 kamir.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package experiments;

import org.apache.hadoopts.app.bucketanalyser.TSOperationControlerPanel;

/**
 * 
 * In order to produce a scientific result we have to export 
 * experimental results for further aggregation and interpretation.
 * 
 * The Experiment Runner needs a MultiChart to plot the results.
 * But in many cases we need multiple Experiments with different parameters.
 * 
 * This is why an additional class is used to combine all experiments which
 * allows us to reproduce results at a later point in time.
 * 
 * @author kamir
 */
public class FinalFigure1 {
    
    public static void main(String[] ARGS ) throws Exception {
        
        CorrelationPropertiesExperiment001.BASEFOLDER = "/Users/kamir/Documents/THESIS/dissertationFINAL/main/FINAL/LATEX/semanpix/FinalFigure1";
    
        
        int z = 13;
        
        String[] args1 = { "50",z+"", "0.0" , "0", "false" };
        CorrelationPropertiesExperiment001.main(args1);
        
        String[] args2 = { "50",z+"", "0.0" , "2", "false" };
        CorrelationPropertiesExperiment001.main(args2);
        
        
        
        String[] args3 = { "50",z+"", "0.2" , "0", "false" };
        CorrelationPropertiesExperiment001.main(args3);
        
        String[] args4 = { "50",z+"", "0.2" , "2", "false" };
        CorrelationPropertiesExperiment001.main(args4);
        
        

        String[] args5 = { "50",z+"", "0.5" , "0", "false" };
        CorrelationPropertiesExperiment001.main(args5);
        
        String[] args6 = { "50",z+"", "0.5" , "2", "false" };
        CorrelationPropertiesExperiment001.main(args6);

        
        
        String[] args7 = { "50",z+"", "0.8" , "0", "false" };
        CorrelationPropertiesExperiment001.main(args7);
        
        String[] args8 = { "50",z+"", "0.8" , "2", "false" };
        CorrelationPropertiesExperiment001.main(args8);

        
    }
    
    
    
}
