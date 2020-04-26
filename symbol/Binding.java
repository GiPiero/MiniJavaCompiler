package symbol;

import symbol.type.SymbolType;

public class Binding{
    public String table_id;
    public String id;
    public SymbolType type;
    public int offset = 0;

    public Binding(String table_id, String id, SymbolType type) {
        this.id = id;
        this.type = type;
        this.table_id = table_id;
    }
}
