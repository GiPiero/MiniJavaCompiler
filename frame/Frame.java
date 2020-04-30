package frame;

import assem.Instruction;
import frame.Access;
import tree.Stm;
import tree.NameOfTemp;
import tree.NameOfLabel;
import java.util.ArrayList;
public abstract class Frame {
    public final NameOfLabel inst_ptr;
    public final ArrayList<Access> formals;
    
    public Frame(NameOfLabel i, ArrayList<Access> f) { inst_ptr = i; formals = f; }
    public abstract Frame newFrame(NameOfLabel name, ArrayList<Boolean> formals);
    public abstract Access allocLocal(boolean escapes);
    public abstract NameOfTemp fp();
    public abstract NameOfTemp rv();
    public abstract int wordSize();
    public abstract Stm procEntryExit1(Stm body);
    public abstract ArrayList<Instruction> procEntryExit3(ArrayList<Instruction> body);

    public NameOfLabel epilogueLabel; // this is a dirty hack probably
}