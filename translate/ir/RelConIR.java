package translate.ir;

import translate.ir.ConIR;
import tree.Exp;
import tree.Stm;
import tree.NameOfLabel;
import tree.CJUMP;

public class RelConIR extends ConIR {
    final private int relop;
    final private Exp left, right; // See slide 51

    public RelConIR (int op, Exp l, Exp r) {
        relop = op;
        left = l;
        right = r;
    }

    public Stm asCon(NameOfLabel t, NameOfLabel f) {
        return new CJUMP(relop, left, right, t, f);
    }
}