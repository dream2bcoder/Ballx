package ballx;

import javax.swing.SwingUtilities;

/**
 *
 * @author Subhankar
 */
public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                Frame frame = new Frame();
                frame.setVisible(true);
                frame.pack();
            }
        });
    }
}
