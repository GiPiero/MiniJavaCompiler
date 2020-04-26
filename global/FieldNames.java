package global;
import java.util.TreeMap;
import java.lang.reflect.Field;

public class FieldNames extends TreeMap<Integer,String>  {

    public FieldNames (String class_name) {
        try {
            Class<?> c = Class.forName(class_name);
            final Field [] f = c.getDeclaredFields();
            for (int i=0; i<f.length; i++) {
                if (f[i].getType()==int.class) put (f[i].getInt(null), f[i].getName());
            }
        } catch (Exception e) {
            e.printStackTrace (System.err);
        }
    }
}
