package graph;
import assem.Instruction;
import assem.LabelInstruction;
import sparc.SPARCFrame;
import tree.NameOfLabel;
import tree.NameOfTemp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class FlowGraph extends Graph<Instruction> {
    private Hashtable<NameOfLabel, Node<Instruction>> labelTable = new Hashtable<>();
    private Hashtable<Node<Instruction>, ArrayList<NameOfLabel>> unresolvedJumps = new Hashtable<>();

    public List<NameOfTemp> def(Node<Instruction> n){
        if(n.elem.def() == null) return new ArrayList<>();
        return n.elem.def();
    }

    public List<NameOfTemp> use(Node<Instruction> n){
        if(n.elem.use() == null) return new ArrayList<>();
        return n.elem.use();
    }

    public boolean isMove(Node<Instruction> n){
        return n.elem.isMove();
    }

    public FlowGraph(List<Instruction> instList){
        Node<Instruction> prevNode = null;

        for(Instruction inst : instList)
            if(inst.isLabel())
                if(!labelTable.containsKey(((LabelInstruction) inst).label))
                    labelTable.put(((LabelInstruction) inst).label, getNode(inst));

        for(Instruction inst : instList) {
            Node<Instruction> n = getNode(inst);

            // If this instruction falls through to the next, add an edge
            if(prevNode != null) addEdge(prevNode, n);

            // If it is a label, remember it for when jumps are resolved
            if(inst.isLabel())
                if(!labelTable.containsKey(((LabelInstruction) inst).label))
                    labelTable.put(((LabelInstruction) inst).label, n);

            // If this instruction has jumps add edges or remember them for later resolution
            if(!inst.assem.equals("\tcall `j0")  // This condition is a dirty dirty hack.
                && inst.jumps() != null && !inst.jumps().isEmpty()) {
                for (NameOfLabel j : inst.jumps()) {
                    if (!labelTable.containsKey(j)) {
                        System.err.println("Unresolved label.");
                    }
                    else addEdge(n, labelTable.get(j));
                }
                prevNode = null; // Make sure this instruction does not fall through
            } else
                prevNode = n;
        }

        // // Resolve jumps
        // for(Node<Instruction> n : unresolvedJumps.keySet())
        //     for(NameOfLabel j : unresolvedJumps.get(n))
        //         if (labelTable.containsKey(j))
        //             addEdge(n, labelTable.get(j));
    }
}
