package ludumdare32;

import java.awt.AlphaComposite;
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
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class LudumDare32 extends JFrame {

    private boolean leftDown = false;
    private boolean rightDown = false;
    private boolean upDown = false;
    private boolean downDown = false;

    Player player = new Player(200, 250, 0);
    MyPanel panel;
    Camera camera;
    
    BufferedImage image;

    public LudumDare32() {
        Tile.loadTileSet("img/Spritesheet/roguelikeSheet_transparent.png", 16, 1);
        World.loadFromFile("levels/test.png");

        image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
        AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f);
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setComposite(composite);
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, 500, 500);

        g2d = image.createGraphics();

        for (int x = 0; x < 500; x++) {
            for (int y = 0; y < 500; y++) {
                ArrayList<Point> tiles = new ArrayList();
                int tx = (int) (x + 16) / 32;
                int ty = (int) (y + 16) / 32;
                try {
                    for (int x2 = tx - 1; x2 < tx + 2; x2++) {
                        for (int y2 = ty - 1; y2 < ty + 2; y2++) {
                            if (World.collisionMap[x2][y2]) {
                                tiles.add(new Point(x2 * 32, y2 * 32));
                            }
                        }
                    }
                } catch (Exception e) {
                }
                for (Point tile : tiles) {
                    double cx = x - tile.x;
                    double cy = y - tile.y;
                    cx = Math.max(0, Math.min(32, cx));
                    cy = Math.max(0, Math.min(32, cy));
                    Vector2D collisionVector = new Vector2D(new Point.Double(x - cx - tile.x, y - cy - tile.y));
                    if (collisionVector.point.x * collisionVector.point.x + collisionVector.point.y * collisionVector.point.y < 13 * 13) {
                        g2d.setColor(new Color(255, 255, 255, 128));
                        g2d.fillRect(x, y, 1, 1);
                    }
                }
            }
        }
        //
        setTitle("LudumDare32");

        panel = new MyPanel();
        camera = new Camera();
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

    class MyThread extends Thread {

        public MyThread() {
            start();
        }

        @Override
        public void run() {

            while (true) {
                player.changeVx((leftDown ? -player.acceleration : 0) + (rightDown ? player.acceleration : 0));
                player.changeVy((upDown ? -player.acceleration : 0) + (downDown ? player.acceleration : 0));
                player.move();
                player.checkCollision();

                camera.update(player, true);
                
                panel.repaint();
                try {
                    sleep((int)(1000/60d));
                } catch (Exception e) {
                }
            }

        }
    }

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
            g2.translate(camera.translateX, camera.translateY);
            World.paint(g2);
            player.paint(g2);
            g.drawImage(image, 0, 0, this);
            g2.translate(-camera.translateX, -camera.translateY);
        }

        private void addKeyBindings() {
            getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "exit");
            getActionMap().put("exit", exit());

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
