package ludumdare32mapeditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import static java.lang.Thread.sleep;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class LudumDare32MapEditor extends JFrame {
    
    private boolean leftDown = false;
    private boolean rightDown = false;
    private boolean upDown = false;
    private boolean downDown = false;

    MyPanel panel;
    static Camera camera;

    public LudumDare32MapEditor() {
        Tile.loadTileSet("../../../src/ludumdare/img/Spritesheet/cloudy.png", 16, 1); //funkar inte :(
        World.loadFromFile("levels/test.png");

        setTitle("LudumDare32 Map Editor");

        panel = new MyPanel();
        camera = new Camera();
        //MyThread thread = new MyThread();

        setContentPane(panel);
        getContentPane().setPreferredSize(new Dimension(800, 608));
        setResizable(false);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public class KeyHandler extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent k) {
            int key = k.getKeyCode();
            if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
                leftDown = true;
            } else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
                rightDown = true;
            } else if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
                upDown = true;
            } else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
                downDown = true;
            }
        }

        @Override
        public void keyReleased(KeyEvent k) {
            int key = k.getKeyCode();
            if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
                leftDown = false;
            } else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
                rightDown = false;
            } else if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
                upDown = false;
            } else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
                downDown = false;
            }
        }
    }

//    class MyThread extends Thread {
//
//        public MyThread() {
//            start();
//        }
//
//        @Override
//        public void run() {
//
//            while (true) {
//                panel.repaint();
//                try {
//                    sleep((int) (1000 / 60d));
//                } catch (Exception e) {
//                }
//            }
//
//        }
//    }

    class MyPanel extends JPanel {

        public MyPanel() {
            addKeyBindings();

            MouseAdapter ma = mouseAdapter();
            addMouseListener(ma);
            addMouseMotionListener(ma);
            addMouseWheelListener(ma);

            addKeyListener(new KeyHandler());

        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(0, 0, World.width * 32, World.width * 32);
            g2.translate(camera.translateX, camera.translateY);
            World.paint(g2);
            
            World.paint2(g2);
            g2.translate(-camera.translateX, -camera.translateY);
        }

        private void addKeyBindings() {
            getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "exit");
            getActionMap().put("exit", exit());

            getInputMap().put(KeyStroke.getKeyStroke("1"), "cloudy");
            getActionMap().put("cloudy", cloudy());

            getInputMap().put(KeyStroke.getKeyStroke("2"), "sunny");
            getActionMap().put("sunny", sunny());

            getInputMap().put(KeyStroke.getKeyStroke("3"), "rainy");
            getActionMap().put("rainy", rainy());

            getInputMap().put(KeyStroke.getKeyStroke("4"), "snowy");
            getActionMap().put("snowy", snowy());

            getInputMap().put(KeyStroke.getKeyStroke("released SHIFT"), "windEnd");
            getInputMap().put(KeyStroke.getKeyStroke("shift pressed SHIFT"), "windStart");
            getActionMap().put("windStart", windStart());
            getInputMap().put(KeyStroke.getKeyStroke("released SHIFT"), "windEnd");
            getActionMap().put("windEnd", windEnd());

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

        private Action cloudy() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                }
            };
        }

        private Action sunny() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                }
            };
        }

        private Action rainy() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                }
            };
        }

        private Action snowy() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                }
            };
        }

        private Action windStart() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                }
            };
        }

        private Action windEnd() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
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
        new LudumDare32MapEditor();
    }
}
