package translate;

import frame.Frame;
import tree.Stm;

public class Fragment {
    public Frame frame;
    public Stm body;

    public Fragment(Frame frame, Stm body){
        this.frame = frame;
        this.body = body;
    }

    public void print(){
        System.out.println();
        System.out.println(body.toString());
    }
}