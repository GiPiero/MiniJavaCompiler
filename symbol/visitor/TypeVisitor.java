package symbol.visitor;

import syntax.*;
import symbol.SymbolTable;
import symbol.type.Method;
import symbol.type.ClassObject;
import symbol.type.SymbolType;
import symbol.type.Primitive;
import symbol.Binding;
import main.CompileError;

public class TypeVisitor implements SyntaxTreeVisitor<SymbolType> {
    private SymbolTable scope;
    public SymbolTable root;

    public TypeVisitor(SymbolTable root){
        this.root = root;
        scope = root;
    }

    public SymbolType visit (Program p)  {
        p.m.accept(this);
        for(int i = 0; i < p.cl.size(); i++)
            p.cl.get(i).accept(this);
        return null;
    }

    public SymbolType visit (MainClass m) {
        scope = scope.sub_table.get(m.i1.s);
        m.s.accept(this);
        scope = scope.parent;
        return null;
    }

    public SymbolType visit (SimpleClassDecl cd) {
        scope = scope.sub_table.get(cd.i.s);
        for(int i = 0; i < cd.ml.size(); i++)
            cd.ml.get(i).accept(this);
        scope = scope.parent;
        return null;
    }

    public SymbolType visit (ExtendingClassDecl ecd) {
        scope = scope.sub_table.get(ecd.i.s);
        for(int i = 0; i < ecd.ml.size(); i++)
            ecd.ml.get(i).accept(this);
        scope = scope.parent;
        return null;
    }

    public SymbolType visit (VarDecl n) { return null; }

    public SymbolType visit (MethodDecl md) {
        // Replace with existence check
        if(scope.getBinding(md.i.s).type.isSame(Primitive.UNDEFINED))
            return null;

        Method m = (Method) scope.getBinding(md.i.s).type;
        scope = scope.sub_table.get(md.i.s);

        for(int i = 0; i < md.sl.size(); i++)
            md.sl.get(i).accept(this);

        SymbolType t = md.e.accept(this);

        if(!m.return_type.canBeAssignedBy(t))
            CompileError.printError(md.e.lineNumber, md.e.columnNumber,
                    "Error: return type does not match");

        scope = scope.parent;
        return null;
    }

    public SymbolType visit (Formal n) { return null; }
    public SymbolType visit (IdentifierType n) { return null; }
    public SymbolType visit (IntArrayType n) { return null; }
    public SymbolType visit (BooleanType n) { return null; }
    public SymbolType visit (IntegerType n) { return null; }
    public SymbolType visit (VoidType n) { return null; }

    public SymbolType visit (Block b) {
        for(int i = 0; i < b.sl.size(); i++)
            b.sl.get(i).accept(this);
        return null;
    }

    public SymbolType visit (If f) {
        SymbolType t = f.e.accept(this);

        if(!t.isSame(Primitive.BOOLEAN))
            CompileError.printError(f.e.lineNumber, f.e.columnNumber,
                    "Error: conditional expression must result in boolean value.");

        f.s1.accept(this);
        f.s2.accept(this);
        return null;
    }

    public SymbolType visit (While w) {
        SymbolType t = w.e.accept(this);

        if(!t.isSame(Primitive.BOOLEAN))
            CompileError.printError(w.e.lineNumber, w.e.columnNumber,
                    "Error: conditional expression must result in boolean value.");

        w.s.accept(this);
        return null;
    }

    public SymbolType visit (Print p) {
        SymbolType t = p.e.accept(this);

        if(t.isSame(Primitive.UNDEFINED))
            CompileError.printError(p.e.lineNumber, p.e.columnNumber,
                    "Error: print expression is undefined.");

        return null;
    }

    public SymbolType visit (Assign a) {
        SymbolType et = a.e.accept(this);
        SymbolType it = scope.getBinding(a.i.s).type;

        if(it.isSame(Primitive.UNDEFINED)){
            CompileError.printError(a.i.lineNumber, a.i.columnNumber,
                    String.format("Error: %s is not defined", a.i.s));
        } else if(!it.canBeAssignedBy(et))
            CompileError.printError(a.i.lineNumber, a.i.columnNumber,
                    "Error: assignment type mismatch.");
        return null;
    }

    public SymbolType visit (ArrayAssign aa) {
        SymbolType t = aa.indexInArray.accept(this);

        if(!t.isSame(Primitive.INT))
            CompileError.printError(aa.indexInArray.lineNumber, aa.indexInArray.columnNumber,
                    "Error: array index must be an integer.");

        t = aa.e.accept(this);

        if(!t.isSame(Primitive.INT))
            CompileError.printError(aa.e.lineNumber, aa.e.columnNumber,
                    "Error: type mismatch in array assignment expression, must be integer.");

        return null;
    }

    public SymbolType visit (And a) {
        SymbolType t1 = a.e1.accept(this);
        SymbolType t2 = a.e2.accept(this);

        if(!t1.isSame(Primitive.BOOLEAN))
            CompileError.printError(a.e1.lineNumber, a.e1.columnNumber,
                    "Error: expression must result in a boolean value.");

        if(!t2.isSame(Primitive.BOOLEAN))
            CompileError.printError(a.e2.lineNumber, a.e2.columnNumber,
                    "Error: expression must result in a boolean value.");

        if(t1.isSame(Primitive.BOOLEAN) && t2.isSame(Primitive.BOOLEAN))
            return Primitive.BOOLEAN;

        return Primitive.UNDEFINED;
    }

    public SymbolType visit (LessThan lt) {
        SymbolType t1 = lt.e1.accept(this);
        SymbolType t2 = lt.e2.accept(this);

        if(!t1.isSame(Primitive.INT))
            CompileError.printError(lt.e1.lineNumber, lt.e1.columnNumber,
                    "Error: expression must result in a integer value.");

        if(!t2.isSame(Primitive.INT))
            CompileError.printError(lt.e2.lineNumber, lt.e2.columnNumber,
                    "Error: expression must result in a integer value.");

        if(t1.isSame(Primitive.INT) && t2.isSame(Primitive.INT))
            return Primitive.BOOLEAN;

        return Primitive.UNDEFINED;
    }

    public SymbolType visit (Plus p) {
        SymbolType t1 = p.e1.accept(this);
        SymbolType t2 = p.e2.accept(this);

        if(!t1.isSame(Primitive.INT))
            CompileError.printError(p.e1.lineNumber, p.e1.columnNumber,
                    "Error: expression must result in a integer value.");

        if(!t2.isSame(Primitive.INT))
            CompileError.printError(p.e2.lineNumber, p.e2.columnNumber,
                    "Error: expression must result in a integer value.");

        if(t1.isSame(Primitive.INT) && t2.isSame(Primitive.INT))
            return Primitive.INT;

        return Primitive.UNDEFINED;
    }

    public SymbolType visit (Minus m) {
        SymbolType t1 = m.e1.accept(this);
        SymbolType t2 = m.e2.accept(this);

        if(!t1.isSame(Primitive.INT))
            CompileError.printError(m.e1.lineNumber, m.e1.columnNumber,
                    "Error: expression must result in a integer value.");

        if(!t2.isSame(Primitive.INT))
            CompileError.printError(m.e2.lineNumber, m.e2.columnNumber,
                    "Error: expression must result in a integer value.");

        if(t1.isSame(Primitive.INT) && t2.isSame(Primitive.INT))
            return Primitive.INT;

        return Primitive.UNDEFINED;
    }

    public SymbolType visit (Times t) {
        SymbolType t1 = t.e1.accept(this);
        SymbolType t2 = t.e2.accept(this);

        if(!t1.isSame(Primitive.INT))
            CompileError.printError(t.e1.lineNumber, t.e1.columnNumber,
                    "Error: expression must result in a integer value.");

        if(!t2.isSame(Primitive.INT))
            CompileError.printError(t.e2.lineNumber, t.e2.columnNumber,
                    "Error: expression must result in a integer value.");

        if(t1.isSame(Primitive.INT) && t2.isSame(Primitive.INT))
            return Primitive.INT;

        return Primitive.UNDEFINED;
    }

    public SymbolType visit (ArrayLookup al) {
        SymbolType t = al.expressionForArray.accept(this);
        SymbolType rt = Primitive.INT;

        if(!t.isSame(Primitive.INTARRAY)) {
            rt = Primitive.UNDEFINED;
            CompileError.printError(al.expressionForArray.lineNumber,
                    al.expressionForArray.columnNumber,
                    "Error: expression is not an array.");
        }

        t = al.indexInArray.accept(this);
        if(!t.isSame(Primitive.INT)) {
            rt = Primitive.UNDEFINED;
            CompileError.printError(al.indexInArray.lineNumber,
                    al.indexInArray.columnNumber,
                    "Error: index expression must result in an integer.");
        }

        return rt;
    }

    public SymbolType visit (ArrayLength al) {
        SymbolType t = al.expressionForArray.accept(this);

        if(!t.isSame(Primitive.INTARRAY)) {
            CompileError.printError(al.expressionForArray.lineNumber,
                    al.expressionForArray.columnNumber,
                    "Error: expression is not an array.");
            return Primitive.UNDEFINED;
        }
        return Primitive.INT;
    }

    public SymbolType visit (Call c) {
        SymbolType t = c.e.accept(this);
        SymbolType rt;

        if(!(t instanceof ClassObject)){
            rt = Primitive.UNDEFINED;
            CompileError.printError(c.e.lineNumber, c.e.columnNumber,
                    "Error: expression is not a class");
            //return Primitive.UNDEFINED; // TODO: this is a hack. remove and fix
        }

        SymbolTable ct = root.sub_table.get(((ClassObject) t).id);
        Binding mb = ct.getBinding(c.i.s);
        rt = mb.type;

        if(mb.type.isSame(Primitive.UNDEFINED)){
            rt = Primitive.UNDEFINED;
            CompileError.printError(c.i.lineNumber, c.i.columnNumber,
                    String.format("Error: class does not have method %s defined.", c.i.s));
        }

        if(!(mb.type instanceof Method)) {
            rt = Primitive.UNDEFINED;
            CompileError.printError(c.i.lineNumber, c.i.columnNumber,
                    String.format("Error: %s is not a method.", c.i.s));
        }

        Method m = ((Method) mb.type);
        rt = m.return_type;
        if(m.param_types.size() != c.el.size()) {
            CompileError.printError(c.i.lineNumber, c.i.columnNumber,
                    String.format("Error: invalid number of arguments to method %s.", c.i.s));
            return rt;
        }

        for(int i = 0; i < c.el.size(); i++){
            t = c.el.get(i).accept(this);

            if(!m.param_types.get(i).canBeAssignedBy(t))
                CompileError.printError(c.el.get(i).lineNumber, c.el.get(i).columnNumber,
                        String.format("Error: type mismatch for argument %d.", i));
        }
        return rt;
    }

    public SymbolType visit (IntegerLiteral n) { return Primitive.INT; }
    public SymbolType visit (True n) { return Primitive.BOOLEAN; }
    public SymbolType visit (False n) { return Primitive.BOOLEAN; }

    public SymbolType visit (IdentifierExp ie) {
        Binding b = scope.getBinding(ie.s);

        if(b.type.isSame(Primitive.UNDEFINED))
            CompileError.printError(ie.lineNumber, ie.columnNumber,
                    String.format("Error: identifier %s has not been declared.", ie.s));

        return b.type;
    }

    public SymbolType visit (This n) {
        SymbolTable restore_scope = scope;
        while(scope.parent != null) {
            SymbolType t = scope.parent.table.get(scope.id).type;
            if(t instanceof ClassObject){
                scope = restore_scope;
                return t;
            }
            scope = scope.parent;
        }
        scope = restore_scope;
        return Primitive.UNDEFINED; // This shouldn't be reachable
    }

    public SymbolType visit (NewArray na) {
        SymbolType t = na.e.accept(this);
        if(!t.isSame(Primitive.INT))
            CompileError.printError(na.e.lineNumber, na.e.columnNumber,
                    "Error: array size must evaluate to an integer.");

        return Primitive.INTARRAY;
    }

    public SymbolType visit (NewObject n) {
        Binding b = root.getBinding(n.i.s);

        if(!(b.type instanceof ClassObject)) {
            CompileError.printError(n.i.lineNumber, n.i.columnNumber,
                    String.format("Error: identifier %s is not a class.", n.i.s));
            return Primitive.UNDEFINED;
        }

        return b.type;
    }

    public SymbolType visit (Not n) {
        SymbolType t = n.e.accept(this);

        if(!t.isSame(Primitive.BOOLEAN)) {
            CompileError.printError(n.e.lineNumber, n.e.columnNumber,
                    "Error: expression must evaluate to boolean");
            return Primitive.UNDEFINED;
        }
        return Primitive.BOOLEAN;
    }

    public SymbolType visit (Identifier n) { return null; }
}
