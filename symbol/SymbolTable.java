package symbol;
import java.util.Hashtable;
import symbol.type.Primitive;

public class SymbolTable {
    public String id;
    public Hashtable<String, Binding> table = new Hashtable<String, Binding>();
    public Hashtable<String, SymbolTable> sub_table = new Hashtable<String, SymbolTable>();
    public SymbolTable parent;
    public boolean complete = false;

    public SymbolTable(SymbolTable parent, String id) {
        this.parent = parent;
        this.id = id;
    }

    public boolean addBinding(String id, Binding b) {
        if(id == null || sub_table.containsKey(id)) return false;
        table.put(id, b);
        return true;
    }

    public boolean addTable(String id, SymbolTable st) {
        if(id == null || sub_table.containsKey(id)) return false;
        sub_table.put(id, st);
        return true;
    }

    public Binding getBinding(String id) {
        SymbolTable t = this;
        Binding b = null;

        do {
            b = t.table.get(id);
            t = t.parent;
        } while(b == null && t != null);

        // Redo this
        if(b == null) {
            b = new Binding(null, id, Primitive.UNDEFINED); // Should table_id be null?
            table.put(id, b);
        }

        return b;
    }
}