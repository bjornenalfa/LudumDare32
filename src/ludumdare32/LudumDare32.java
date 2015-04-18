package ludumdare32;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 *
 * @author bjornenalfa
 */
public class LudumDare32 extends JFrame {

    public LudumDare32() {
        setTitle("");

        MyPanel panel = new MyPanel();

        setContentPane(panel);
        getContentPane().setPreferredSize(new Dimension(800, 600));
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    class MyPanel extends JPanel {

        public MyPanel() {
            addKeyBindings();

            MouseAdapter ma = mouseAdapter();
            addMouseListener(ma);
            addMouseMotionListener(ma);
            addMouseWheelListener(ma);
        }

        @Override
        protected void paintComponent(Graphics g) {

        }

        private void addKeyBindings() {
            getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_0, 0), "keyBinding");
            getActionMap().put("keyBinding", keyBinding());
        }

        private Action keyBinding() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                }
            };
        }

        private MouseAdapter mouseAdapter() {
            return new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent me) {

                }
            };
        }
    }

    public static void main(String[] args) {
        new LudumDare32();
    }
}
