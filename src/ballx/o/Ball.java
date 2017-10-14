package ballx.o;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

/**
 *
 * @author Subhankar
 */
public class Ball extends ShapeContext {

    static int count = 0;
    
    private Font font;
    private Ellipse2D circle;
    private static final String chars = "SUBHANKAR";
    
    int radius = 101;
    String msg = "";

    public Ball(Dimension container) {
        super(container);
        initBall();
    }
    
    private void initBall() {
        setResizable(false);
        msg = "" + chars.charAt(count++);
        circle = new Ellipse2D.Double();
        font = new Font(Font.MONOSPACED, Font.BOLD, 68);
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }
    
    public void setRadius(int radius) {
        this.radius = radius;
        super.setsize(radius, radius);
    }

    @Override
    public Shape getShape() {
        return circle;
    }
    
    @Override
    public Dimension getSize() {
        return circle.getBounds().getSize();
    }
    
    @Override
    public boolean contains(int x, int y) {
        return circle.contains(x, y);
    }
    
    @Override
    protected void updateInnerComponent() {
        circle.setFrame(x, y, radius, radius);
    }
    
    @Override
    public void setInitialLocation(int x, int y) {
        super.setInitialLocation(x, y);
        circle.setFrame(x, y, radius, radius);
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        
        RadialGradientPaint rgp = new RadialGradientPaint((float)circle.getCenterX(), (float)circle.getCenterY(), radius/2, new float[]{0.7f, 1.0f}, new Color[]{this.getBackgroundColor(), new Color(110, 0, 0)});
        g.setPaint(rgp);
        g.fillOval(x, y, radius, radius);
        
        g.setColor(new Color(210, 0, 0));
        g.setFont(this.getFont());
        FontMetrics fm = g.getFontMetrics();
        g.drawString(msg, x + radius/2 - fm.stringWidth(msg)/2, y + radius/2 - fm.getHeight()/2 + fm.getAscent());
    }
}
