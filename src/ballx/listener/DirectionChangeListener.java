package ballx.listener;

import ballx.o.Direction;

/**
 *
 * @author Subhankar
 */
public interface DirectionChangeListener extends IListener {
    void directionChanged(Direction direction);
    void directionChangedOnCollision(Direction direction);
}
