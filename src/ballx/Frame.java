package ballx;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import org.java.myutils.utils.DrawingUtils;

/**
 *
 * @author Subhankar
 */
public class Frame extends JFrame {
    
    DrawingUtils du;
    
    public Frame() {
        du = new DrawingUtils();
        initGUI();
    }

    private void initGUI() {
        Dimension msize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension csize = new Dimension(msize.width/1, msize.height/1);
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setUndecorated(true);
        
        this.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(), "NONE"));
        
        this.setLocation(du.relativeCenter(msize, csize));
        this.add(new Panel(csize), BorderLayout.CENTER);
        
    }
}
