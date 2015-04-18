package ludumdare32;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class LudumDare32 extends JFrame {

    Player player = new Player(200, 200, 0);
    MyPanel panel;
    
    public LudumDare32() {
        Tile.loadTileSet("img/Spritesheet/roguelikeSheet_transparent.png", 16, 1);
        World.loadFromFile("levels/test.png");
        //
        setTitle("LudumDare32");

        panel = new MyPanel();
        MyThread thread = new MyThread();
        
        setContentPane(panel);
        getContentPane().setPreferredSize(new Dimension(800, 600));
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
        //

    }

    class MyThread extends Thread {

        public MyThread() {
            start();
        }

        @Override
        public void run() {

            while (true) {
                player.move();
                player.checkCollision();
                
                panel.repaint();
                try {
                    sleep(50);
                } catch (Exception e) {
                }
            }

        }
    }

    class MyPanel extends JPanel {

        private boolean leftDown = false;
        private boolean rightDown = false;
        private boolean upDown = false;
        private boolean downDown = false;

        public MyPanel() {
            addKeyBindings();

            MouseAdapter ma = mouseAdapter();
            addMouseListener(ma);
            addMouseMotionListener(ma);
            addMouseWheelListener(ma);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            World.paint(g2);
            player.paint(g2);
        }

        private void addKeyBindings() {
            getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "exit");
            getActionMap().put("exit", exit());

            getInputMap().put(KeyStroke.getKeyStroke("A"), "left");
            getActionMap().put("left", left());
            getInputMap().put(KeyStroke.getKeyStroke("released A"), "releaseLeft");
            getActionMap().put("releaseLeft", releaseLeft());

            getInputMap().put(KeyStroke.getKeyStroke("D"), "right");
            getActionMap().put("right", right());
            getInputMap().put(KeyStroke.getKeyStroke("released D"), "releaseRight");
            getActionMap().put("releaseRight", releaseRight());

            getInputMap().put(KeyStroke.getKeyStroke("W"), "up");
            getActionMap().put("up", up());
            getInputMap().put(KeyStroke.getKeyStroke("released W"), "releaseUp");
            getActionMap().put("releaseUp", releaseUp());

            getInputMap().put(KeyStroke.getKeyStroke("S"), "down");
            getActionMap().put("down", down());
            getInputMap().put(KeyStroke.getKeyStroke("released S"), "releaseDown");
            getActionMap().put("releaseDown", releaseDown());

            getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "jump");
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
                    if (rightDown == false) {
                        leftDown = true;
                        player.setVx(-1);
                    }
                }
            };
        }

        private Action releaseLeft() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (leftDown == true) {
                        leftDown = false;
                        player.setVx(0);
                    }
                }
            };
        }

        private Action right() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (leftDown == false) {
                        rightDown = true;
                        player.setVx(1);
                    }
                }
            };
        }

        private Action releaseRight() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (rightDown == true) {
                        rightDown = false;
                        player.setVx(0);
                    }
                }
            };
        }

        private Action up() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (downDown == false) {
                        upDown = true;
                        player.setVy(-1);
                    }
                }
            };
        }

        private Action releaseUp() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (upDown == true) {
                        upDown = false;
                        player.setVy(0);
                    }
                }
            };
        }

        private Action down() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (upDown == false) {
                        downDown = true;
                        player.setVy(1);
                    }
                }
            };
        }

        private Action releaseDown() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (downDown == true) {
                        downDown = false;
                        player.setVy(0);
                    }
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
