package symbol.type;

import symbol.Binding;
import symbol.SymbolTable;
import symbol.type.ClassObject;
import symbol.type.SymbolType;

public class ClassObject extends SymbolType {
    public int size = 0;
    public String base;
    public String id;
    public SymbolTable table;

    public ClassObject(SymbolTable table, String id, String base) {
        this.table = table;
        this.id = id;
        this.base = base;
    }

    @Override
    public boolean isSame(SymbolType t){
        if(!(t instanceof ClassObject)) return false;
        return id.equals(((ClassObject) t).id);
    }

    @Override
    public boolean canBeAssignedBy(SymbolType t) {
        if(!(t instanceof ClassObject)) return false;
        if(id.equals(((ClassObject) t).id)) return true;

        String base_id = ((ClassObject) t).base;
        while(base_id != null) {
            Binding b = table.parent.getBinding(base_id);
            ClassObject c = (ClassObject) b.type;
            if(base_id.equals(id)) return true;
            base_id = c.base;
        }
        return false;
    }
}