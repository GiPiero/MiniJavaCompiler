package translate;

import frame.Frame;
import tree.Stm;

public class Fragment {
    private Frame frame;
    private Stm body;

    public Fragment(Frame frame, Stm body){
        this.frame = frame;
        this.body = body;
    }

    public void print(){
        System.out.println(frame.inst_ptr.toString());
        System.out.println(body.toString());
    }
}