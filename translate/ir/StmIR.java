package translate.ir;

import translate.ir.IRTree;
import tree.Stm;
import tree.Exp;
import tree.NameOfLabel;

public class StmIR extends IRTree {
    private final Stm stm;
    public StmIR(Stm s) { stm = s; }
    public Stm asStm(){ return stm; }
    
    // These shouldn't be called
    public Stm asCon(NameOfLabel t, NameOfLabel f) { return null; }
    public Exp asExp() { return null; }
}