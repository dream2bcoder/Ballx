package ballx.listener;

import ballx.o.ShapeContext;

/**
 *
 * @author Subhankar
 */
public interface CollisionListener extends IListener {
    void onCollision(ShapeContext source, ShapeContext target);
}
