package sparc;

import frame.Access;
import tree.NameOfTemp;
import tree.TEMP;
import tree.Exp;

public class InReg extends Access {
    public NameOfTemp t;

    public InReg(NameOfTemp t) { this.t = t; }
    public Exp access(Exp framePointer){
        return new TEMP(t);
    }

}