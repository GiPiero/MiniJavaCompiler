package symbol.type;

public abstract class SymbolType {
    public abstract boolean isSame(SymbolType t);
    public abstract boolean canBeAssignedBy(SymbolType t);
}