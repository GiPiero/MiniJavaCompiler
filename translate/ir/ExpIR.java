package translate.ir;

import translate.ir.IRTree;
import tree.*;


public class ExpIR extends IRTree {
    private final Exp exp;
    public ExpIR(Exp e) { exp = e; }
    public Exp asExp() { return exp; }
    public Stm asStm() { return new EVAL(exp);}

    public Stm asCon(NameOfLabel t, NameOfLabel f) {
        Exp exp = this.asExp();
        return new CJUMP(CJUMP.EQ, exp, CONST.TRUE, t, f);
    }
}