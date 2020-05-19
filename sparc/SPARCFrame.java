package sparc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import assem.Instruction;
import assem.LabelInstruction;
import assem.OperationInstruction;
import frame.Access;
import tree.NameOfLabel;
import tree.NameOfTemp;
import tree.Stm;
import tree.Exp;
import tree.TEMP;
import tree.SEQ;
import tree.TEMP;
import tree.MOVE;
import tree.LABEL;
import tree.JUMP;

public class SPARCFrame extends frame.Frame {
    // SPARC registers
    static final NameOfTemp [] globalRegs = new NameOfTemp[8];
    static final NameOfTemp [] inRegs = new NameOfTemp[8];
    static final NameOfTemp [] localRegs = new NameOfTemp[8];
    static final NameOfTemp [] outRegs = new NameOfTemp[8];
    static {
        for(int i = 0; i < 8; i++) {
            localRegs[i] = new NameOfTemp("l" + i);
            inRegs[i] = new NameOfTemp("i" + i);
            outRegs[i] = new NameOfTemp("o" + i);
            globalRegs[i] = new NameOfTemp("g" + i);
        }
    }

    static final NameOfTemp [] inArgs = new NameOfTemp[6];
    static final NameOfTemp [] outArgs = new NameOfTemp[6];
    static final NameOfTemp [] calleeSaves = new NameOfTemp[7];
    static {
        for(int i = 0; i < 7; i++)
            calleeSaves[i] = globalRegs[i+1];
        for(int i = 0; i < 6; i++) {
            inArgs[i] = inRegs[i];
            outArgs[i] = outRegs[i];
        }
    }

    // References to special registers
    static final NameOfTemp fpReg = new NameOfTemp("fp");//= inRegs[6];
    static final NameOfTemp spReg = new NameOfTemp("sp");//= outRegs[6];
    static final NameOfTemp zeroReg = globalRegs[0];

    public SPARCFrame(NameOfLabel l, ArrayList<Access> f) {
        super(l, f);
        epilogueLabel = NameOfLabel.generateLabel("end"); // this is a dirty hack probably
        for(int i = 0; i < 8; i++){
            tempMap.put(globalRegs[i], "%g" + i);
            tempMap.put(localRegs[i], "%l" + i);
            tempMap.put(inRegs[i], "%i" + i);
            tempMap.put(outRegs[i], "%o" + i);
        }
        tempMap.put(fpReg, "%fp");
        tempMap.put(spReg, "%sp");
    }

    public frame.Frame newFrame(NameOfLabel name, ArrayList<Boolean> formals){
        ArrayList<Access> formalAccesses = new ArrayList<Access>();
        for(int i = 0; i < formals.size(); i++){
            if(formals.get(i))
                System.err.println("ERROR:newFrame: Formal cannot escape.");
            if(i >= 6)
                System.err.println("ERROR:newFrame: Cannot have more than 6 formals.");
            else
                formalAccesses.add(new InReg(inArgs[i]));
        }
        return new SPARCFrame(name, formalAccesses);
    }

    int regLocals = 0;
    int frameLocals = 0;
    public Access allocLocal(boolean escapes) {
        if(escapes || regLocals > 7)
            return new InFrame(-wordSize()*(++frameLocals));

        return new InReg(localRegs[regLocals++]);
    }

    public NameOfTemp fp() { return fpReg; }
    public NameOfTemp rv() {return inRegs[0]; }
    public NameOfTemp RVSet() { return outRegs[0]; }
    public int wordSize() { return 4; }

    public Stm procEntryExit1(Stm body) {
//        for(int i = 0; i < calleeSaves.length; i++){
//            Exp r = new TEMP(calleeSaves[i]);
//            Exp ptrExp = allocLocal(true).access(new TEMP(fp()));
//            body = new SEQ(new MOVE(ptrExp, r), new SEQ(body, new MOVE(r, ptrExp)));
//        }
        return new SEQ(new SEQ(new LABEL(inst_ptr), body), new JUMP(epilogueLabel));
    }

    public ArrayList<Instruction> procEntryExit3(ArrayList<Instruction> body){
        int stack_size = 96 + ((frameLocals + 1) * wordSize());
        stack_size += 8 - (stack_size % 8);
        body.add(1,
                new OperationInstruction("\tsave\t`s0, -"+stack_size+", `d0",
                        spReg, spReg));
        body.add(new LabelInstruction(epilogueLabel));
        if(inst_ptr.toString().equals("main")) {
            body.add(new OperationInstruction("\tclr\t%o0"));
            body.add(new OperationInstruction("\tmov\t1, %g1"));
            body.add(new OperationInstruction("\tta\t0x90"));
        }
        //else{
            body.add(new OperationInstruction("\tret"));
            body.add(new OperationInstruction("\trestore"));
        //}
        return body;
    }
}