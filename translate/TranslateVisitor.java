package translate;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;

import frame.Access;
import sparc.InFrame;
import symbol.SymbolTable;
import symbol.type.ClassObject;
import symbol.type.Method;
import symbol.type.SymbolType;
import translate.ir.*;
import frame.Frame;
import syntax.*;
import tree.*;

public class TranslateVisitor implements SyntaxTreeVisitor<IRTree>{
    private static final NAME new_obj = new NAME(new NameOfLabel("new_object"));
    private static final NAME new_array = new NAME(new NameOfLabel("new_array"));
    private static final NAME print_int = new NAME(new NameOfLabel("print_int"));
    private final Hashtable<String, NameOfLabel> names = new Hashtable<String, NameOfLabel>();
    private final Frame factory;
    private final SymbolTable root;
    private SymbolTable scope;
    private ClassObject currClass;
    private Method currMethod;
    private Frame currFrame;

    public ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    /* Helper functions */
    //private static IRTree plus
    //private static IRTree mul (def do this)
    private void addFragment(Frame frame, IRTree body){
        fragments.add(new Fragment(frame, frame.procEntryExit1(body.asStm())));
    }

    private Exp varAccess(String id, Frame frame) {
        if(currMethod.accesses.containsKey(id))
            return currMethod.accesses.get(id).access(new TEMP(currFrame.fp()));

        if(currClass.table.table.containsKey(id)){
            int offset = currClass.table.table.get(id).offset;
            return new MEM(
                    new BINOP(
                            BINOP.PLUS,
                            currFrame.formals.get(0).access(new TEMP(currFrame.fp())),
                            new CONST(offset)
                    )
            );
        }
        return null;
    }

    /* Visitor functions */
    public TranslateVisitor(SymbolTable table, Frame factory) {
        root = table;
        scope = table;
        this.factory = factory;
    }

    public IRTree visit (Program p){
        for(int i = 0; i < p.cl.size(); i++)
            p.cl.get(i).accept(this);

        p.m.accept(this);
        return null;
    }

    public IRTree visit (MainClass m){
        currClass= (ClassObject) scope.table.get(m.i1.s).type;
        scope = scope.sub_table.get(m.i1.s);
        Frame newFrame = factory.newFrame(
                new NameOfLabel("main"),
                new ArrayList());
        addFragment(
                newFrame,
                new StmIR(new SEQ(new SEQ(new LABEL("main"), m.s.accept(this).asStm()), new JUMP(newFrame.epilogueLabel))));
        scope = scope.parent;
        currClass = null;
        return null;
    }
    public IRTree visit (SimpleClassDecl cd){
        currClass= (ClassObject) scope.table.get(cd.i.s).type;
        scope = scope.sub_table.get(cd.i.s);

        for(int i = 0; i < cd.vl.size(); i++)
            cd.vl.get(i).accept(this);

        for(int i = 0; i < cd.ml.size(); i++)
            cd.ml.get(i).accept(this);

        scope = scope.parent;
        currClass = null;
        return null;
    }

    public IRTree visit (ExtendingClassDecl ecd){
        currClass= (ClassObject) scope.table.get(ecd.i.s).type;
        scope = scope.sub_table.get(ecd.i.s);

        for(int i = 0; i < ecd.vl.size(); i++)
            ecd.vl.get(i).accept(this);
        for(int i = 0; i < ecd.ml.size(); i++)
            ecd.ml.get(i).accept(this);

        scope = scope.parent;
        currClass = null;
        return null;
    }

    public IRTree visit (VarDecl n){ return null; }

    public IRTree visit (MethodDecl n){
        ArrayList<Boolean> params = new ArrayList<Boolean>();
        params.add(false);

        currMethod = (Method) scope.table.get(n.i.s).type;

        for(Formal f : n.fl) {
            params.add(false);
        }

        NameOfLabel name = new NameOfLabel(scope.id, n.i.s);
        Frame newFrame = factory.newFrame(name, params);
        currFrame = newFrame;
        currMethod.accesses.put("this", newFrame.formals.get(0));
        for(int i = 0; i < n.fl.size(); i++)
            currMethod.accesses.put(n.fl.get(i).i.s, newFrame.formals.get(i+1));

        scope = scope.sub_table.get(n.i.s);

        for(VarDecl v : n.vl)
            currMethod.accesses.put(v.i.s, newFrame.allocLocal(false));

        Exp retExp;
        if(n.sl.size() > 0){
            Stm stm = n.sl.get(0).accept(this).asStm();
            for(int i = 1; i < n.sl.size(); i++)
                stm = new SEQ(stm, n.sl.get(i).accept(this).asStm());
            retExp = new RET(stm, n.e.accept(this).asExp());
        }
        else retExp = n.e.accept(this).asExp();

        fragments.add(new Fragment(newFrame, newFrame.procEntryExit1(
                new SEQ(new SEQ(new LABEL(name), new MOVE(new TEMP(newFrame.rv()), retExp)), new JUMP(newFrame.epilogueLabel)))));
        scope = scope.parent;
        currMethod = null;
        currFrame = null;
        return null;
    }

    public IRTree visit (Formal n){ return null; }
    public IRTree visit (IdentifierType n){ return null; }
    public IRTree visit (IntArrayType n){ return null; }
    public IRTree visit (BooleanType n){ return null; }
    public IRTree visit (IntegerType n){ return null; }
    public IRTree visit (VoidType n){ return null; }

    public IRTree visit (Block n){
        Stm stm = new ExpIR(CONST.ZERO).asStm(); // NOP if block is empty

        if(n.sl.size() > 0) {
            stm = n.sl.get(0).accept(this).asStm();
            for(int i = 1; i < n.sl.size(); i++)
                stm = new SEQ(stm, n.sl.get(i).accept(this).asStm());
        }

        return new StmIR(stm);
    }

    public IRTree visit (If n){
        return new IfThenElseIR(
                n.e.accept(this),
                n.s1.accept(this),
                n.s2.accept(this));
    }

    public IRTree visit (While n){
        NameOfLabel cond = NameOfLabel.generateLabel("cond");
        NameOfLabel body = NameOfLabel.generateLabel("body");
        NameOfLabel join = NameOfLabel.generateLabel("join");

        return new StmIR(SEQ.fromList(
                new LABEL(cond),
                n.e.accept(this).asCon(body, join),
                new LABEL(body),
                n.s.accept(this).asStm(),
                new JUMP(cond),
                new LABEL(join)));
    }

    public IRTree visit (Print n){
        return new StmIR( new ExpIR(new CALL(print_int, n.e.accept(this).asExp())).asStm());
    }
    public IRTree visit (Assign n){
        return new StmIR(new MOVE(varAccess(n.i.s, currFrame), n.e.accept(this).asExp()));
    }
    public IRTree visit (ArrayAssign n){
        return new StmIR (new MOVE(
            new BINOP(
                BINOP.PLUS,
                new BINOP(
                    BINOP.MUL,
                    new BINOP(BINOP.PLUS, CONST.ONE, n.indexInArray.accept(this).asExp()),
                    new CONST(factory.wordSize())),
                varAccess(n.nameOfArray.s, currFrame)),
            n.e.accept(this).asExp()));
    }

    public IRTree visit (And n){
        return new AndConIR(
                new ExpIR(n.e1.accept(this).asExp()),
                new ExpIR(n.e2.accept(this).asExp()));
    }

    public IRTree visit (LessThan n){
        return new RelConIR(
                CJUMP.LT,
                n.e1.accept(this).asExp(),
                n.e2.accept(this).asExp());
    }

    public IRTree visit (Plus n){
        return new ExpIR(new BINOP(
                BINOP.PLUS,
                n.e1.accept(this).asExp(),
                n.e2.accept(this).asExp()));
    }

    public IRTree visit (Minus n){
        return new ExpIR(new BINOP(
                BINOP.MINUS,
                n.e1.accept(this).asExp(),
                n.e2.accept(this).asExp()));
    }

    public IRTree visit (Times n){
        return new ExpIR(new BINOP(
                BINOP.MUL,
                n.e1.accept(this).asExp(),
                n.e2.accept(this).asExp()));
    }

    public IRTree visit (ArrayLookup n){
        return new ExpIR(new MEM(new BINOP(
                BINOP.PLUS,
                n.expressionForArray.accept(this).asExp(),
                new BINOP(
                    BINOP.MUL,
                    new CONST(factory.wordSize()),
                    new BINOP(
                        BINOP.PLUS,
                        CONST.ONE,
                        n.indexInArray.accept(this).asExp())))));
    }

    public IRTree visit (ArrayLength n){
        return new ExpIR(new MEM(n.expressionForArray.accept(this).asExp()));
    }

    public IRTree visit (Call n){
        ArrayList<Exp> params = new ArrayList<Exp>();
        params.add(n.e.accept(this).asExp());
        for(int i = 0; i < n.el.size(); i++)
            params.add(n.el.get(i).accept(this).asExp());

        return new ExpIR(new CALL(new NAME(new NameOfLabel(n.getReceiverClassName(), n.i.s)), params));
    }

    public IRTree visit (IntegerLiteral n){ return new ExpIR(new CONST(n.i)); }
    public IRTree visit (True n){ return new ExpIR(CONST.TRUE);  }
    public IRTree visit (False n){ return new ExpIR(CONST.FALSE); }

    public IRTree visit (IdentifierExp n){
        return new ExpIR(varAccess(n.s, currFrame));
    }

    public IRTree visit (This n){
        return new ExpIR(currFrame.formals.get(0).access(new TEMP(currFrame.fp())));
    }

    public IRTree visit (NewArray n){
        TEMP retTemp = new TEMP(NameOfTemp.generateTemp());

        // External call to the runtime library function "new_array"
        return new ExpIR( new RET(
            new MOVE(retTemp, new CALL(new_array, n.e.accept(this).asExp(), new CONST(factory.wordSize()))),
            retTemp));

        // Runtime library function for allocating a new array is as follows:
        /*void *new_array(int count, int size){
            void *ret = calloc(count + 1, size);
            ((int *) ret)[0] = count;
            return ret;
        }*/
    }

    public IRTree visit (NewObject n){
        int size = ((ClassObject) root.table.get(n.i.s).type).size * factory.wordSize();
        TEMP retTemp = new TEMP(NameOfTemp.generateTemp());

        // External call to the runtime library function "new_obj"
        return new ExpIR(new RET(
            new MOVE(retTemp, new CALL(new_obj, new CONST(size))),
            retTemp));

        // Runtime library function for allocating a new object is as follows:
        /*void *new_obj(int size){
            return memset(malloc(size), 0, size);
        }*/
    }

    public IRTree visit (Not n){
        return new ExpIR(new BINOP(BINOP.MINUS, CONST.ONE, n.e.accept(this).asExp()));
    }
    public IRTree visit (Identifier n){
        return new ExpIR(varAccess(n.s, currFrame));
    }
}
