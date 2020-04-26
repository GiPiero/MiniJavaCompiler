package translate.ir;

import translate.ir.IRTree;
import tree.Exp;
import tree.Stm;
import tree.NameOfTemp;
import tree.NameOfLabel;
import tree.RET;
import tree.SEQ;
import tree.MOVE;
import tree.TEMP;
import tree.CONST;
import tree.LABEL;

public abstract class ConIR extends IRTree {
    public Exp asExp() {
        final NameOfTemp r = NameOfTemp.generateTemp();
        final NameOfLabel t = NameOfLabel.generateLabel();
        final NameOfLabel f = NameOfLabel.generateLabel();

        return new RET(
            SEQ.fromList(
                new MOVE(new TEMP(r), CONST.TRUE),
                asCon(t,f),
                new LABEL(f),
                new MOVE(new TEMP(r), CONST.FALSE),
                new LABEL(t)),
            new TEMP(r));
    }
    public Stm asStm() { 
        final NameOfLabel t = NameOfLabel.generateLabel();
        final NameOfLabel f = NameOfLabel.generateLabel();

        return SEQ.fromList(
            asCon(t,f),
            new LABEL(t), 
            new LABEL(f));
    }
    public abstract Stm asCon(NameOfLabel t, NameOfLabel f);
}