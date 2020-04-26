package symbol.visitor;

import symbol.type.ClassObject;
import syntax.*;
import main.CompileError;
import symbol.SymbolTable;
import symbol.Binding;
import symbol.type.Primitive;
import symbol.type.SymbolType;
import symbol.type.Method;
import java.util.ArrayList;

public class ClassVisitor implements SyntaxTreeVisitor<Void> {
    private SymbolTable scope;
    public SymbolTable root;

    public ClassVisitor(SymbolTable root) {
        this.scope = root;
        this.root = root;
    }

    private SymbolType ConvertType(Type t, String id) {
        // TODO: getBinding will add an undefined entry to root's symbol table if t.s doesn't exist, is that ok?
        if(t instanceof IdentifierType) return root.getBinding(((IdentifierType) t).s).type;
        else if(t instanceof IntArrayType) return Primitive.INTARRAY;
        else if(t instanceof BooleanType) return Primitive.BOOLEAN;
        else if(t instanceof IntegerType) return Primitive.INT;
        //else if(t instanceof VoidType) return Primitive.VOID;
        return null;
    }

    public Void visit (Program p)  {
        p.m.accept(this);
        for(int i = 0; i < p.cl.size(); i++)
            p.cl.get(i).accept(this);
        return null;
    }

    public Void visit (MainClass mc) {
        scope = scope.sub_table.get(mc.i1.s);

        SymbolTable ct = scope; //((ClassObject) scope.getBinding(m.i1.s).type).table;
        SymbolTable mt = new SymbolTable(ct, "main");
        Method m = new Method("main", Primitive.VOID, new ArrayList<SymbolType>()/*null?*/);

        // Add main method binding and main method symboltable to main class table
        if(!ct.addBinding("main", new Binding(ct.id, m.id, m)) || !ct.addTable("main", mt))
            CompileError.printError(mc.i1.lineNumber, mc.i1.columnNumber,
                    "Error: method main is already defined.");

        // Add main arguments to main method symboltable
        Binding b = new Binding(mt.id, mc.i2.s, Primitive.STRINGARRAY/*null?*/);
        if(!mt.addBinding(mc.i2.s, b))
            CompileError.printError(mc.i2.lineNumber, mc.i2.columnNumber,
                    String.format("Error: parameter %s is already defined.", mc.i2.s));

        scope = scope.parent;
        return null;
    }

    public Void visit (SimpleClassDecl cd) {
        int i;
        scope = scope.sub_table.get(cd.i.s);

        for(i = 0; i < cd.vl.size(); i++) {
            cd.vl.get(i).accept(this);
            scope.getBinding(cd.vl.get(i).i.s).offset = i;
        }

        ((ClassObject) root.getBinding(cd.i.s).type).size = i;

        for(i = 0; i < cd.ml.size(); i++)
            cd.ml.get(i).accept(this);

        scope = scope.parent;
        return null;
    }

    public Void visit (ExtendingClassDecl ecd) {
        int i;
        // Check if base class has been defined
        // TODO: Replace with check whether exists in table
        Binding b = scope.getBinding(ecd.j.s);
        if(b.type.isSame(Primitive.UNDEFINED))
            CompileError.printError(ecd.j.lineNumber, ecd.j.columnNumber,
                    String.format("Error: base class %s is undefined.", ecd.j.s));

        scope = scope.sub_table.get(ecd.i.s);

        for(i = 0; i < ecd.vl.size(); i++) {
            ecd.vl.get(i).accept(this);
            scope.getBinding(ecd.vl.get(i).i.s).offset = i;
        }

        ((ClassObject) root.getBinding(ecd.i.s).type).size = i;

        for(i = 0; i < ecd.ml.size(); i++)
            ecd.ml.get(i).accept(this);

        scope = scope.parent;
        return null;
    }

    public Void visit (VarDecl vd) {
        SymbolType t = ConvertType(vd.t, vd.i.s);

        scope.addBinding(vd.i.s, new Binding(scope.id, vd.i.s, t));

        /*SymbolTable nothing = null;
        scope.put(vd.i.s, nothing)*/
        return null;
    }

    public Void visit (MethodDecl md) {
        ArrayList<SymbolType> param_types = new ArrayList<SymbolType>();
        SymbolTable mt = new SymbolTable(scope, md.i.s);
        scope = mt;

        for(int i = 0; i < md.fl.size(); i++){
            md.fl.get(i).accept(this);
            param_types.add(scope.getBinding(md.fl.get(i).i.s).type);
        }

        for(int i = 0; i < md.vl.size(); i++) {
            md.vl.get(i).accept(this);
            scope.getBinding(md.vl.get(i).i.s).offset = i;
        }

        SymbolType t = ConvertType(md.t, md.i.s);
        Method m = new Method(md.i.s, t, param_types);
        scope = scope.parent;

        // TODO: Implement overloading by mangling names
        // with parameter types
        Binding b = new Binding(scope.id, m.id, m);
        if(!scope.addBinding(m.id, b) || !scope.addTable(m.id, mt))
            CompileError.printError(md.i.lineNumber, md.i.columnNumber,
                    String.format("Error: method %s is already defined.", md.i.s));

        return null;
    }

    public Void visit (Formal f) {
        SymbolType t = ConvertType(f.t, f.i.s);
        scope.addBinding(f.i.s, new Binding(scope.id, f.i.s, t));

        /*SymbolTable nothing = null;
        scope.put(vd.i.s, nothing)*/
        return null;
    }

    public Void visit (IdentifierType n) { return null; }
    public Void visit (IntArrayType n) { return null; }
    public Void visit (BooleanType n) { return null; }
    public Void visit (IntegerType n) { return null; }
    public Void visit (VoidType n) { return null; }
    public Void visit (Block n) { return null; }
    public Void visit (If n) { return null; }
    public Void visit (While n) { return null; }
    public Void visit (Print n) { return null; }
    public Void visit (Assign n) { return null; }
    public Void visit (ArrayAssign n) { return null; }
    public Void visit (And n) { return null; }
    public Void visit (LessThan n) { return null; }
    public Void visit (Plus n) { return null; }
    public Void visit (Minus n) { return null; }
    public Void visit (Times n) { return null; }
    public Void visit (ArrayLookup n) { return null; }
    public Void visit (ArrayLength n) { return null; }
    public Void visit (Call n) { return null; }
    public Void visit (IntegerLiteral n) { return null; }
    public Void visit (True n) { return null; }
    public Void visit (False n) { return null; }
    public Void visit (IdentifierExp n) { return null; }
    public Void visit (This n) { return null; }
    public Void visit (NewArray n) { return null; }
    public Void visit (NewObject n) { return null; }
    public Void visit (Not n) { return null; }
    public Void visit (Identifier n) { return null; }
}
