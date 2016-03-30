/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package research.topics.networks.gui;

import org.apache.hadoopts.data.export.MesswertTabelle;
import org.apache.hadoopts.data.export.OriginProject;

/**
 *
 * @author kamir
 */
public class AnalysisProject {
    
    public static String baseFolder = "p:/DATA/015_Berlin/";
    
    public OriginProject originPro = null;
    
    public void initOriginProject() { 
        originPro = new OriginProject();
        originPro.folderName = baseFolder + "origindata/";
    };

    public void storeMesswertTabelle(MesswertTabelle mwt) {
        originPro.storeMesswertTabelle( mwt );
    }
    
}
