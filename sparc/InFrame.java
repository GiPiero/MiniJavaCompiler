package sparc;

import frame.Access;
import tree.Exp;
import tree.MEM;
import tree.BINOP;
import tree.CONST;

public class InFrame extends Access {
    public int offset;

    public InFrame(int offset){ this.offset = offset; }
    
    public Exp access(Exp framePointer){
        return new MEM(
            new BINOP(
                BINOP.PLUS,
                framePointer,
                new CONST(offset)
            )
        );
    }
}