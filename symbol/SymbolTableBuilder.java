package symbol;

import syntax.Program;
import symbol.SymbolTable;
import symbol.visitor.*;

public class SymbolTableBuilder {
    public static SymbolTable buildSymbolTable(Program p) {
        GlobalVisitor gv = new GlobalVisitor();
        p.accept(gv);

        ClassVisitor cv = new ClassVisitor(gv.table);
        p.accept(cv);

        ExtendedClassVisitor ev = new ExtendedClassVisitor(cv.root);
        p.accept(ev);

        TypeVisitor tv = new TypeVisitor(ev.root);
        p.accept(tv);

        return tv.root;
    }
}