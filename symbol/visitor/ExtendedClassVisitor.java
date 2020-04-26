package symbol.visitor;

import syntax.*;
import symbol.SymbolTable;
import symbol.type.SymbolType;
import symbol.type.Method;
import symbol.type.Primitive;
import symbol.type.ClassObject;
import main.CompileError;

public class ExtendedClassVisitor implements SyntaxTreeVisitor {
    public SymbolTable root;
    public SymbolTable scope;

    public ExtendedClassVisitor(SymbolTable root){
        this.root = root;
        scope = root;
    }

    public Void visit (Program p)  {
        for(int i = 0; i < p.cl.size(); i++)
            p.cl.get(i).accept(this);
        return null;
    }

    public Void visit (MainClass n) { return null;}
    public Void visit (SimpleClassDecl n) { return null; }

    // TODO: handle class that extends main
    public Void visit (ExtendingClassDecl ecd) {
        // Descend into this classes scope
        scope = scope.sub_table.get(ecd.i.s);

        // Get the scope and id for the base class
        SymbolTable base_scope = root.sub_table.get(ecd.j.s);
        String base_id = base_scope.id;

        if(base_scope == null) return null;

        // Iterate up through parent classes
        while(base_id != null){
            // Iterate through the base class' bindings and add them to this class
            for(String id : base_scope.table.keySet()){
                if(!scope.table.containsKey(id)){
                    scope.addBinding(id, base_scope.getBinding(id));
                    //scope.complete = true; // Mark this class as complete (What if parents have nothing?)
                    continue;
                }

                // Check to see if method declaration matches parent method declaration.
                SymbolType t1 = base_scope.getBinding(id).type;
                if(t1 instanceof Method) {
                    t1 = (Method) t1; // Necissary?
                    Method t2 = (Method) scope.getBinding(id).type;
                    if(!t1.canBeAssignedBy(t2)){
                        CompileError.printError(ecd.i.lineNumber, ecd.i.columnNumber,
                                String.format("Error: in class %s method %s has type mismatch with parent method.",
                                        scope.id, id));
                        scope.table.get(id).type = Primitive.UNDEFINED;
                        continue;
                    }
                }
            }

            // Update the size of this ClassObject and offsets of locals
            int parent_size = ((ClassObject) root.getBinding(base_id).type).size;
            ((ClassObject) root.getBinding(ecd.i.s).type).size += parent_size;
            for(VarDecl v : ecd.vl)
                scope.getBinding(v.i.s).offset += parent_size;

            // If the base scope has been fully updated break
            if(base_scope.complete == true) break;

            // Get the scope parent to the base class. If it does not exist, break.
            ClassObject parent_class = (ClassObject) root.table.get(base_id).type;
            if(parent_class.base == null) break;
            base_id = parent_class.base;
            base_scope = root.sub_table.get(parent_class.base);
        }


        // Return to parent scope
        scope.complete = true; // Mark this class as complete
        scope = scope.parent;
        return null;
    }

    public Void visit (VarDecl n) { return null; }
    public Void visit (MethodDecl n) { return null; }
    public Void visit (Formal n) { return null; }
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
