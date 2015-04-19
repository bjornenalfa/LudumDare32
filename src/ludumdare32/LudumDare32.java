package ludumdare32;

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

    private boolean changingWind = false;

    double windstartX;
    double windstartY;

    Player player = new Player(200, 250, 0);
    MyPanel panel;
    Camera camera;

//    BufferedImage image;
    public LudumDare32() {
        Tile.loadTileSet("img/Spritesheet/cloudy.png", 16, 1);
        World.loadFromFile("levels/test.png");

        Enemy foe = new Enemy(250, 250, 10, 13, 0.5);
        foe.setTarget(70, 550);
        foe.moveToTarget(true);

        foe = new Enemy(250, 250, 10, 13, 0.5);
        foe.setTarget(150, 500);
        foe.moveToTarget(true);

        foe = new Enemy(280, 250, 10, 13, 0.5);
        foe.setTarget(250, 550);
        foe.moveToTarget(true);

        foe = new Enemy(810, 250, 10, 13, 0.5);
        foe.setTarget(300, 600);
        foe.moveToTarget(true);

//        image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
//        AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f);
//        Graphics2D g2d = (Graphics2D) image.getGraphics();
//        g2d.setComposite(composite);
//        g2d.setColor(new Color(0, 0, 0, 0));
//        g2d.fillRect(0, 0, 500, 500);
//
//        g2d = image.createGraphics();
//
//        for (int x = 0; x < 500; x++) {
//            for (int y = 0; y < 500; y++) {
//                ArrayList<Point> tiles = new ArrayList();
//                int tx = (int) (x + 16) / 32;
//                int ty = (int) (y + 16) / 32;
//                try {
//                    for (int x2 = tx - 1; x2 < tx + 2; x2++) {
//                        for (int y2 = ty - 1; y2 < ty + 2; y2++) {
//                            if (World.collisionMap[x2][y2]) {
//                                tiles.add(new Point(x2 * 32, y2 * 32));
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                }
//                for (Point tile : tiles) {
//                    double cx = x - tile.x;
//                    double cy = y - tile.y;
//                    cx = Math.max(0, Math.min(32, cx));
//                    cy = Math.max(0, Math.min(32, cy));
//                    Vector2D collisionVector = new Vector2D(new Point.Double(x - cx - tile.x, y - cy - tile.y));
//                    if (collisionVector.point.x * collisionVector.point.x + collisionVector.point.y * collisionVector.point.y < 13 * 13) {
//                        g2d.setColor(new Color(255, 255, 255, 128));
//                        g2d.fillRect(x, y, 1, 1);
//                    }
//                }
//            }
//        }
        //
        setTitle("LudumDare32");

        panel = new MyPanel();
        camera = new Camera();
        MyThread thread = new MyThread();

        setContentPane(panel);
        getContentPane().setPreferredSize(new Dimension(800, 608));
        setResizable(false);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
                player.changeV((leftDown ? -1 : 0) + (rightDown ? 1 : 0),(upDown ? -1 : 0) + (downDown ? 1 : 0),player.acceleration);

                Weather.updateTransition();
                Wind.update();
                Character.updateCharacters();
                
                World.update(player);

                camera.update(player, true);

                panel.repaint();
                try {
                    sleep((int) (1000 / 60d));
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
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(0, 0, World.width * 32 + 100, World.width * 32 + 100);
            Wind.paint(g2);
            g2.translate(camera.translateX, camera.translateY);
            //g2.drawRect(0, 0, World.width*32+2000, World.width*32+2000);
            Weather.paintTransition(g2);
            World.paint(g2);
            Weather.paintTransitionClearClip(g2);
            
            if(changingWind) {
                g2.setColor(Color.RED);
                g2.drawLine((int) windstartX, (int) windstartY, (int) player.getX(), (int) player.getY());
            }
            Character.paintCharacters(g2);
            
            Weather.paintTransition2(g2);
            World.paint2(g2);
            Weather.paintTransitionClearClip(g2);
//            g2.drawImage(image, 0, 0, this);
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
                    Cloudy.activate(new Point.Double(player.getX(), player.getY()));
                }
            };
        }

        private Action sunny() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Sunny.activate(new Point.Double(player.getX(), player.getY()));
                }
            };
        }

        private Action rainy() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Rainy.activate(new Point.Double(player.getX(), player.getY()));
                }
            };
        }

        private Action snowy() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Snowy.activate(new Point.Double(player.getX(), player.getY()));
                }
            };
        }

        private Action windStart() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (changingWind) {
                        return;
                    }
                    changingWind = true;
                    windstartX = player.getX();
                    windstartY = player.getY();
                }
            };
        }

        private Action windEnd() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    changingWind = false;
                    double dx = player.getX() - windstartX;
                    double dy = player.getY() - windstartY;
                    Wind.direction = Math.atan2(dy, dx);
                    double length = Math.sqrt(dx * dx + dy * dy);
                    if (length < 10) {
                        Wind.power = 0;
                    } else if (length > 60) {
                        Wind.power = 0.5;
                    } else {
                        Wind.power = Math.sqrt(dx * dx + dy * dy) / 120;
                    }
                    System.out.println(length + " : " + Wind.power);
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
