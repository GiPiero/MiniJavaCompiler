package translate.ir;

import tree.*;
import translate.ir.IRTree;
import tree.NameOfLabel;
import tree.NameOfTemp;
import tree.RET;
import tree.SEQ;
import tree.LABEL;
import tree.MOVE;
import tree.TEMP;
import tree.JUMP;

public class IfThenElseIR extends IRTree {
    private final IRTree con, e2, e3;
    private final NameOfLabel t = NameOfLabel.generateLabel("t");
    private final NameOfLabel f = NameOfLabel.generateLabel("f");
    private final NameOfLabel join = NameOfLabel.generateLabel("join");

    public IfThenElseIR(IRTree con, IRTree e2, IRTree e3) {
        this.con = con;
        this.e2 = e2;
        this.e3 = e3;
    }

    public Exp asExp() {
        NameOfTemp r = NameOfTemp.generateTemp();
        return new RET(
            new SEQ(
                new SEQ(
                    new LABEL(t),
                    new MOVE(new TEMP(r), e2.asExp())
                ),
                new SEQ(
                    new LABEL(f),
                    new MOVE(new TEMP(r), e3.asExp())
                )
            ),
            new TEMP(r)
        );
    }

    // Will this be called?
    public Stm asStm() {
        return SEQ.fromList(
            con.asCon(t, f),
            SEQ.fromList(new LABEL(t), e2.asStm(), new JUMP(join)),
            SEQ.fromList(new LABEL(f), e3.asStm(), new JUMP(join)),
            new LABEL(join)
        );
    }

    // This should never be called
    public Stm asCon(NameOfLabel t, NameOfLabel f) {
        return null;
    }
}