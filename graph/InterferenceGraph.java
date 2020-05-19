package graph;

import assem.Instruction;
import tree.NameOfTemp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashSet;

public class InterferenceGraph extends Graph<NameOfTemp> {
    private FlowGraph fg;

    // Table of temp lists that are live for their corresponding instruction nodes
    private Hashtable<Node, ArrayList<NameOfTemp>> liveTable = new Hashtable<>();

    // These are reset every time analyzeLiveliness is called from the constructor
    private NameOfTemp currTemp;
    private HashSet<Node<Instruction>> visited = new HashSet<>();

    // Record the life of temps in a DFS manner
    private void analyzeLiveliness(Node<Instruction> n){
        // Base cases
        if(currTemp == null || fg == null) return;
        if(visited.contains(n) || (fg.def(n) != null && fg.def(n).contains(currTemp))) return;

        // Visit
        visited.add(n);

        // Operate and traverse
        for(Node<Instruction> next : n.pred) {
            if(visited.contains(next)) continue;
            if(!liveTable.containsKey(next))
                liveTable.put(next, new ArrayList<>());
            if(!liveTable.get(next).contains(currTemp))
                liveTable.get(next).add(currTemp);
            analyzeLiveliness(next);
        }
    }

    public InterferenceGraph(FlowGraph g){
        fg = g;

        // Analyze liveliness of temporaries
        for(Node<Instruction> instNode : g.nodes) {
            if(g.use(instNode) != null)
                for (NameOfTemp t : g.use(instNode)) {
                    if(t == null) continue;
                    currTemp = t;
                    visited.clear();
                    analyzeLiveliness(instNode);
                }
        }

        // Construct interference graph
        for(Node<Instruction> instNode : g.nodes){
            if(g.def(instNode) != null) {
                for (NameOfTemp d : g.def(instNode)) {
                    if(d == null) continue;
                    Node<NameOfTemp> src = getNode(d);

                    if (instNode.elem.isMove()) {
                        ArrayList<NameOfTemp> liveTemps = liveTable.get(instNode);
                        if(liveTemps != null)
                            for (NameOfTemp liveTemp : liveTable.get(instNode))
                                if (!instNode.elem.uses(liveTemp))
                                    addEdge(src, getNode(liveTemp));
                    } else {
                        ArrayList<NameOfTemp> liveTemps = liveTable.get(instNode);
                        if(liveTemps != null)
                            for (int i = 0; i < liveTemps.size(); i++)
                                addEdge(src, getNode(liveTemps.get(i)));
                    }
                }
            }
        }
    }
}
