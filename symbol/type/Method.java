package symbol.type;
import java.util.ArrayList;
import symbol.type.SymbolType;

public class Method extends SymbolType {
    public String id;
    public SymbolType return_type;
    public ArrayList<SymbolType> param_types;

    public Method(String id, SymbolType return_type, ArrayList<SymbolType> param_types) {
        this.id = id;
        this.return_type = return_type;
        this.param_types = param_types;
    }

    @Override
    public boolean isSame(SymbolType t) {
        if(!(t instanceof Method))
            return false;
        if(!return_type.isSame(((Method) t).return_type))
            return false;
        if(param_types.size() != ((Method) t).param_types.size())
            return false;
        for(int i = 0; i < param_types.size(); i++)
            if(!param_types.get(i).isSame(((Method) t).param_types.get(i)))
                return false;
        return true;
    }

    @Override
    public boolean canBeAssignedBy(SymbolType t) {
        if(!(t instanceof Method))
            return false;
        if(!return_type.canBeAssignedBy(((Method) t).return_type))
            return false;
        if(param_types.size() != ((Method) t).param_types.size())
            return false;
        for(int i = 0; i < param_types.size(); i++)
            if(!param_types.get(i).isSame(((Method) t).param_types.get(i)))
                return false;
        return true;
    }
}