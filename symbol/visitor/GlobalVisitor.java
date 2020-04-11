package symbol.visitor;

import syntax.*;
import symbol.Binding;
import symbol.SymbolTable;
import symbol.type.ClassObject;
import main.CompileError;

public class GlobalVisitor implements SyntaxTreeVisitor<Void> {
    public SymbolTable table;

    @Override
    public Void visit (Program p)  {
        table = new SymbolTable(null, "root");
        p.m.accept(this);

        for(int i = 0; i < p.cl.size(); i++)
            p.cl.get(i).accept(this);

        return null;
    }

    @Override
    public Void visit (MainClass m) {
        final SymbolTable ct = new SymbolTable(table, m.i1.s);
        final ClassObject c = new ClassObject(ct, m.i1.s, null);
        final Binding b = new Binding(table.id, m.i1.s, c);

        table.addBinding(m.i1.s, b);
        table.addTable(m.i1.s, ct);
        return null;
    }

    @Override
    public Void visit (SimpleClassDecl cd) {
        final SymbolTable ct = new SymbolTable(table, cd.i.s);
        final ClassObject c = new ClassObject(ct, cd.i.s, null);
        final Binding b = new Binding(table.id, cd.i.s, c);

        if(!table.addBinding(cd.i.s, b) || !table.addTable(cd.i.s, ct))
            CompileError.printError(cd.i.lineNumber, cd.i.columnNumber,
                    String.format("Error: class %s is already defined.", cd.i.s));

        return null;
    }

    @Override
    public Void visit (ExtendingClassDecl ec) {
        final SymbolTable ct = new SymbolTable(table, ec.i.s);
        final ClassObject c = new ClassObject(ct, ec.i.s, ec.j.s);
        final Binding b = new Binding(table.id, ec.i.s, c);

        if(!table.addBinding(ec.i.s, b) || !table.addTable(ec.i.s, ct))
            CompileError.printError(ec.i.lineNumber, ec.i.columnNumber,
                    String.format("Error: class %s is already defined.", ec.i.s));

        return null;
    }

    @Override
    public Void visit (VarDecl n) { return null; }
    @Override
    public Void visit (MethodDecl n) { return null; }
    @Override
    public Void visit (Formal n) { return null; }
    @Override
    public Void visit (IdentifierType n) { return null; }
    @Override
    public Void visit (IntArrayType n) { return null; }
    @Override
    public Void visit (BooleanType n) { return null; }
    @Override
    public Void visit (IntegerType n) { return null; }
    @Override
    public Void visit (VoidType n) { return null; }
    @Override
    public Void visit (Block n) { return null; }
    @Override
    public Void visit (If n) { return null; }
    @Override
    public Void visit (While n) { return null; }
    @Override
    public Void visit (Print n) { return null; }
    @Override
    public Void visit (Assign n) { return null; }
    @Override
    public Void visit (ArrayAssign n) { return null; }
    @Override
    public Void visit (And n) { return null; }
    @Override
    public Void visit (LessThan n) { return null; }
    @Override
    public Void visit (Plus n) { return null; }
    @Override
    public Void visit (Minus n) { return null; }
    @Override
    public Void visit (Times n) { return null; }
    @Override
    public Void visit (ArrayLookup n) { return null; }
    @Override
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
