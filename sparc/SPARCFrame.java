package sparc;

import java.util.ArrayList;
import frame.Access;
import tree.NameOfLabel;
import tree.NameOfTemp;
import tree.Stm;

public class SPARCFrame extends frame.Frame {
    static final NameOfTemp framePointer = NameOfTemp.generateTemp();
    static final NameOfTemp [] formalRegs = new NameOfTemp[6];

    static {
        for(int i = 0; i < formalRegs.length; i++)
            formalRegs[i] = NameOfTemp.generateTemp();
    }

    public SPARCFrame(NameOfLabel i, ArrayList<Access> f) { super(i, f); }

    public frame.Frame newFrame(NameOfLabel name, ArrayList<Boolean> formals){
        ArrayList<Access> formalAccesses = new ArrayList<Access>();
        for(int i = 0; i < formals.size(); i++){
            if(formals.get(i))
                System.err.println("ERROR:newFrame: Formal cannot escape.");
            if(i >= 6)
                System.err.println("ERROR:newFrame: Cannot have more than 6 formals.");

            formalAccesses.add(new InReg(formalRegs[i]));
        }
        return new SPARCFrame(name, formalAccesses);
    }
    int numlocals = 0;
    public Access allocLocal(boolean escapes) {
        if(escapes)
            return new InFrame(-wordSize()*(++numlocals));

        return new InReg(NameOfTemp.generateTemp());
    }
    public NameOfTemp fp() { return framePointer; }
    public NameOfTemp rv() { return formalRegs[0]; };
    public int wordSize() { return 4; }
    public Stm procEntryExit1(Stm body){
        return body;
    }
}