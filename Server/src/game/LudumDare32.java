package game;

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
import server.Client;

public class LudumDare32 extends JFrame {

    static String SERVER_IP = "127.0.0.1";
    static int SERVER_PORT = 9010;
    static String CLIENT_ID = "ld33";

    private boolean leftDown = false;
    private boolean rightDown = false;
    private boolean upDown = false;
    private boolean downDown = false;

    private boolean changingWind = false;

    double windstartX;
    double windstartY;

    static Player player = new Player(655, 450, 0);
    MyPanel panel;
    static Camera camera;

    static Client client = new Client(SERVER_IP, SERVER_PORT, CLIENT_ID);
    static PlayerDataList players = new PlayerDataList();

    BufferedImage image;

    public LudumDare32() {
        Character.localPlayer = player;
        //Tile.loadTileSet("img/Spritesheet/cloudy.png", 16, 1);
        new Thread(client).start();
        World.setTileSet(Cloudy.tileSet);
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
                            if (World.collisionMap[x2][y2][Weather.current] != 0) {
                                tiles.add(new Point(x2 * 32, y2 * 32));
                            }
                        }
                    }
                } catch (Exception e) {
                }
                for (Point tile : tiles) {
                    byte collisionType = World.collisionMap[(tile.x / 32)][(tile.y / 32)][Weather.current];
                    double cx = x - tile.x;
                    double cy = y - tile.y;
                    if ((collisionType & 1) == 1) {// BIT 1 = BASE SQUARE COLLISION
                        cx = Math.max(0, Math.min(31, cx));
                        cy = Math.max(0, Math.min(31, cy));
                    }
                    if ((collisionType & 2) == 2) {// BIT 2 = TOP LEFT NO COLLISION
                        if (cx + cy < 32) {
                            // Ortogonal projektion av vektor från (0,32) till c på normaliserad vektor (1,-1)
                            cy = cy - 32;
                            double scalar = (cx / Math.sqrt(2) - cy / Math.sqrt(2)); // Skalär produkt mellan c och normaliserad vektor (1,-1)
                            cx = scalar / Math.sqrt(2); // c = length * normaliserad vektor (1,-1)
                            cy = -scalar / Math.sqrt(2);
                            cy = cy + 32;
                        }
                    }
                    if ((collisionType & 4) == 4) {// BIT 3 = TOP RIGHT NO COLLISION
                        if ((32 - cx) + cy < 32) {
                            // Ortogonal projektion av vektor från (0,0) till c på normaliserad vektor (1,1)
                            double scalar = (cx / Math.sqrt(2) + cy / Math.sqrt(2)); // Skalär produkt mellan c och normaliserad vektor (1,1)
                            cx = scalar / Math.sqrt(2); // c = length * normaliserad vektor (1,1)
                            cy = scalar / Math.sqrt(2);
                        }
                    }
                    if ((collisionType & 8) == 8) {// BIT 4 = BOTTOM LEFT NO COLLISION
                        if (cx + (32 - cy) < 32) {
                            // Ortogonal projektion av vektor från (0,0) till c på normaliserad vektor (1,1)
                            double scalar = (cx / Math.sqrt(2) + cy / Math.sqrt(2)); // Skalär produkt mellan c och normaliserad vektor (1,1)
                            cx = scalar / Math.sqrt(2); // c = length * normaliserad vektor (1,1)
                            cy = scalar / Math.sqrt(2);
                        }
                    }
                    if ((collisionType & 16) == 16) {// BIT 5 = BOTTOM RIGHT NO COLLISION
                        if ((32 - cx) + (32 - cy) < 32) {
                            // Ortogonal projektion av vektor från (0,32) till c på normaliserad vektor (1,-1)
                            cy = cy - 32;
                            double scalar = (cx / Math.sqrt(2) - cy / Math.sqrt(2)); // Skalär produkt mellan c och normaliserad vektor (1,-1)
                            cx = scalar / Math.sqrt(2); // c = length * normaliserad vektor (1,-1)
                            cy = -scalar / Math.sqrt(2);
                            cy = cy + 32;
                        }
                    }
                    if ((collisionType & 32) == 32) {// BIT 6 = LEFT EDGE NO COLLISION
                        if (cx < 1) {
                            cx = 100000;
                        }
                    }
                    if ((collisionType & 64) == 64) {// BIT 7 = RIGHT EDGE NO COLLISION
                        if (cy < 1) {
                            cy = 100000;
                        }
                    }
                    if ((collisionType & 128) == 128) {// BIT 8 = TOP EDGE NO COLLISION
                        if (cy < 1) {
                            cy = 100000;
                        }
                    }
                    Vector2D collisionVector = new Vector2D(new Point.Double(x - cx - tile.x, y - cy - tile.y));
                    double sqDist = collisionVector.point.x * collisionVector.point.x + collisionVector.point.y * collisionVector.point.y;
                    if (sqDist < 16 * 16) {
                        if (sqDist == 0) {
                            g2d.setColor(new Color(255, 255, 255, 32));
                        } else {
                            //int k = (int) (255*(Math.sqrt(sqDist)/(16)));
                            int k = (int) (255 * (sqDist / (16 * 16)));
                            g2d.setColor(new Color(255 - k, 255, 255, 255 - k));
                        }
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

            while (true) { // UPDATING =========================================================================================================
                PlayerDataList newPlayerDataList = client.getPlayerDataList();
                if (newPlayerDataList != null) {
                    players = newPlayerDataList;
                }
                player.changeV((leftDown ? -1 : 0) + (rightDown ? 1 : 0), (upDown ? -1 : 0) + (downDown ? 1 : 0), player.acceleration);

                Weather.updateTransition();
                Wind.update();
                Character.updateCharacters();

                World.update(player);
                Weather.update();

                if (Math.random() < 1.0 / 60.0) {
                    Particles.particleList.add(new WindParticle(Math.random() * World.pixelWidth, Math.random() * World.pixelHeight));
                }

                Particles.updateAll();

                camera.update(player, true);

                panel.repaint();

                client.sendPlayerData(new PlayerData((float) player.getX(), (float) player.getY()));
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
        protected void paintComponent(Graphics g) { // PAINTING =============================================================================================
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(0, 0, World.width * 32, World.width * 32);
            g2.translate(camera.translateX, camera.translateY);
            //g2.drawRect(0, 0, World.width*32+2000, World.width*32+2000);
            Weather.paintTransition(g2);
            World.paint(g2);
            Weather.transitionClearClip(g2);

            if (changingWind) {
                g2.setColor(Color.RED);
                g2.drawLine((int) windstartX, (int) windstartY, (int) player.getX(), (int) player.getY());
            }
            Character.paintCharacters(g2);
            for (int i = 0; i < players.list.length; i++) {
                new Player(players.list[i].x, players.list[i].y, 0).paint(g2);
            }
            Weather.paintTransition2(g2);
            World.paint2(g2);
            Particles.paintAll(g2);
            Weather.paint(g2);
            Weather.transitionClearClip(g2);
            Weather.paintOldWeather(g2);
            g2.drawImage(image, 0, 0, this);
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
                    double direction = Math.atan2(dy, dx);
                    double length = Math.sqrt(dx * dx + dy * dy);
                    double power = 0;
                    if (length < 10) {
                        power = 0;
                    } else if (length > 60) {
                        power = 0.5;
                    } else {
                        power = Math.sqrt(dx * dx + dy * dy) / 120;
                    }
                    Wind.change(direction, power);
                    System.out.println(length + " : " + power);
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
