package symbol.type;
import symbol.type.SymbolType;

public class Primitive extends SymbolType {
    public static final Primitive INT = new Primitive("int");
    public static final Primitive BOOLEAN = new Primitive("boolean");
    public static final Primitive VOID = new Primitive("void");
    public static final Primitive UNDEFINED = new Primitive("undefined"); // Get rid of this
    public static final Primitive INTARRAY = new Primitive("int []");
    public static final Primitive STRINGARRAY = new Primitive("String []");

    private String str;

    private Primitive(String str){ this.str = str; }

    @Override
    public boolean isSame(SymbolType t) { return t == this; }

    @Override
    public boolean canBeAssignedBy(SymbolType t) { return t == this; }
}