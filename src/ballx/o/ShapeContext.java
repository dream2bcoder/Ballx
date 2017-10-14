package ballx.o;

import ballx.listener.CollisionListener;
import ballx.listener.DirectionChangeListener;
import ballx.listener.IListener;
import ballx.listener.impl.ListenerContext;
import ballx.u.Utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.java.myutils.utils.DrawingUtils;

/**
 *
 * @author Subhankar
 */
public class ShapeContext extends ListenerContext {

    public ShapeContextType contextType = ShapeContextType.TYPE_OBJECT;

    private final Dimension screensize;
    protected RenderingHints renderingHints;
    private Color backgroundColor;
    private int stepsize;

    protected int x, y;
    private int _x, _y;
    protected Dimension size;
    protected Direction direction;

    private boolean resizable;
    protected ShapeContext target;
    protected List<ShapeContext> neighbours;

    protected DrawingUtils drawingUtil;

    public ShapeContext(Dimension screensize) {
        this.screensize = screensize;
        size = new Dimension();
        drawingUtil = new DrawingUtils();
        direction = new Direction();
        neighbours = new ArrayList<>();
        setRenderingHints();
    }

    public void reset() {
        x = _x = 0;
        y = _y = 0;
        size.width = 0;
        size.height = 0;
    }

    protected void setRenderingHints() {
        renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    public void setNeighbours(List<ShapeContext> neighbours) {
        this.neighbours = neighbours;
    }

    public boolean isResizable() {
        return resizable;
    }

    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }

    public void addCollisionListener(CollisionListener collisionListener) {
        super.addListener(collisionListener);
    }

    public void removeCollisionListener(CollisionListener collisionListener) {
        super.removeListener(collisionListener);
    }

    public void addDirectionChangeListener(DirectionChangeListener directionChangeListener) {
        super.addListener(directionChangeListener);
    }

    public void removeDirectionChangeListener(DirectionChangeListener directionChangeListener) {
        super.removeListener(directionChangeListener);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setInitialLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public int getStepsize() {
        return stepsize;
    }

    public Shape getShape() {
        return null;
    }

    public Dimension getSize() {
        return size;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setsize(int width, int height) {
        size.width = width;
        size.height = height;
    }

    public void setStepsize(int stepsize) {
        this.stepsize = stepsize <= 0 ? 1 : stepsize;
    }

    public boolean contains(int x, int y) {
        return false;
    }

    public boolean contains(ShapeContext o) {
        return false;
    }

    protected void checkCollision() {
        
        
        
        if(!neighbours.isEmpty())
        {
            
            ShapeContext[] contexts = Utils.getArray(neighbours);
            
            for(int i=0;i<contexts.length;i++) {
                ShapeContext context = contexts[i];
                if (    context != null && context != this && 
                        context.getShape() != null && this.getShape() != null && 
                        drawingUtil.isIntersects(this.getShape(), context.getShape())) {

                    target = context;

                    if (context.contextType == ShapeContextType.TYPE_GROUP && ((Group) context).isRegistered(this)) {
                        target = null;
                    } else if (this.contextType == ShapeContextType.TYPE_GROUP && ((Group) this).isRegistered(context)) {
                        target = null;
                    }

                    return;
                }
            }
        }

        target = null;
    }

    protected void updateInnerComponent() {
        // override this method to update inner drawable component
    }

    public void resizeOnDrag(int dragX, int dragY) {

        if (!resizable) {
            return;
        }

        if (dragX < x) {
            size.width = _x - dragX;
            x = dragX;
        } else {
            size.width = dragX - x;
            _x = dragX;
        }

        if (dragY < y) {
            size.height = _y - dragY;
            y = dragY;
        } else {
            size.height = dragY - y;
            _y = dragY;
        }

        updateInnerComponent();
    }

    public boolean update() {
        return update(null);
    }

    public boolean update(ShapeContext group) {
        return update(stepsize, group);
    }

    public boolean update(int stepsize, ShapeContext group) {

        if (resizable) {
            return false;
        }

        boolean isRegistered = Group.isExist(group) && ((Group) group).isRegistered(this);

        if (x <= (isRegistered ? group.x : 0) || null != target && direction.horizontalDirection.equals(Direction.BACKWARD)) {
            direction.setHorizontalDirection(Direction.FORWARD);
        } else if (x + getSize().width >= (isRegistered ? group.x + group.getSize().width : screensize.width) || null != target && direction.horizontalDirection.equals(Direction.FORWARD)) {
            direction.setHorizontalDirection(Direction.BACKWARD);
        }

        if (y <= (isRegistered ? group.y : 0) || null != target && direction.verticalDirection.equals(Direction.UPWARD)) {
            direction.setVerticalDirection(Direction.DOWNWARD);
        } else if (y + getSize().height >= (isRegistered ? group.y + group.getSize().height : screensize.height) || null != target && direction.verticalDirection.equals(Direction.DOWNWARD)) {
            direction.setVerticalDirection(Direction.UPWARD);
        }

        updateLocation(stepsize);
        checkCollision();

        for (IListener l : listeners) {
            if (l != null) {
                if (direction.isDirectionChanged()) {
                    direction.setDirectionChanged(false);
                    if (l instanceof DirectionChangeListener) {
                        ((DirectionChangeListener) l).directionChanged(direction);
                    }
                }

                if (null != target) {
                    if (l instanceof CollisionListener) {
                        ((CollisionListener) l).onCollision(this, target);
                    }
                    if (l instanceof DirectionChangeListener) {
                        ((DirectionChangeListener) l).directionChangedOnCollision(direction);
                    }
                }
            }
        }

        return isRegistered;
    }

    protected void updateLocation(int stepsize) {

        switch (direction.horizontalDirection) {
            case Direction.BACKWARD:
                x -= stepsize;
                break;
            case Direction.FORWARD:
                x += stepsize;
        }

        switch (direction.verticalDirection) {
            case Direction.DOWNWARD:
                y += stepsize;
                break;
            case Direction.UPWARD:
                y -= stepsize;
        }

        updateInnerComponent();
    }

    public void draw(Graphics2D g) {
        g.setRenderingHints(renderingHints);
    }

    public static enum ShapeContextType {
        TYPE_OBJECT,
        TYPE_GROUP
    }
}
