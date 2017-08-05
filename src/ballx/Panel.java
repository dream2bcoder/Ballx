package ballx;

import ballx.o.Ball;
import ballx.listener.CollisionListener;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.JPanel;

import org.java.myutils.utils.AnimationUtils;
import org.java.myutils.utils.DrawingUtils;

/**
 *
 * @author Subhankar
 */
public class Panel extends JPanel implements Runnable {
    
    Ball[] ball = new Ball[6];
    Dimension size;
    DrawingUtils du;
    Thread th;
    
    boolean keyPaused = false;
    boolean paused = false;
    Ball selectedBall = null;
    int mx = 0, my = 0;
    
    public Panel(Dimension size) {
        this.size = size;
        du = new DrawingUtils();
        initBalls();
        animate();
        
        addKeyListener(new KeyAdapter() {
            
            @Override
            public void keyReleased(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_SPACE) {
                    paused = !paused;
                    keyPaused = !keyPaused;
                }
            }
        });
        
        addMouseListener(new MouseAdapter() {
            
            @Override
            public void mousePressed(MouseEvent me) {
                
                for(int i=0;i<ball.length;i++) {
                    if(ball[i].contains(me.getX(), me.getY())) {
                        if(!paused) paused = true;
                        selectedBall = ball[i];
                        mx = selectedBall.getX() - me.getX();
                        my = selectedBall.getY() - me.getY();
                        break;
                    }
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent me) {
                if(paused && !keyPaused) paused = false;
                selectedBall = null;
            }
            
            @Override
            public void mouseClicked(MouseEvent me) {
                keyPaused = !keyPaused;
                paused = !paused;
            }
        });
        
        addMouseMotionListener(new MouseAdapter() {
            
            
            @Override
            public void mouseDragged(MouseEvent me) {
                if(selectedBall != null) {
                    for(int i=0;i<ball.length;i++) {
                        if(ball[i] == selectedBall) {
                            selectedBall.setInitialLocation(me.getX() + mx, me.getY() + my);
                            repaint();
                            break;
                        }
                    }
                }
            }
        });
        
        setFocusable(true);
    }
    
    private void animate() {
        th = new Thread(this);
        th.start();
    }
    
    @Override
    public Dimension getPreferredSize() {
        return size;
    }
    
    @Override
    public void run() {
        while (true) {
            if (!paused) {
                for (int i = 0; i < ball.length; i++) {
                    ball[i].update();
                }
                repaint();
                AnimationUtils.debouncingDelay(5);
            }
            AnimationUtils.debouncingDelay(1);
        }
    }
    
    @Override
    public void paint(Graphics g) {
        g.setColor(new Color(22, 22, 22));
        g.fillRect(0, 0, size.width, size.height);
        
        for (Ball b : ball) {
            if (null != b) {
                b.draw(g);
            }
        }
    }
    
    private void initBalls() {
        for (int i = 0; i < ball.length; i++) {
            ball[i] = new Ball(size);
            ball[i].setStepsize(i + 1);
            ball[i].setColor(du.getRandomColor());
            //ball[i].setStepsize(new Random().nextInt(ball.length) + new Random().nextInt(ball.length) - new Random().nextInt(ball.length));
            ball[i].setInitialLocation(new Random().nextInt(size.width - 70), new Random().nextInt(size.height - 70));
            //ball[i].addDirectionChangeListener(new B_DirectionListener(ball[i]));
            ball[i].addCollisionListener(new B_CollisionListener());
        }
        
        for (int i = 0; i < ball.length; i++) {
            ball[i].setNeighbourBalls(ball);
        }
        
    }
    
    class B_CollisionListener implements CollisionListener {
        
        int state = 0;
        
        @Override
        public void onCollision(Object source, Object target) {
            if(source instanceof Ball && target instanceof Ball) {
                Ball b1 = (Ball) source;
                Ball b2 = (Ball) target;
                
                int targetStepSize = b1.getStepsize();
                int sourceStepSize = b2.getStepsize();
                b2.setStepsize(sourceStepSize);
                b1.setStepsize(targetStepSize);

                Color tc = b2.getColor();
                b2.setColor(b1.getColor());
                b1.setColor(tc);
            }

//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    AppUtils.playSound();
//                }
//            }).start();
        }
        
    }
    
}
