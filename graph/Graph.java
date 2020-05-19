package graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

public class Graph<T> {
    //public HashSet<Node<T>> nodes = new HashSet<>();
    public ArrayList<Node<T>> nodes = new ArrayList<>();
    public Hashtable<T, Node<T>> nodeTable = new Hashtable<>();

    public Node<T> getNode(T elem) {
        if(nodeTable.containsKey(elem))
            return nodeTable.get(elem);

        Node<T> n = new Node<T>(elem);
        nodes.add(n);
        nodeTable.put(elem, n);
        return n;
    }
    public void addEdge(Node<T> src, Node<T> dst){
        if(src.succ != null && !src.succ.contains(dst)) {
            src.succ.add(dst);
            src.outDegree++;
        }
        if(dst.pred != null && !dst.pred.contains(src)){
            dst.pred.add(src);
            dst.inDegree++;
        }
    }
    public void remEdge(Node<T> src, Node<T> dst){
        if(src.succ != null && src.succ.contains(dst)){
            src.succ.remove(dst);
            src.outDegree--;
        }
        if(dst.pred != null && dst.pred.contains(src)) {
            dst.pred.remove(src);
            dst.inDegree--;
        }
    }
}
