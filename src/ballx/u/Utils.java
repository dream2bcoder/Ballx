package ballx.u;

import ballx.o.ShapeContext;
import java.util.List;

/**
 *
 * @author Subhankar
 */
public class Utils {
    
    public static ShapeContext[] getArray(List<ShapeContext> list) {
        if(null != list && !list.isEmpty()) {
            ShapeContext[] contexts =  new ShapeContext[list.size()];
            return list.toArray(contexts);
        }
        
        return null;
    }
}
