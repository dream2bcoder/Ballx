package ballx.listener.impl;

import ballx.listener.IListener;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Subhankar
 */
public class ListenerContext {
    
    protected final List<IListener> listeners;

    public ListenerContext() {
        listeners = new ArrayList<>();
    }
    
    protected void addListener(IListener listener) {
        if (listeners.isEmpty() || !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    protected void removeListener(IListener listener) {
        if (!listeners.isEmpty() && listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }
}
