package graph;

import java.util.ArrayList;

public class Node<T> {
    public T elem;

    public int inDegree;
    public int outDegree;

    public Node(T elem){
        this.elem = elem;
    }

    public ArrayList<Node<T>> succ = new ArrayList<>();
    public ArrayList<Node<T>> pred = new ArrayList<>();

    public ArrayList<Node<T>> adj(){
        final ArrayList<Node<T>> res = new ArrayList<>();
        res.addAll(succ);
        res.addAll(pred);
        return res;
    }
}
