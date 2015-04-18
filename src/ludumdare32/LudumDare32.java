package ludumdare32;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class LudumDare32 extends JFrame {
    
    public LudumDare32() {
        Tile.loadTileSet("img/Spritesheet/roguelikeSheet_transparent.png", 16, 1);
        World.loadFromFile("levels/test.png");
        //
        setTitle("LudumDare32");

        MyPanel panel = new MyPanel();

        setContentPane(panel);
        getContentPane().setPreferredSize(new Dimension(800, 600));
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
        //
        
        
        
    }

    class MyPanel extends JPanel {

        Player player;
        
        public MyPanel() {
            addKeyBindings();

            MouseAdapter ma = mouseAdapter();
            addMouseListener(ma);
            addMouseMotionListener(ma);
            addMouseWheelListener(ma);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;
            World.paint(g2);
        }

        private void addKeyBindings() {
            getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "exit");
            getActionMap().put("exit", exit());
            getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "left");
            getActionMap().put("left", left());
            getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "right");
            getActionMap().put("right", right());
            getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), "up");
            getActionMap().put("up", up());
            getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "down");
            getActionMap().put("down", down());
            getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "jump");
            getActionMap().put("jump", jump());
        }

        private Action exit() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            };
        }

        private Action left() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    player.setVx(-1);
                }
            };
        }

        private Action right() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    player.setVx(1);
                }
            };
        }
        
        private Action up() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    player.setVx(-1);
                }
            };
        }

        private Action down() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    player.setVx(1);
                }
            };
        }

        private Action jump() {
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
