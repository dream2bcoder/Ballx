package ballx.o;

import ballx.listener.CollisionListener;
import ballx.listener.DirectionChangeListener;
import ballx.listener.IListener;
import ballx.listener.impl.ListenerContext;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.Calendar;

/**
 *
 * @author Subhankar
 */
public class Ball extends ListenerContext {

    private final Dimension size;
    private final Ellipse2D circle;
    
    private static final String chars = "SUBHA+";
    
    Point cc;
    int radius = 107;
    private int stepsize = 1;
    Direction direction;
    Color c = Color.GREEN;
    Ball targetedBall = null;
    Ball[] neighbourBalls = null;
    
    static int count = 0;
    String msg = "";
    long _time = 0;
    
    RenderingHints rh;

    public Ball(Dimension container) {
        size = container;
        cc = new Point();
//        msg = String.valueOf(++count);
        msg = "" + chars.charAt(count++);
        circle = new Ellipse2D.Double();
        direction = new Direction();
        _time = Calendar.getInstance().getTimeInMillis();
        
        rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    public void setNeighbourBalls(Ball[] neighbourBalls) {
        this.neighbourBalls = neighbourBalls;
    }

    public Dimension getSize() {
        return new Dimension(radius, radius);
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setColor(Color c) {
        this.c = c;
    }

    public Color getColor() {
        return c;
    }

    public Ellipse2D getBound() {
        return circle;
    }

    public void setStepsize(int stepsize) {
        this.stepsize = stepsize <= 0 ? 1 : stepsize;
    }

    public int getStepsize() {
        return stepsize;
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
    
    public void setInitialLocation(int x, int y) {
        cc.x = x;
        cc.y = y;
        circle.setFrame(cc.x, cc.y, radius, radius);
    }

    public void setInitialLocation(Point location) {
        cc.x = location.x;
        cc.y = location.y;
        circle.setFrame(cc.x, cc.y, radius, radius);
    }

    public void checkCollision() {
        for (int i = 0; null != neighbourBalls && i < neighbourBalls.length; i++) {
            if (this != neighbourBalls[i] && isIntersects(this.getBound(), neighbourBalls[i].getBound())) {
                targetedBall = neighbourBalls[i];
                return;
            }
        }

        targetedBall = null;
    }

    private static boolean isIntersects(Shape shape1, Shape shape2) {
        Area a = new Area(shape1);
        a.intersect(new Area(shape2));
        return !a.isEmpty();
    }

    public void update() {
        update(stepsize);
    }

    public void update(int stepsize) {
        if (cc.x <= 0 || null != targetedBall && direction.horizontalDirection.equals(Direction.BACKWARD)) {
            direction.setHorizontalDirection(Direction.FORWARD);
        } else if (cc.x + radius >= size.width || null != targetedBall && direction.horizontalDirection.equals(Direction.FORWARD)) {
            direction.setHorizontalDirection(Direction.BACKWARD);
        }

        if (cc.y <= 0 || null != targetedBall && direction.verticalDirection.equals(Direction.UPWARD)) {
            direction.setVerticalDirection(Direction.DOWNWARD);
        } else if (cc.y + radius >= size.height || null != targetedBall && direction.verticalDirection.equals(Direction.DOWNWARD)) {
            direction.setVerticalDirection(Direction.UPWARD);
        }

        updateLocation(stepsize);
        circle.setFrame(cc.x, cc.y, radius, radius);

        if(Calendar.getInstance().getTimeInMillis() - _time > 1500)
            checkCollision();

        for (IListener l : listeners) {
            if (l != null) {
                if (direction.isDirectionChanged()) {
                    direction.setDirectionChanged(false);
                    if(l instanceof DirectionChangeListener) {
                        ((DirectionChangeListener)l).directionChanged(direction);
                    }   
                }

                if (null != targetedBall) {
                    if(l instanceof CollisionListener) {
                        ((CollisionListener)l).onCollision(this, targetedBall);
                    }  
                    if(l instanceof DirectionChangeListener) {
                        ((DirectionChangeListener)l).directionChangedOnCollision(direction);
                    }
                }
            }
        }
    }
    
    public int getX() {
        return (int) circle.getX();
    }
    
    public int getY() {
        return (int) circle.getY();
    }
    
    public boolean contains(int x, int y) {
        return circle.contains(x, y);
    }

    private void updateLocation(int stepsize) {

        switch (direction.horizontalDirection) {
            case Direction.BACKWARD:
                cc.x -= stepsize;
                break;
            case Direction.FORWARD:
                cc.x += stepsize;
        }

        switch (direction.verticalDirection) {
            case Direction.DOWNWARD:
                cc.y += stepsize;
                break;
            case Direction.UPWARD:
                cc.y -= stepsize;
        }
    }

    public void draw(Graphics g2) {
        
        Graphics2D g = (Graphics2D) g2;
        g.setRenderingHints(rh);
        
        RadialGradientPaint rgp = new RadialGradientPaint((float)circle.getCenterX(), (float)circle.getCenterY(), radius/2, new float[]{0.7f, 1.0f}, new Color[]{c, new Color(110, 0, 0)});
        g.setPaint(rgp);
        g.fillOval(cc.x, cc.y, radius, radius);
        
        g.setColor(new Color(210, 0, 0));
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 80));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(msg, cc.x + radius/2 - fm.stringWidth(msg)/2, cc.y + radius/2 - fm.getHeight()/2 + fm.getAscent());
    }
}
