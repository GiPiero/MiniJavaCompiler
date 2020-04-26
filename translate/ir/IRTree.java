package translate.ir;

import tree.NameOfLabel;
import tree.Exp;
import tree.Stm;

public abstract class IRTree {
    public abstract Exp asExp();
    public abstract Stm asStm();
    public Stm asCon(NameOfLabel t, NameOfLabel f) {
        throw new UnsupportedOperationException("ERROR");
    }
    public String toString() {
        return String.format("IR: %s", asStm().toString());
    }
}