package ballx;

import ballx.listener.CollisionListener;

import ballx.o.Ball;
import ballx.o.Group;
import ballx.o.ShapeContext;
import ballx.u.Utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JPanel;

import org.java.myutils.utils.AnimationUtils;
import org.java.myutils.utils.DrawingUtils;

/**
 *
 * @author Subhankar
 */
public class Panel extends JPanel implements Runnable {

    private final int balls = 5;

    Dimension size;
    DrawingUtils du;
    Thread th;

    boolean keyPaused = false;
    boolean paused = false;
    Ball selectedBall = null;
    int mx = 0, my = 0;
    
    ShapeContext target = null;

    List<ShapeContext> comps;

    public Panel(Dimension size) {
        this.size = size;
        comps = new ArrayList<>();
        du = new DrawingUtils();
        initBalls();
        animate();
        addListeners();
    }

    private void addListeners() {
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

                ShapeContext context;

                for (int i = 0; i < comps.size(); i++) {
                    context = comps.get(i);
                    if ( context.contains(me.getX(), me.getY())) {
                        if (!paused) {
                            paused = true;
                        }
                        target = context;
                        mx = target.getX() - me.getX();
                        my = target.getY() - me.getY();
                        return;
                    }
                }

                for (int i = 0; i < comps.size(); i++) {
                    context = comps.get(i);
                    if (context.contextType == ShapeContext.ShapeContextType.TYPE_GROUP) {
                        if (!((Group) context).isPersist()) {
                            comps.remove(i);
                            i--;
                        }
                    }
                }

                Group group = new Group(size);
                comps.add(group);
                group.setBeginPoint(me.getX(), me.getY());
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                if (paused && !keyPaused) {
                    paused = false;
                }

                if (null != target && target.contextType == ShapeContext.ShapeContextType.TYPE_OBJECT) {
                    for (ShapeContext context : comps) {
                        if (context != null && context.contextType == ShapeContext.ShapeContextType.TYPE_GROUP) {
                            Group group = (Group) context;
                            if (group.contains(target)) {
                                group.register(target);
                                break;
                            } else if (group.isRegistered(target)) {
                                group.unregister(target);
                                break;
                            }
                        }
                    }
                    target = null;
                }
            }

            @Override
            public void mouseClicked(MouseEvent me) {
//                keyPaused = !keyPaused;
//                paused = !paused;

                for (ShapeContext context : comps) {
                    if (context != null && context.contextType == ShapeContext.ShapeContextType.TYPE_GROUP) {
                        Group group = (Group) context;
                        if (!group.isPersist()) {
                            group.removeSelection();
                        }
                    }
                }
                repaint();
            }
        });

        addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseDragged(MouseEvent me) {
                if (target != null) {
                    for (int i = 0; i < comps.size(); i++) {
                        if (comps.get(i) == target) {
                            target.setInitialLocation(me.getX() + mx, me.getY() + my);
                            repaint();
                            return;
                        }
                    }
                }

                for (ShapeContext context : comps) {
                    if (context != null && context.contextType == ShapeContext.ShapeContextType.TYPE_GROUP) {
                        Group group = (Group) context;
                        if (!group.isPersist()) {
                            group.resizeOnDrag(me.getX(), me.getY());
                        }
                    }
                }

                repaint();
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
        ShapeContext[] acomps;
        while (true) {
            if (!paused) {
                acomps = Utils.getArray(comps);
                for (int i=0;null != acomps && i<acomps.length;i++) {
                    if (acomps[i] != null && acomps[i].contextType == ShapeContext.ShapeContextType.TYPE_OBJECT) {
                        
                        for (int k=0;k<acomps.length;k++) {
                            ShapeContext group = acomps[k];
                            if (group != null && ((Ball) acomps[i]).update(group)) {
                                break;
                            }
                        }
                    }
                }
                repaint();
                AnimationUtils.debouncingDelay(8);
            }
            AnimationUtils.debouncingDelay(1);
        }
    }

    @Override
    public void paint(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(new Color(22, 22, 22));
        g2.fillRect(0, 0, size.width, size.height);

        for (ShapeContext comp : comps) {
            if (null != comp) {
                comp.draw(g2);
            }
        }
    }

    private void initBalls() {
        Ball ball;
        for (int i = 0; i < balls; i++) {
            ball = new Ball(size);
            ball.setStepsize(1);
            ball.setBackgroundColor(du.getRandomColor());
            //ball[i].setStepsize(new Random().nextInt(ball.length) + new Random().nextInt(ball.length) - new Random().nextInt(ball.length));
            ball.setInitialLocation(new Random().nextInt(size.width - ball.getSize().width), new Random().nextInt(size.height - ball.getSize().height));
            //ball[i].addDirectionChangeListener(new B_DirectionListener(ball[i]));
            ball.addCollisionListener(new B_CollisionListener());
            comps.add(ball);
        }

        for (int i = 0; i < comps.size(); i++) {
            comps.get(i).setNeighbours(comps);
        }

    }

    class B_CollisionListener implements CollisionListener {

        int state = 0;

        @Override
        public void onCollision(ShapeContext source, ShapeContext target) {
            if (source instanceof Ball && target instanceof Ball) {
                Ball b1 = (Ball) source;
                Ball b2 = (Ball) target;

                int targetStepSize = b1.getStepsize();
                int sourceStepSize = b2.getStepsize();
                b2.setStepsize(sourceStepSize);
                b1.setStepsize(targetStepSize);

                Color bg = b2.getBackgroundColor();
                b2.setBackgroundColor(b1.getBackgroundColor());
                b1.setBackgroundColor(bg);
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
