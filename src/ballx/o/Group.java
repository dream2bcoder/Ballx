package ballx.o;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Subhankar
 */
public final class Group extends ShapeContext {
    
    private boolean persist;
    private final List<ShapeContext> comps;
    private final Rectangle2D rectangle;
    
    public Group(Dimension screensize) {
        super(screensize);
        setResizable(true);
        comps = new ArrayList<>();
        rectangle = new Rectangle2D.Double();
        contextType = ShapeContextType.TYPE_GROUP;
        
    }

    public boolean isPersist() {
        return persist;
    }

    public void setPersist(boolean persist) {
        this.persist = persist;
    }
    
    public boolean isRegistered(ShapeContext o) {
        return !comps.isEmpty() && comps.contains(o);
    }
    
    public void register(ShapeContext o) {
        
        if(!comps.contains(o) && o.contextType == ShapeContextType.TYPE_OBJECT) {
            
            if(comps.isEmpty()) {
                persist = true;
                setResizable(false);
            }
            
            comps.add(o);
        }
    }
    
    public void unregister(ShapeContext o) {
        if(!comps.isEmpty() && comps.contains(o)) {
            comps.remove(o);
        }
        
        if(comps.isEmpty())
            persist = false;
    }
    
    public void removeSelection() {
        reset();
    }
    
    public void setBeginPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public Point getBeginPoint() {
        return new Point(x, y);
    }
    
    @Override
    public void setInitialLocation(int x, int y) {
        super.setInitialLocation(x, y);
        rectangle.setFrame(x, y, size.width, size.height);
    }
    
    @Override
    protected void updateInnerComponent() {
        rectangle.setFrame(x, y, size.width, size.height);
    }

    @Override
    public Shape getShape() {
        return rectangle;
    }
    
    @Override
    public Dimension getSize() {
        return rectangle.getBounds().getSize();
    }
    
    @Override
    public void draw(Graphics2D g) {
        
        if(persist)
            g.setColor(Color.GREEN);
        else
            g.setColor(Color.RED);
        
        g.drawRect(x, y, size.width, size.height);
    }
    
    @Override
    public boolean contains(int x, int y) {
        return rectangle.contains(x, y);
    }
    
    @Override
    public boolean contains(ShapeContext o) {
        return (    
            o.contextType == ShapeContextType.TYPE_OBJECT && 
            o.getX() >= x && o.getX() + o.getSize().width <= x + getSize().width && 
            o.getY() >= y && o.getY() + o.getSize().height <= y + getSize().height
        );
    }
    
    public static boolean isExist(ShapeContext group) {
        return (null != group && group.contextType == ShapeContextType.TYPE_GROUP && group.getSize().width > 0 && group.getSize().height > 0);
    }
}
