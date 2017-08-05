package ballx.listener;

/**
 *
 * @author Subhankar
 */
public interface CollisionListener extends IListener {
    void onCollision(Object source, Object target);
}
