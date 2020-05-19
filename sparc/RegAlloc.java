package sparc;

import graph.InterferenceGraph;
import graph.Node;
import tree.NameOfTemp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

public class RegAlloc {
    static final String [] inArgColors = { "%i1", "%i2", "%i3", "%i4", "%i5" };
    static final String [] outArgColors = { "%o1", "%o2", "%o3", "%o4", "%o5" };
    static final String [] localColors = { "%l0", "%l1", "%l2", "%l3", "%l4", "%l5", "%l6", "%l7" };
    static final String [] globalColors = { "%g0", "%g1", "%g2", "%g3", "%g4", "%g5", "%g6", "%g7" }; // %g0 will never be used

    final private ArrayList<String> availableColors = new ArrayList<>();
    final private Hashtable<NameOfTemp, String> colored = new Hashtable<>();
    private InterferenceGraph g;
    private SPARCFrame frame;
    private int colorsUsed = 0;
    public int calleeSavesUsed = 0;

    public RegAlloc(SPARCFrame frame, InterferenceGraph g){
        this.g = g;
        this.frame = frame;
        availableColors.addAll(Arrays.asList(localColors));
        availableColors.addAll(Arrays.asList(globalColors));
        if(frame.formals.size() < 6) {
            availableColors.addAll(frame.formals.size() + 1, Arrays.asList(inArgColors));
            availableColors.addAll(frame.formals.size() + 1, Arrays.asList(outArgColors));
        }
    }

    public void allocRegs(){
        for(Node<NameOfTemp> n : g.nodes){
            if(!frame.tempMap.containsKey(n.elem)){
                final String color = selectColor(n);
                if(color == null)
                    System.err.println("ERROR:allocRegs: Register spilling.");
                else {
                    frame.tempMap.put(n.elem, color);
                    for(int i = 0; i < globalColors.length; i++) {
                        if (globalColors[i].equals(color)) {
                            calleeSavesUsed++;
                            break;
                        }
                    }
                }
            }
        }
    }

    private String selectColor(Node<NameOfTemp> n){
        for(int i = 0; i < availableColors.size(); i++){
            boolean found = true;
            for(int j = 0; j < n.adj().size(); j++){
                Node<NameOfTemp> tmpNode = n.adj().get(j);
                if(frame.tempMap.containsKey(tmpNode.elem)
                    && frame.tempMap.get(tmpNode.elem).equals(availableColors.get(i))) {
                    found = false;
                    break;
                }
            }
            if(found) return availableColors.get(i);
        }
        return null;
    }
}
