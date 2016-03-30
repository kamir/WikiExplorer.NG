package research.networks.data;

import research.wikinetworks.NodePair;

/**
 *
 * @author kamir
 */
public class WikiEdge {

    NodePair np = null;
    String label = "-";

    public WikiEdge(NodePair _np) {
        np = _np;
    }

    public WikiEdge(String string) {
        label = string;
    }

    public String getWeight() {
        return "W";
    }

    public String getCapacity() {
        return "C";
    }

    public String toString() {
        return label;
    };


}
