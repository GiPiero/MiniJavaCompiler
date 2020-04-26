package translate.ir;

import translate.ir.ConIR;
import translate.ir.IRTree;
import tree.NameOfLabel;
import tree.Stm;
import tree.SEQ;
import tree.CJUMP;
import tree.BINOP;
import tree.CONST;
import tree.LABEL;

public class AndConIR extends ConIR {
    final IRTree e1, e2; // Switch to ExpIR?

    public AndConIR(IRTree e1, IRTree e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    public Stm asCon(NameOfLabel t, NameOfLabel f) {
        NameOfLabel test = NameOfLabel.generateLabel();

        return SEQ.fromList(
            new CJUMP(BINOP.AND, e1.asExp(), CONST.TRUE, test, f),
            new LABEL(test),
            e2.asCon(t, f));
    } 
}